package com.gakk.noorlibrary.ui.fragments.tabs

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogTasbihResetBinding
import com.gakk.noorlibrary.databinding.FragmentHomeBinding
import com.gakk.noorlibrary.extralib.cardstackview.CardStackListener
import com.gakk.noorlibrary.extralib.cardstackview.Direction
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.model.billboard.Data
import com.gakk.noorlibrary.model.tracker.SalahStatus
import com.gakk.noorlibrary.ui.adapter.HomeFragmentAdapter
import com.gakk.noorlibrary.ui.fragments.CountControl
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.NinetyNineNamesOfAllahViewModel
import com.gakk.noorlibrary.viewModel.TrackerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class HomeFragment : Fragment(), BillboardItemControl, HomeCellItemControl, CardStackListener,
    CountControl {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mCallback: MainCallback
    private lateinit var repository: RestRepository
    private lateinit var model: HomeViewModel
    private lateinit var biilboradList: List<Data>
    private lateinit var prayerTimeCalculator: PrayerTimeCalculator
    private lateinit var modelTracker: TrackerViewModel
    private lateinit var modelAllahNames: NinetyNineNamesOfAllahViewModel
    private var fromMonth: Date? = null
    private lateinit var toMonth: String
    private val dateFormatForDisplaying =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private lateinit var adapter: HomeFragmentAdapter
    var prayerDataList: List<com.gakk.noorlibrary.model.tracker.Data>? = null
    private lateinit var upCommingPrayer: UpCommingPrayer
    private var fromMalaysia = false
    private var mAllahNamesList: List<com.gakk.noorlibrary.model.names.Data> = mutableListOf()
    private lateinit var playerControl: MediaPlayerControl
    private var mSelectedIndex: Int = 0
    private lateinit var mNameInfo: com.gakk.noorlibrary.model.names.Data
    private var soundOff = false
    private var localcount = 0
    private var totalCount = 0
    private var userSelectCount = 33
    private var sound: Boolean = true
    private var mFlag1 = 0
    private lateinit var mp: MediaPlayer
    var selectedItem = "0"
    private lateinit var dua: Array<String>
    private var duaIndex: Array<String> = arrayOf("0", "1", "2", "3", "4", "5", "6", "7")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCallback = (requireActivity() as MainCallback)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prayerTimeCalculator = PrayerTimeCalculator(requireContext())
        setPrayerWaqt()

        lifecycleScope.launch {

            playerControl = MediaPlayerControl()
            mp = MediaPlayer.create(context, R.raw.second)

            dua =
                context?.resources?.getStringArray(R.array.tasbih_duas) as Array<String>


            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HomeFragment,
                HomeViewModel.FACTORY(repository)
            ).get(HomeViewModel::class.java)

            modelTracker = ViewModelProvider(
                this@HomeFragment,
                TrackerViewModel.FACTORY(repository)
            ).get(TrackerViewModel::class.java)

            modelAllahNames = ViewModelProvider(
                this@HomeFragment,
                NinetyNineNamesOfAllahViewModel.FACTORY(repository)
            ).get(NinetyNineNamesOfAllahViewModel::class.java)


            subscribeObserver()

            fromMonth = Util.getFirstDateOfMonth(Date())

            toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))

            if (isNetworkConnected(requireContext())) {
                model.getBillboradData()
            } else {
                binding.noInternetLayout.root.visibility = View.VISIBLE
            }

            binding.noInternetLayout.btnRetry.handleClickEvent {
                model.getBillboradData()
            }
        }
    }

    private fun subscribeObserver() {
        model.billboardResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                    binding.noInternetLayout.root.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    when (it.data?.status) {
                        200 -> {
                            biilboradList = it.data.data
                            modelAllahNames.loadNamesOfAllah()
                        }
                        else -> {
                            binding.noDataLayout.root.visibility = View.VISIBLE
                        }
                    }

                    if (binding.noInternetLayout.root.isVisible) {
                        binding.noInternetLayout.root.visibility = View.GONE
                    }

                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                    binding.noInternetLayout.root.visibility = View.VISIBLE
                }
            }
        }

        modelAllahNames.nineNamesOfAllahData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    mAllahNamesList = it.data?.data!!
                    model.getHomeData()
                }

                Status.ERROR -> {

                }
            }
        }
        model.homeResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d("home", "loading")
                }

                Status.SUCCESS -> {
                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            //have to configure only for robi
                            var homeList: MutableList<com.gakk.noorlibrary.model.home.Data> =
                                mutableListOf()
                            homeList.clear()
                            homeList =
                                it.data.data as MutableList<com.gakk.noorlibrary.model.home.Data>

                            adapter = HomeFragmentAdapter(
                                homeList,
                                mAllahNamesList,
                                mCallback,
                                this@HomeFragment,
                                prayerTimeCalculator,
                                this@HomeFragment,
                                this@HomeFragment,
                                this
                            )

                            binding.homeRecycle.adapter = adapter
                            adapter.nextWaqt = upCommingPrayer.nextWaqtNameTracker

                            modelTracker.loadAllPrayerData(
                                dateFormatForDisplaying.format(Date()),
                                dateFormatForDisplaying.format(Date())
                            )

                        }
                        STATUS_NO_DATA -> {
                            binding.noDataLayout.item = ImageFromOnline("bg_no_data.png")
                            binding.noDataLayout.root.visibility = View.VISIBLE
                        }
                    }

                    binding.progressLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }


        modelTracker.prayerListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d("Tracker", "loading")
                }

                Status.SUCCESS -> {
                    when (it.data?.status) {

                        200 -> {

                            prayerDataList = it.data.data
                            adapter.prayerData =
                                prayerDataList

                        }
                        else -> {
                        }
                    }
                    adapter.invalidatePersonalTracker()
                }

                Status.ERROR -> {
                    adapter.invalidatePersonalTracker()
                    Log.d("Tracker", "ERROR")
                }
            }
        }

        modelTracker.addPrayerData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d("AddPrayerData", "LOADING")
                }
                Status.SUCCESS -> {
                    modelTracker.loadAllPrayerData(
                        dateFormatForDisplaying.format(Date()),
                        dateFormatForDisplaying.format(Date())
                    )
                }

                Status.ERROR -> {
                    Log.d("AddPrayerData", "Error")
                }
            }
        }

        modelTracker.updatePrayerData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d("UpdatePrayerData", "loading")
                }
                Status.SUCCESS -> {
                    modelTracker.loadAllPrayerData(
                        dateFormatForDisplaying.format(Date()),
                        dateFormatForDisplaying.format(Date())
                    )
                }
                Status.ERROR -> {
                    Log.d("UpdatePrayerData", "Error")
                }
            }
        }
    }

    override fun getitem(position: Int): Data {
        return biilboradList.get(position)
    }

    override fun getListSize(): Int {
        return biilboradList.size
    }

    override fun btnClick() {
        val statusSalah = getSalahStatus(upCommingPrayer.currentWaqtName)

        when (prayerDataList?.count() ?: 0 == 0) {

            true -> {
                val salahStatus = SalahStatus(
                    statusSalah?.get("Fajr")!!,
                    statusSalah.get("Zuhr")!!,
                    statusSalah.get("Asar")!!,
                    statusSalah.get("Maghrib")!!,
                    statusSalah.get("Isha")!!,
                )

                modelTracker.postPrayerData(
                    outputFormat.format(Date()),
                    AppPreference.language!!, salahStatus
                )
            }

            else -> {
                val salahStatus = SalahStatus(
                    statusSalah?.get("Fajr")!!,
                    statusSalah.get("Zuhr")!!,
                    statusSalah.get("Asar")!!,
                    statusSalah.get("Maghrib")!!,
                    statusSalah.get("Isha")!!,
                )
                modelTracker.updatePrayerData(
                    prayerDataList!!.get(0).id,
                    prayerDataList!!.get(0).createdBy, outputFormat.format(Date()),
                    AppPreference.language!!, salahStatus
                )
            }

        }
    }

    override fun switchClick(isChecked: Boolean, switchName: String) {

        when (prayerDataList?.count() ?: 0 == 0) {

            true -> {
                var salahStatus: SalahStatus? = null
                when (switchName) {
                    SWITCH_FAJR -> {
                        salahStatus = SalahStatus(
                            isChecked,
                            false,
                            false,
                            false,
                            false,
                        )
                    }
                    SWITCH_JOHR -> {
                        salahStatus = SalahStatus(
                            false,
                            isChecked,
                            false,
                            false,
                            false,
                        )
                    }
                    SWITCH_ASR -> {
                        salahStatus = SalahStatus(
                            false,
                            false,
                            isChecked,
                            false,
                            false,
                        )
                    }
                    SWITCH_MAGRIB -> {
                        salahStatus = SalahStatus(
                            false,
                            false,
                            false,
                            isChecked,
                            false,
                        )
                    }
                    SWITCH_ESHA -> {
                        salahStatus = SalahStatus(
                            false,
                            false,
                            false,
                            false,
                            isChecked,
                        )
                    }
                }

                if (salahStatus != null) {
                    modelTracker.postPrayerData(
                        outputFormat.format(Date()),
                        AppPreference.language!!, salahStatus
                    )
                }
            }

            else -> {
                var salahStatus: SalahStatus? = null
                when (switchName) {
                    SWITCH_FAJR -> {
                        salahStatus = SalahStatus(
                            isChecked,
                            prayerDataList?.get(0)?.salahStatus?.zuhr,
                            prayerDataList?.get(0)?.salahStatus?.asar,
                            prayerDataList?.get(0)?.salahStatus?.maghrib,
                            prayerDataList?.get(0)?.salahStatus?.isha,
                        )
                    }
                    SWITCH_JOHR -> {
                        salahStatus = SalahStatus(
                            prayerDataList?.get(0)?.salahStatus?.fajr,
                            isChecked,
                            prayerDataList?.get(0)?.salahStatus?.asar,
                            prayerDataList?.get(0)?.salahStatus?.maghrib,
                            prayerDataList?.get(0)?.salahStatus?.isha,
                        )
                    }
                    SWITCH_ASR -> {
                        salahStatus = SalahStatus(
                            prayerDataList?.get(0)?.salahStatus?.fajr,
                            prayerDataList?.get(0)?.salahStatus?.zuhr,
                            isChecked,
                            prayerDataList?.get(0)?.salahStatus?.maghrib,
                            prayerDataList?.get(0)?.salahStatus?.isha,
                        )
                    }
                    SWITCH_MAGRIB -> {
                        salahStatus = SalahStatus(
                            prayerDataList?.get(0)?.salahStatus?.fajr,
                            prayerDataList?.get(0)?.salahStatus?.zuhr,
                            prayerDataList?.get(0)?.salahStatus?.asar,
                            isChecked,
                            prayerDataList?.get(0)?.salahStatus?.isha,
                        )
                    }
                    SWITCH_ESHA -> {
                        salahStatus = SalahStatus(
                            prayerDataList?.get(0)?.salahStatus?.fajr,
                            prayerDataList?.get(0)?.salahStatus?.zuhr,
                            prayerDataList?.get(0)?.salahStatus?.asar,
                            prayerDataList?.get(0)?.salahStatus?.maghrib,
                            isChecked,
                        )
                    }
                }

                if (salahStatus != null) {
                    modelTracker.updatePrayerData(
                        prayerDataList!!.get(0).id,
                        prayerDataList!!.get(0).createdBy, outputFormat.format(Date()),
                        AppPreference.language!!, salahStatus
                    )
                }
            }

        }
    }

    override fun playPauseBtnClick() {
        playerControl.handlePlayPauseClick()
    }

    override fun reloadBtnClick() {
        mSelectedIndex = 0
        adapter.reload()
        adapter.invalidateNamesCell()
    }

    override fun soundonOffClick(off: Boolean) {
        soundOff = off
    }

    override fun tasbihButtonClick() {
        localcount++

        if (localcount > userSelectCount) {
            localcount = 0
            Toast.makeText(context, getString(R.string.count_complete), Toast.LENGTH_LONG)
                .show()
        } else {
            adapter.updateTasbihCount(localcount, userSelectCount)
            AppPreference.saveTashbihCount(
                AppPreference.loadTashbihCount(selectedItem) + 1,
                selectedItem
            )
            if (totalCount >= 0) {
                totalCount++
            } else {
                totalCount = 1
            }
        }
        AppPreference.totalCount = totalCount
        handleSoundTasbih()
    }

    override fun tasbihSoundButtonClick() {
        soundButtonClickTasbih()
    }

    override fun tasbihResetButtonClick() {
        showResetDialog()
    }

    override fun getTasbihCount() = localcount
    override fun getTasbihTimes() = userSelectCount
    override fun shareBitMap(bitmap: Bitmap?) {
        val uri = bitmap?.saveToInternalStorage(requireContext())

        requireActivity().shareCacheDirBitmapV2(uri)
    }

    override fun shareImage(imageUrl: String) {
        lifecycleScope.launch {
            getBitmapFromUrlX(imageUrl, activity)
        }
    }

    fun handleSoundTasbih() {
        if (sound) {
            if (mFlag1 != 0) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop()
                        mp.release()
                        mp = MediaPlayer.create(context, R.raw.second)
                    }
                    mp.start()
                    if (sound) {
                        mp.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                mp.start()
            }
        }
        mFlag1 = 1
    }

    fun soundButtonClickTasbih() {
        if (sound) {
            sound = false
            adapter.updateTasbihLayoutSoundButton(true)
        } else {
            sound = true
            adapter.updateTasbihLayoutSoundButton(false)
        }
    }

    fun setPrayerWaqt() {
        upCommingPrayer = prayerTimeCalculator.getUpCommingPrayer()
    }

    fun getSalahStatus(currentWaqt: String): HashMap<String, Boolean>? {
        val salahMap: HashMap<String, Boolean> = HashMap<String, Boolean>()

        salahMap.put("Fajr", false)
        salahMap.put("Zuhr", false)
        salahMap.put("Asar", false)
        salahMap.put("Maghrib", false)
        salahMap.put("Isha", false)
        when (currentWaqt) {
            getString(R.string.txt_fajr) -> {
                salahMap.put("Fajr", true)
            }

            getString(R.string.txt_johr) -> {
                salahMap.put("Zuhr", true)
            }

            getString(R.string.txt_asr) -> {
                salahMap.put("Asar", true)
            }

            getString(R.string.txt_magrib) -> {
                salahMap.put("Maghrib", true)
            }
            getString(R.string.txt_esha) -> {
                salahMap.put("Isha", true)
            }
            else -> {
                print("no wakt found")
            }
        }

        return salahMap
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction?.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        mSelectedIndex++
        playerControl.pause()
        //playerControl.killMediaPlayer()
        loadData()
        if (adapter.mcardStackLayoutManager?.topPosition == adapter.getItemCountCard() - 40) {
            adapter.paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled:")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardAppeared: ($position)")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardDisappeared: ($position)")
    }

    fun loadData() {
        lifecycleScope.launch {


            if (mSelectedIndex == 99) {
                mSelectedIndex = 0
            }
            if (playerControl == null) {
                playerControl = MediaPlayerControl()
            } else {
                playerControl.killMediaPlayer()
            }
            launch {
                delay(50)
                playerControl.handlePlayPauseClick()
            }
        }
    }

    inner class MediaPlayerControl {
        private var mPlayer: MediaPlayer? = null
        private var isPlaying = false

        init {
            isPlaying = false
        }

        fun handlePlayPauseClick() {
            when (isPlaying) {
                true -> pause()
                else -> play()
            }
        }

        fun play() {

            lifecycleScope.launch(Dispatchers.IO) {
                mNameInfo = mAllahNamesList.get(mSelectedIndex)
                isPlaying = true
                mPlayer = MediaPlayer()
                withContext(Dispatchers.Main) {
                    adapter.updateAllNameLayoutPlayPauseButton(true)
                    mPlayer?.setOnCompletionListener {

                        adapter.updateAllNameLayoutPlayPauseButton(false)
                        isPlaying = false
                    }

                    mPlayer?.setDataSource(mNameInfo.contentBaseUrl + "/" + mNameInfo.contentUrl)
                    mPlayer?.prepare()
                    mPlayer?.start()


                    when (soundOff) {
                        true -> {
                            mPlayer?.setVolume(0F, 0F)
                        }

                        false -> {
                            mPlayer?.setVolume(1F, 1F)
                        }
                    }
                }


            }


        }

        fun pause() {

            lifecycleScope.launch(Dispatchers.IO) {
                isPlaying = false
                withContext(Dispatchers.Main) {
                    adapter.updateAllNameLayoutPlayPauseButton(false)
                }
                mPlayer?.stop()
            }

        }

        fun killMediaPlayer() {
            try {
                mPlayer?.reset()
                mPlayer?.release()
                mPlayer = null
                isPlaying = false
            } catch (e: Exception) {

            }
        }
    }

    override fun getUserCount(count: Int) {
        localcount = 0
        userSelectCount = count

        adapter.updateTasbihItemCount(count)
    }

    override fun getSelectedItem(name: String) {
        selectedItem = name
        // adapter.updateTasbihSelectedItemCount(selectedItem)
    }


    fun showResetDialog() {
        val customDialog =
            MaterialAlertDialogBuilder(
                requireActivity(),
                R.style.MaterialAlertDialog_rounded
            )
        val binding: DialogTasbihResetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.dialog_tasbih_reset,
            null,
            false
        )


        val dialogView: View = binding.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        binding.imgClose.handleClickEvent {
            alertDialog.dismiss()
        }

        binding.btnTotalCount.handleClickEvent {
            resetTotalCount()
            alertDialog.dismiss()
        }

        binding.btnCurrentCount.handleClickEvent {
            resetCurrentCount()
            alertDialog.dismiss()
        }
    }


    fun resetTotalCount() {
        AppPreference.cleartotalCount()
        clearHistory(duaIndex)
        adapter.resetTasbihItemCount()
        localcount = 0
        totalCount = 0
    }

    fun resetCurrentCount() {
        localcount = 0
        adapter.resetTasbihItemCount()
    }

    private fun clearHistory(ars: Array<String>) {
        for (ar in ars) {
            AppPreference.clearHistoryCount(ar)
        }

    }

    override fun onResume() {
        super.onResume()
        // DeepLinkActionObserver.addSubscriber(this)
        totalCount = AppPreference.totalCount
    }

    /*override fun scrollToPatch(id: String) {
        if(isAllPatchLoaded){
            scrollablePatchId=id
            val pos=adapter.getPatchIndexByPatchId(scrollablePatchId!!)
            if(pos!=-1){
                Log.e("ScrollPos=","->$pos")
                binding.homeRecycle.scrollToPosition(pos)
               // scrollablePatchId=null
            }
        }else{
            scrollablePatchId=id
        }

    }*/
}

interface BillboardItemControl : Serializable {
    fun getitem(position: Int): Data
    fun getListSize(): Int
}

interface HomeCellItemControl : Serializable {
    fun btnClick()
    fun switchClick(isChecked: Boolean, switchName: String)
    fun playPauseBtnClick()
    fun reloadBtnClick()
    fun soundonOffClick(off: Boolean)
    fun tasbihButtonClick()
    fun tasbihSoundButtonClick()
    fun tasbihResetButtonClick()
    fun getTasbihCount(): Int
    fun getTasbihTimes(): Int
    fun shareBitMap(bitmap: Bitmap?)
    fun shareImage(imageUrl: String)
}