package com.gakk.noorlibrary.ui.fragments.azan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentAzanBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.ui.fragments.azan.azan_schedular.SalaatAlarmReceiver
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.util.TimeFormtter.getBanglaMonthName
import com.gakk.noorlibrary.util.TimeFormtter.getBanglaWeekName
import com.gakk.noorlibrary.util.TimeFormtter.getNumberByLocale
import com.gakk.noorlibrary.viewModel.AzanViewModel
import com.github.eltohamy.materialhijricalendarview.CalendarDay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/3/2021, Mon
 */

private const val TAG = "AzanFragment_Debug"

internal class AzanFragment : Fragment(), View.OnClickListener {

    private lateinit var todayDate: Date
    private lateinit var binding: FragmentAzanBinding
    private var mCallback: DetailsCallBack? = null
    private lateinit var viewmodel: AzanViewModel
    private lateinit var repository: RestRepository

    private lateinit var locationHelper: LocationHelper

    private lateinit var prayerTimeCalculator: PrayerTimeCalculator
    private lateinit var upCommingPrayer: UpCommingPrayer

    private var fromMalaysia = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AzanFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_azan,
                container,
                false
            )


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.item = ImageFromOnline("azan_header_bg.png")


        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewmodel = ViewModelProvider(
                this@AzanFragment,
                AzanViewModel.FACTORY(repository)
            ).get(AzanViewModel::class.java)

            locationHelper = LocationHelper(requireContext())

            mCallback?.setToolBarTitle(resources.getString(R.string.cat_azan))


            subscribeObserver()

            updateCurrentTimeView()

            initClockAndViews()

            prayerTimeCalculator = PrayerTimeCalculator(requireContext())

            viewmodel.startTimer()

            updateDate(todayDate)

            checkAlarmAlreadySet()
        }
    }

    private fun checkAlarmAlreadySet() {
        val fajrAlarm = AppPreference.getAlarmForAzan(AppPreference.IS_FAJR_ALARM_SET)
        binding.alarmImgFajr.isSelected = fajrAlarm

        val johurAlarm = AppPreference.getAlarmForAzan(AppPreference.IS_DHUHR_ALARM_SET)
        binding.alarmImgJohur.isSelected = johurAlarm

        val asrAlarm = AppPreference.getAlarmForAzan(AppPreference.IS_ASR_ALARM_SET)
        binding.alarmImgAsr.isSelected = asrAlarm

        val magribAlarm = AppPreference.getAlarmForAzan(AppPreference.IS_MAGHRIB_ALARM_SET)
        binding.alarmImgMagrib.isSelected = magribAlarm

        val eshaAlarm = AppPreference.getAlarmForAzan(AppPreference.IS_ISHA_ALARM_SET)
        binding.alarmImgEsha.isSelected = eshaAlarm
    }

    private fun subscribeObserver() {
        viewmodel.timerLiveData.observe(viewLifecycleOwner) {
            it?.let {
                upCommingPrayer = prayerTimeCalculator.getUpCommingPrayer()
                updateCurrentTimeView()
                updateViews()
            }
        }
    }

    private fun initClockAndViews() {
        todayDate = Date()

        val calendar: Calendar = Calendar.getInstance()

        val dp =
            resources.getDimension(R.dimen.clock_face_size) / resources.displayMetrics.density


        binding.clockLayout.clock.setCalendar(calendar)
            .setDiameterInDp(dp)
            .setOpacity(1.0f)
            .setShowSeconds(true).color =
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        updateAlarmStatus()

        binding.rlFajr.setOnClickListener(this)
        binding.rlJohur.setOnClickListener(this)
        binding.rlAsr.setOnClickListener(this)
        binding.rlMagrib.setOnClickListener(this)
        binding.rlEsha.setOnClickListener(this)

        binding.nextDate.setOnClickListener(this)
        binding.prevDate.setOnClickListener(this)
    }

    fun updateCurrentTimeView() {
        val minute = TimeFormtter.getCurrentTime() + " "

        binding.currentTimeValTv.text = minute
    }

    private fun setNamazTime(todayDate: Date) {

        val prayerTime = prayerTimeCalculator.getPrayerTimeByDate(todayDate)

        Log.d(TAG, "setNamazTime: ${prayerTime.getFajr()} ${prayerTime.getAsr()}")
        val fajrTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTime.getFajr()) + " " + getString(
                R.string.txt_am
            )
        val johurTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTime.getDuhr()) + " " +
                    TimeFormtter.getDhuhrAmOrPm(
                        prayerTime.getDuhr(),
                        requireContext()
                    )
        val asrTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTime.getAsr()) + " " + getString(
                R.string.txt_pm
            )
        val magribTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTime.getMagrib()) + " " + getString(
                R.string.txt_pm
            )
        val eshaTxt =
            TimeFormtter.getFormattedTimeHourMinute(prayerTime.getIsha()) + " " + getString(
                R.string.txt_pm
            )
        Log.e("location", "" + prayerTime.getDuhr())


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
    }


    private fun updateViews() {
        setBg(upCommingPrayer.nextWaqtName)

        upCommingPrayer.nextWaqtTime

        try {
            binding.nextAzanValTv.text =
                getNumberByLocale(upCommingPrayer.timeLeft)

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


            val maxTimeDifference =
                upCommingWaqtStartingTime - currentWaqtStartingTime

            val currentTimeDifference =
                upCommingWaqtStartingTime - System.currentTimeMillis()


            val percentage =
                floor(100.00f - ((currentTimeDifference.toDouble() / maxTimeDifference) * 100))

            binding.clockLayout.progressBar.progress = percentage.toInt()

        } catch (e: Exception) {
            Log.e("Checkprogress", "Time Left :${e.localizedMessage}")
        }
    }


    private fun setBg(nextWaqt: String) {
        binding.rlFajr.background = null
        binding.rlJohur.background = null
        binding.rlAsr.background = null
        binding.rlMagrib.background = null
        binding.rlEsha.background = null


        when (nextWaqt) {
            context?.getString(R.string.txt_fajr) -> {
                binding.rlEsha.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.next_prayer_row
                    )
                )
            }
            context?.getString(R.string.txt_johr) -> {
                binding.rlFajr.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.next_prayer_row
                    )
                )
            }
            context?.getString(R.string.txt_asr) -> {
                binding.rlJohur.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.next_prayer_row
                    )
                )
            }
            context?.getString(R.string.txt_magrib) -> {
                binding.rlAsr.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.next_prayer_row
                    )
                )
            }
            context?.getString(R.string.txt_esha) -> {
                binding.rlMagrib.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.next_prayer_row
                    )
                )
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.rlFajr.id -> {
                binding.alarmImgFajr.isSelected = !binding.alarmImgFajr.isSelected
                AppPreference.setAlarmForAzan(
                    binding.alarmImgFajr.isSelected,
                    AppPreference.IS_FAJR_ALARM_SET
                )

                updateAlarmStatus()
            }

            binding.rlJohur.id -> {
                binding.alarmImgJohur.isSelected = !binding.alarmImgJohur.isSelected
                AppPreference.setAlarmForAzan(
                    binding.alarmImgJohur.isSelected,
                    AppPreference.IS_DHUHR_ALARM_SET
                )
                updateAlarmStatus()

            }

            binding.rlAsr.id -> {
                binding.alarmImgAsr.isSelected = !binding.alarmImgAsr.isSelected
                AppPreference.setAlarmForAzan(
                    binding.alarmImgAsr.isSelected,
                    AppPreference.IS_ASR_ALARM_SET
                )
                updateAlarmStatus()
            }

            binding.rlMagrib.id -> {
                binding.alarmImgMagrib.isSelected = !binding.alarmImgMagrib.isSelected
                AppPreference.setAlarmForAzan(
                    binding.alarmImgMagrib.isSelected,
                    AppPreference.IS_MAGHRIB_ALARM_SET
                )
                updateAlarmStatus()
            }

            binding.rlEsha.id -> {
                binding.alarmImgEsha.isSelected = !binding.alarmImgEsha.isSelected
                AppPreference.setAlarmForAzan(
                    binding.alarmImgEsha.isSelected,
                    AppPreference.IS_ISHA_ALARM_SET
                )
                updateAlarmStatus()
            }


            binding.nextDate.id -> {
                nextPrevious(1)
            }

            binding.prevDate.id -> {
                nextPrevious(2)
            }

        }
    }

    private fun updateAlarmStatus() {
        val sar = SalaatAlarmReceiver()
        sar.cancelAlarm(requireContext())
        sar.setAlarm(requireContext())
    }

    private fun nextPrevious(i: Int) {
        if (i == 1) {
            todayDate = TimeFormtter.incrementDateByOne(todayDate)
        } else if (i == 2) {
            todayDate = TimeFormtter.decrementDateByOne(todayDate)
        }
        updateDate(todayDate)
    }

    private fun updateDate(date: Date) {
        val cal = Calendar.getInstance()
        cal.time = date
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val weekIndex = cal.get(7) - 1
        //update islamic date
        binding.arabicDateTv.text = (
                getNumberByLocale(
                    (CalendarDay.from(date).getDay() - 1).toString() + " "
                            + resources.getStringArray(R.array.custom_months)[CalendarDay.from(
                        date
                    )
                        .month] + " "
                            + getNumberByLocale(
                        java.lang.String.valueOf(
                            CalendarDay.from(date).year
                        )
                    )
                )
                )


        //update english date
        binding.engDateTv.text =
            (getBanglaWeekName(weekIndex, requireContext())
                .toString() + ", " + getNumberByLocale(day.toString()) + " " + getBanglaMonthName(
                month,
                requireContext()
            ) + " " + getNumberByLocale(year.toString()))


        setNamazTime(date)
    }

}