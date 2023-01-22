package com.gakk.noorlibrary.ui.fragments

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogNamazerDakBinding
import com.gakk.noorlibrary.databinding.FragmentNamazTimingBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.util.TimeFormtter.getBanglaMonthName
import com.gakk.noorlibrary.util.TimeFormtter.getNumberByLocale
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.github.eltohamy.materialhijricalendarview.CalendarDay
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor


internal class NamazTimingFragment : Fragment() {

    private var fromMalaysia: Boolean = false
    private lateinit var binding: FragmentNamazTimingBinding
    private lateinit var prayerTimeCalculator: PrayerTimeCalculator
    private lateinit var upCommingPrayer: UpCommingPrayer
    private var prayerHandler: Handler? = null
    private var prayerRunnable: Runnable? = null
    private lateinit var model: HomeViewModel
    private lateinit var modelLiterature: LiteratureViewModel
    private lateinit var repository: RestRepository
    private lateinit var locationHelper: LocationHelper
    private var ppTimes: ArrayList<List<Long>> = arrayListOf()
    private var listNamazerDak: MutableList<Literature> = arrayListOf()
    private lateinit var imageLoader: ImageLoader
    private lateinit var downloadScope: CoroutineScope

    lateinit var cal: Calendar
    var year = 0
    var month = 0
    var day = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_namaz_timing, container, false)

        locationHelper = LocationHelper(requireContext())
        prayerTimeCalculator = PrayerTimeCalculator(requireContext())
        upCommingPrayer = prayerTimeCalculator.getUpCommingPrayer()
        binding.upcommingPrayer = upCommingPrayer

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("Checkfun", "onViewCreated")

        binding.item = ImageFromOnline("ic_bg_namaz_timing.png")

        lifecycleScope.launch {

            cal = Calendar.getInstance()
            imageLoader = Coil.imageLoader(requireContext())
            if (!this@NamazTimingFragment::downloadScope.isInitialized) {
                downloadScope = CoroutineScope(SupervisorJob())
            }
            try {
                cal.time = Date()
                year = cal.get(Calendar.YEAR)
                month = cal.get(Calendar.MONTH)
                day = cal.get(Calendar.DATE)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@NamazTimingFragment, HomeViewModel.FACTORY(repository)
            ).get(HomeViewModel::class.java)

            modelLiterature = ViewModelProvider(
                this@NamazTimingFragment, LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            subscribeObserver()

            setNamazTime()

        }
        initPrayerTimeHandler()
    }


    private fun subscribeObserver() {
        modelLiterature.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Util.showProgressDialog(context, "", "")
                }
                Status.SUCCESS -> {

                    Util.hide()
                    listNamazerDak = it.data?.data!!

                    showNamazerDakDialog(upCommingPrayer.nextWaqtName)
                }
                Status.ERROR -> {
                    Util.hide()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initPrayerTimeHandler()
        Log.e("onResume", "Called")
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause", "Called")
    }

    override fun onStop() {
        super.onStop()
        removePrayerTimeHandler()
    }

    private fun setNamazTime() {
        binding.tvNextPrayer.setText(R.string.tct_next_prayer_robi)

        setDateTime()

        val fajrTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTimeCalculator.prayerTimesToday.getFajr()) + " " + getString(
                R.string.txt_am
            )
        val johurTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTimeCalculator.prayerTimesToday.getDuhr()) + " " + TimeFormtter.getDhuhrAmOrPm(
                prayerTimeCalculator.prayerTimesToday.getDuhr(), requireContext()
            )
        val asrTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTimeCalculator.prayerTimesToday.getAsr()) + " " + getString(
                R.string.txt_pm
            )
        val magribTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTimeCalculator.prayerTimesToday.getMagrib()) + " " + getString(
                R.string.txt_pm
            )
        val eshaTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTimeCalculator.prayerTimesToday.getIsha()) + " " + getString(
                R.string.txt_pm
            )



        when (AppPreference.language) {
            LAN_BANGLA -> {
                binding.tvFajrTime.text = fajrTxt.getNumberInBangla()
                binding.tvJohurTime.text = johurTxt.getNumberInBangla()
                binding.tvAsrTime.text = asrTxt.getNumberInBangla()
                binding.tvMagribTime.text = magribTxt.getNumberInBangla()
                binding.tvEshaTime.text = eshaTxt.getNumberInBangla()
            }

            else -> {
                binding.tvFajrTime.text = fajrTxt
                binding.tvJohurTime.text = johurTxt
                binding.tvAsrTime.text = asrTxt
                binding.tvMagribTime.text = magribTxt
                binding.tvEshaTime.text = eshaTxt
            }
        }


        binding.ivNamazDak.handleClickEvent {
            modelLiterature.loadImageBasedLiteratureListBySubCategory(
                getString(R.string.namaz_dak_id), "undefined", "1"
            )
        }

    }

    private fun showNamazerDakDialog(waqtName: String) {
        val customDialog = MaterialAlertDialogBuilder(
            requireActivity(), R.style.MaterialAlertDialog_rounded
        )
        val binding: DialogNamazerDakBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_namazer_dak, null, false
        )


        val dialogView: View = binding.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        var shareItem: Literature? = null

        for (i in listNamazerDak) {

            if (i.title?.trim().equals(waqtName)) {

                binding.literature = i
                shareItem = i
                break
            }
        }


        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(true)
        alertDialog.show()

        binding.imgClose.handleClickEvent {
            alertDialog.dismiss()
        }

        binding.btnShare.handleClickEvent {
             getBitmapFromUrl(shareItem?.fullImageUrl!!)

            /* lifecycleScope.launch {
                 getBitmapFromUrlX(shareItem?.fullImageUrl!!, requireActivity())
             }*/

            alertDialog.dismiss()
        }
    }

    fun getBitmapFromUrl(bitmapURL: String?) {
        downloadScope.launch {
            context.let {
                val request = ImageRequest.Builder(requireContext()).data(bitmapURL).build()
                try {
                    val downloadedBitmap =
                        (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
                    // Save the bitmap to internal storage and get uri
                    val uri = downloadedBitmap.saveToInternalStorage(requireContext())

                    // Finally, share the internal storage saved bitmap
                    activity?.shareCacheDirBitmap(uri)
                } catch (e: Exception) {
                }
            }
        }

    }

    private fun updateTime() {
        val minute = TimeFormtter.getCurrentTime() + " "
        binding.layoutHeader.tvMinute.text = minute
    }


    private fun setDateTime() {

        var dateTxt = ""

        val dayofmonth = decrementDateByOne(Date())
        dateTxt = (getNumberByLocale(day.toString()) + " " + context?.let {
            getBanglaMonthName(month, it)
        } + " " + getNumberByLocale(year.toString()) + "  •  " + getNumberByLocale(
            (CalendarDay.from(dayofmonth).getDay()).toString()
        ) + " " + requireContext().resources.getStringArray(R.array.custom_months)[CalendarDay.from(
            dayofmonth
        ).getMonth()] + " " + getNumberByLocale(CalendarDay.from(Date()).getYear().toString()))



        binding.layoutHeader.tvDateHeader.text = dateTxt

        updateTime()

        binding.layoutHeader.tvGreeting.text = getGreetingMessage()
    }


    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> context?.getString(R.string.text_good_morning).toString()
            in 12..15 -> context?.getString(R.string.text_good_afternoon).toString()
            in 16..20 -> context?.getString(R.string.text_good_evening).toString()
            in 21..23 -> context?.getString(R.string.text_good_night).toString()
            else -> context?.getString(R.string.text_good_afternoon).toString()
        }
    }

    fun setBg(nextWaqt: String) {
        binding.rlFajr.background = null
        binding.rlJohur.background = null
        binding.rlAsr.background = null
        binding.rlMagrib.background = null
        binding.rlEsha.background = null
        when (nextWaqt) {
            context?.getString(R.string.txt_fajr) -> {
                binding.rlEsha.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_area_bg_namaz_row, null
                )
            }
            context?.getString(R.string.txt_johr) -> {
                binding.rlFajr.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_area_bg_namaz_row, null
                )
            }
            context?.getString(R.string.txt_asr) -> {
                binding.rlJohur.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_area_bg_namaz_row, null
                )
            }
            context?.getString(R.string.txt_magrib) -> {
                binding.rlAsr.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_area_bg_namaz_row, null
                )
            }
            context?.getString(R.string.txt_esha) -> {
                binding.rlMagrib.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_area_bg_namaz_row, null
                )
            }
        }

    }


    private fun initPrayerTimeHandler() {

        prayerHandler = Handler()
        prayerRunnable = object : Runnable {
            override fun run() {

                updateTime()

                upCommingPrayer = prayerTimeCalculator.getUpCommingPrayer()

                setBg(upCommingPrayer.nextWaqtName)


                try {
                    binding.upcommingPrayer = upCommingPrayer
                    binding.tvTimeLeft.text = getNumberByLocale(upCommingPrayer.timeLeft)

                    val currentWaqtStartingTime = when (upCommingPrayer.nextWaqtName) {
                        context?.resources?.getString(R.string.txt_fajr) -> TimeFormtter.milliSecondsFromTimeStringV5(
                            upCommingPrayer.currentWaqtStartingTime
                        )
                        else -> TimeFormtter.milliSecondsFromTimeStringV3(upCommingPrayer.currentWaqtStartingTime)
                    }
                    val upCommingWaqtStartingTime =
                        when (prayerTimeCalculator.allWaqtOverForTOday) {
                            true -> TimeFormtter.milliSecondsFromTimeStringV4(upCommingPrayer.nextWaqtTime)
                            false -> TimeFormtter.milliSecondsFromTimeStringV3(upCommingPrayer.nextWaqtTime)
                        }


                    val maxTimeDifference = upCommingWaqtStartingTime - currentWaqtStartingTime

                    val currentTimeDifference =
                        upCommingWaqtStartingTime - System.currentTimeMillis()


                    val percentage =
                        floor(100.00f - ((currentTimeDifference.toDouble() / maxTimeDifference) * 100))


                    binding.progressCircular.progress = percentage.toInt()

                } catch (e: Exception) {
                    Log.e("Checkprogress", "Time Left :${e.localizedMessage}")
                }


                prayerHandler?.postDelayed(this, 1000)
            }

        }

        prayerHandler?.post(prayerRunnable!!)
    }

    fun removePrayerTimeHandler() {
        try {
            prayerHandler?.removeCallbacks(prayerRunnable!!)
            prayerRunnable = null
            prayerHandler = null
        } catch (e: Exception) {

        }

    }
}