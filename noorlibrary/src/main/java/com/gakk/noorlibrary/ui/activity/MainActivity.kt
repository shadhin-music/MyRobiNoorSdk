package com.gakk.noorlibrary.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.callbacks.LiteratureListCallBack
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.databinding.ActivityMainNoorSdkBinding
import com.gakk.noorlibrary.databinding.DialogAlreadyAttemptBinding
import com.gakk.noorlibrary.job.RozaAlarmControlJob
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.model.quranSchool.Scholar
import com.gakk.noorlibrary.ui.adapter.SliderAdapter
import com.gakk.noorlibrary.ui.fragments.CompassFragment
import com.gakk.noorlibrary.ui.fragments.LiteratureHomeFragment
import com.gakk.noorlibrary.ui.fragments.tabs.HomeFragment
import com.gakk.noorlibrary.ui.fragments.tabs.MoreFragment
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.Serializable

internal class MainActivity : BaseActivity(), MainCallback {
    private lateinit var binding: ActivityMainNoorSdkBinding
    private val fragmentList = ArrayList<Fragment>()
    private lateinit var model: QuranViewModel
    private lateinit var modelSubscription: SubscriptionViewModel
    private lateinit var modelLiterature: LiteratureViewModel
    private lateinit var modelProfile: ProfileViewModel
    private lateinit var viewmodelHome: HomeViewModel
    private lateinit var repository: RestRepository
    private var surahList: MutableList<Data>? = null
    private var surahId: String? = null
    private var moreFragment: MoreFragment? = null
    var mainCallback: MainCallback? = null


    val settingsActLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.e("Noor", "permitted")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainCallback = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_noor_sdk)


        setStatusColor(R.color.colorPrimaryDark)

        binding.ivLogoHome.setImageResource(R.drawable.ic_noor_yellow_robi)



        setSlider()

        checkGPS()

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            }
        }

        lifecycleScope.launch {

            handleRozaAlarmControlJob()
            handleExtras(intent)

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@MainActivity,
                QuranViewModel.FACTORY(repository)
            ).get(QuranViewModel::class.java)


            modelSubscription = ViewModelProvider(
                this@MainActivity,
                SubscriptionViewModel.FACTORY(repository)
            ).get(SubscriptionViewModel::class.java)

            modelLiterature = ViewModelProvider(
                this@MainActivity,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            modelProfile = ViewModelProvider(
                this@MainActivity,
                ProfileViewModel.FACTORY(repository)
            ).get(ProfileViewModel::class.java)

            viewmodelHome = ViewModelProvider(
                this@MainActivity,
                HomeViewModel.FACTORY(repository)
            ).get(HomeViewModel::class.java)

            model.surahListResponse.observe(this@MainActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
                        surahList = it.data?.data

                        openDetailsActivityWithPageName(
                            PAGE_SURAH_DETAILS,
                            surahId,
                            surahList
                        )
                    }
                    else -> {
                        Log.d("Surah", "Failed")
                    }
                }
            }
            modelProfile.profile.observe(
                this@MainActivity
            ) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("Profile", "loading")
                    }

                    Status.SUCCESS -> {
                        Log.e("Profile", "success")
                        AppPreference.cachedUserInfo = it.data?.data!!
                    }

                    Status.ERROR -> {
                        Log.e("Profile", "error${it.message}")
                    }
                }
            }

            modelSubscription.weeklySubInfo.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("SubMain", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.d("SubMain", "Weekly" + it.data)
                        when (it.data?.regStatus) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                            }
                            "0AK" -> {
                                AppPreference.subWeekly = false
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }

            modelSubscription.weeklySubInfoRobi.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("SubMain", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.d("SubMain", "Weekly" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                            }
                            "0AK" -> {
                                AppPreference.subWeekly = false
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }
            modelSubscription.monthlySubInfo.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.e("Subinfo", "Monthly" + it.data)
                        when (it.data?.regStatus) {
                            "1AK" -> {
                                AppPreference.subMonthly = true

                            }
                            "0AK" -> {
                                AppPreference.subMonthly = false
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }

            modelSubscription.monthlySubInfoRobi.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.e("Subinfo", "Monthly" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subMonthly = true

                            }
                            "0AK" -> {
                                AppPreference.subMonthly = false
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }

            modelLiterature.literatureListData.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("Live", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.d("Live", "Sucess")
                        BaseApplication.LIVE_VIDEO_ID = it.data?.data?.get(0)?.refUrl
                    }
                    Status.ERROR -> {
                        Log.d("Live", "Error")
                    }
                }
            }

            viewmodelHome.addDeviceInfo.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("addDeviceInfo", "Sucess")
                    }

                    Status.SUCCESS -> {
                        when (it.data?.status) {
                            200 -> {
                                Log.d("addDeviceInfo", "Sucess")
                            }
                        }
                    }

                    Status.ERROR -> {
                        Log.d("addDeviceInfo", "ERROR")
                    }
                }
            }

            modelLiterature.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.live_video_id),
                "undefined",
                "1"
            )

            if (isNetworkConnected(this@MainActivity)) {
                AppPreference.userNumber.let {
                    modelSubscription.checkSubscriptionRobi(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_DAILY
                    )
                    modelSubscription.checkSubscriptionFifteenDays(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_FIFTEENDAYS
                    )
                }
            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    val locationHelper = LocationHelper(this)
                    locationHelper.requestLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    showToast("Please allow permission to get accurate prayer time info")
                }
                return
            }
        }
    }


    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.e("LifeCycleAct", "onNewIntent- mainActivity")

        this.intent = intent
        lifecycleScope.launch {
            handleRozaAlarmControlJob()
            handleExtras(intent)
        }
    }

    private fun setSlider() {

        val adapter = SliderAdapter(this)
        binding.pager.adapter = adapter
        binding.pager.isUserInputEnabled = false

        fragmentList.addAll(
            listOf(
                HomeFragment.newInstance(),
                LiteratureHomeFragment.newInstance(
                    literatureType = LiteratureType.Hadis
                ),
                CompassFragment(),
            )
        )

        adapter.setFragmentList(fragmentList)


        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tab_home -> {
                    binding.pager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.tab_my_robi -> {
                    binding.pager.currentItem = 1
                    return@setOnItemSelectedListener true
                }

                R.id.tab_compass -> {
                    binding.pager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
                R.id.tab_more -> {
                    if (moreFragment != null) {
                        return@setOnItemSelectedListener false
                    }
                    moreFragment = MoreFragment.newInstance()
                    moreFragment?.show(supportFragmentManager, moreFragment?.tag)
                    return@setOnItemSelectedListener true
                }

                else -> return@setOnItemSelectedListener false
            }
        }
    }


    override fun openSuraDetailsById(id: String) {
        Toast.makeText(this, "Sura Detais Clicked", Toast.LENGTH_LONG).show()
    }


    override fun openDetailsActivityWithPageName(
        pageName: String,
        surahId: String?,
        surahList: MutableList<Data>?,
        scholar: Scholar?,
        selectedIndex: Int?,
        literatureListCallBack: LiteratureListCallBack?,
        currentPageNo: Int?,
        isFav: Boolean?,
        catId: String?,
        subCatId: String?,
        pageTitle: String?,
        itemCount: Int?,
        times: Int?,
        literatures: MutableList<Literature>?,
        isFromHomeEvent: Boolean?
    ) {


        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
            .apply {
                putExtra(PAGE_NAME, pageName)

                if (pageName == PAGE_LITERATURE_DETAILS) {
                    putExtra(SELECTED_INDEX, selectedIndex)
                    putExtra(LITERATURE_LIST_CALL_BACK, LiteratureListWrapper(literatures))
                    putExtra(CURRENT_PAGE, currentPageNo)
                    putExtra(IS_FAV, isFav)
                }

                if (pageName == PAGE_TASBIH) {
                    putExtra(SELECTED_INDEX, selectedIndex)
                    putExtra(CURRENT_PAGE, currentPageNo)
                    putExtra(ITEM_COUNT, itemCount)
                    putExtra(ITEM_TIMES, times)
                }

                if (pageName == PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY) {
                    putExtra(CAT_ID, R.string.namaz_rules_cat_id.getLocalisedTextFromResId())
                    putExtra(SUB_CAT_ID, R.string.kalima_sub_cat_id.getLocalisedTextFromResId())
                    putExtra(PAGE_TITLE, "")
                    putExtra(IS_FAV, false)
                }

                if (pageName == PAGE_ISLAMIC_EVENT && isFromHomeEvent == true) {

                    putExtra(CAT_ID, catId)
                    putExtra(SUB_CAT_ID, subCatId)
                    putExtra(PAGE_TITLE, "")
                    putExtra("FromHome", true)
                }
                surahId?.let {
                    putExtra(SURAH_ID, it)
                }
                surahList?.let {
                    putExtra(SURAH_LIST, it as Serializable)
                }
                scholar?.let {
                    putExtra(SCHOLAR, it)
                }
            }
        startActivity(intent)

    }

    override fun getWindowHeight(): Int {
        return super.getWindowHeight(this)
    }

    override fun getScreenWith(): Int {
        return super.getScreenWidth()
    }

    override fun showToastMessage(message: String) {
        super.showToast(message)
    }

    override fun makeMoreFragmentNull() {
        moreFragment = null
    }

    override fun openCurrentSurahById(id: String) {
        surahId = id
        model.getAllSurah("1")
    }

    private fun handleExtras(intent: Intent?) {
        intent?.let { intent ->

            val page: String? = intent.getStringExtra(DESTINATION_FRAGMENT)
            var pageNotification: String? = null
            /* val segment = intent.getStringExtra(HAJJ_TRACKER_SEGMENT_TAG)

           segment.let {
                if (segment.equals(NOTIFICATION_SEGMENT_VALUE) || segment.equals("HajjTrackerProMax")) {
                    startActivity(
                        Intent(this, HajjTrackerActivity::class.java)
                            .putExtra("Tacker", "notification")
                            .putExtra(HAJJ_TRACKER_SEGMENT_TAG, segment)
                            .putExtra(USER_NAME_TAG, intent.getStringExtra("UserName"))
                            .putExtra(USER_NUMBER, intent.getStringExtra("UserPhoneNumber"))
                    )
                }
            }*/

            intent.extras?.let {

                val keys = it.keySet()
                if (!keys.isNullOrEmpty()) {
                    keys.forEach { key ->
                        when (it[key]) {
                            is Long -> println("$key = ${intent.getLongExtra(key, 0L)}")
                            is Int -> println("$key = ${intent.getIntExtra(key, 0)}")
                            is String -> println("$key = ${intent.getStringExtra(key)}")
                            is Boolean -> println("$key = ${intent.getBooleanExtra(key, false)}")
                            else -> println("unkonwn Type")
                        }
                    }
                }

                pageNotification = intent.extras?.get(NOTIFICATION_TITLE_TAG) as String?
            }

            page?.let {
                when (it) {
                    PAGE_ROZA -> {
                        openDetailsActivityWithPageName(
                            it,
                        )
                    }
                    PAGE_SURAH_DETAILS -> {
                        val surahId =
                            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getCurrentSurah()?.id
                        val surahList =
                            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getSurahList()
                        SurahListControl.surahList = surahList
                        surahId?.let { it1 -> SurahListControl.updateSelectedIndex(it1) }
                        openDetailsActivityWithPageName(
                            page,
                            surahId,
                            surahList,
                        )
                        Log.i("QURAN_", "id :$surahId size:${surahList?.size ?: 0}")

                    }
                    PAGE_LIVE_VIDEO -> {

                        startActivity(
                            Intent(this, YoutubePlayerActivity::class.java).apply {
                                putExtra(IS_IJTEMA_LIVE_VIDEO, false)
                            }
                        )
                    }
                    else -> {
                        openDetailsActivityWithPageName(
                            it,
                        )
                    }
                }
            }

            pageNotification?.let {

                when (it) {
                    PAGE_LIVE_VIDEO -> {
                        BaseApplication.LIVE_VIDEO_ID =
                            intent.extras?.get(NOTIFICATION_CATEGORY_ID_TAG) as String?
                        startActivity(
                            Intent(this, YoutubePlayerActivity::class.java).apply {
                                putExtra(IS_IJTEMA_LIVE_VIDEO, false)
                            }
                        )
                    }

                    else -> {
                        openDetailsActivityWithPageName(
                            it,
                        )
                    }

                }

            }
        }

    }

    fun handleRozaAlarmControlJob() {
        if (isNetworkConnected(this)) {
            RozaAlarmControlJob(this).startJob()
        } else {
            Log.e("Noor", "No internet connection!")
        }
    }

    override fun onResume() {
        super.onResume()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPerm()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private fun requestExactAlarmPerm() {
        showSettingsDialog()
    }

    fun showSettingsDialog() {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )
        val binding: DialogAlreadyAttemptBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_already_attempt,
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
        alertDialog.setCancelable(true)
        alertDialog.show()

        (binding.tvTitleExit.layoutParams as ConstraintLayout.LayoutParams).apply {
            marginStart = 50.toPx()
            topMargin = 8.toPx()
            marginEnd = 30.toPx()
            bottomMargin = 8.toPx()
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
        binding.tvTitleExit.setText("On Android S and higher, the app needs permission to schedule exact alarms. Without this, no alarm can be set. Please go to Settings and enable this permission to continue setting an alarm.")
        binding.btnComplete.setText("Go to Settings")

        binding.btnComplete.handleClickEvent {
            alertDialog.dismiss()
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                intent.action = ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                intent.setData(Uri.parse("package:com.gakk.noorlibrary"))
            }
            settingsActLauncher.launch(intent)
        }

    }

    private fun checkGPS() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        var statusOfGPS = false
        if (manager != null) {
            statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        if (!statusOfGPS) {
            Util.displayPromptForEnablingGPS(this)
        }
    }

    override fun onStart() {
        super.onStart()
    }

}

