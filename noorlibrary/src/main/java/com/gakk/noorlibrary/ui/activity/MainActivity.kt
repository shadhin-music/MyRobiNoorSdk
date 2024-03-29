package com.gakk.noorlibrary.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
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
import com.gakk.noorlibrary.job.RozaAlarmControlJob
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.adapter.SliderAdapter
import com.gakk.noorlibrary.ui.fragments.LiteratureHomeFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SubsResource
import com.gakk.noorlibrary.ui.fragments.tabs.HomeFragment
import com.gakk.noorlibrary.ui.fragments.tabs.MoreFragment
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.QuranViewModel
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.Serializable

internal class MainActivity : BaseActivity(), MainCallback {

    private lateinit var ivLogoHome: ImageView
    private lateinit var pager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    private val fragmentList = ArrayList<Fragment>()
    private lateinit var model: QuranViewModel
    private lateinit var modelSubscription: SubscriptionViewModel
    private lateinit var modelLiterature: LiteratureViewModel
    private lateinit var viewmodelHome: HomeViewModel
    private lateinit var repository: RestRepository
    private var surahList: MutableList<Data>? = null
    private var surahId: String? = null
    private var moreFragment: MoreFragment? = null
    var mainCallback: MainCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainCallback = this

        setContentView(R.layout.activity_main_noor_sdk)
        setupUi()

        setStatusColor(R.color.colorPrimaryDark)

        ivLogoHome.setImageResource(R.drawable.ic_noor_yellow_robi)

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

            modelSubscription.dailySubInfoRobi.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("SubMain", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.d("SubMain", "dailySubInfoRobi" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subDaily = true
                            }

                            else -> {
                                AppPreference.subDaily = false
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }

            modelSubscription.fifteenSubInfoRobi.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.e("Subinfo", "fifteenSubInfoRobi" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subFifteenDays = true

                            }
                            else -> {
                                AppPreference.subFifteenDays = false
                            }

                        }
                    }
                    Status.ERROR -> {
                        Log.d("Sub", "Error" + it.message)
                    }
                }
            }
            modelSubscription.sslSubInfoMonthly.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("sslSubInfoMonthly", "LOADING")
                    }

                    Status.SUCCESS -> {
                        Log.e("sslSubInfoMonthly", "SUCCESS")
                        when (it.data?.response) {
                            "1AC" -> {
                                AppPreference.subMonthlySsl = true
                            }

                            else -> {
                                AppPreference.subMonthlySsl = false
                            }
                        }
                    }

                    Status.ERROR -> {
                        Log.e("sslSubInfoMonthly", "ERROR")
                    }
                }
            }

            modelSubscription.sslSubInfoHalfYearly.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("sslSubInfoHalfYearly", "LOADING")
                    }

                    Status.SUCCESS -> {
                        Log.e("sslSubInfoHalfYearly", "SUCCESS")
                        when (it.data?.response) {
                            "1AC" -> {
                                AppPreference.subHalfYearlySsl = true
                            }

                            else -> {
                                AppPreference.subHalfYearlySsl = false
                            }
                        }
                    }

                    Status.ERROR -> {
                        Log.e("sslSubInfoHalfYearly", "ERROR")
                    }
                }
            }

            modelSubscription.sslSubInfoYearly.observe(this@MainActivity) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("sslSubInfoYearly", "LOADING")
                    }

                    Status.SUCCESS -> {
                        Log.e("sslSubInfoYearly", "SUCCESS")
                        when (it.data?.response) {
                            "1AC" -> {
                                AppPreference.subYearlySsl = true
                            }

                            else -> {
                                AppPreference.subYearlySsl = false
                            }
                        }
                    }

                    Status.ERROR -> {
                        Log.e("sslSubInfoYearly", "ERROR")
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


            // all sub

            modelSubscription.subscription_robi.observe(this@MainActivity) {

                when(it)
                {
                    is SubsResource.Error -> Log.e("Sub", "Error" + it.msg)
                    SubsResource.Loading -> Log.e("Sub", "loading")
                    is SubsResource.SubscriptionRobi ->
                    {

                        when(it.subscriptionId)
                        {

                            SUBSCRIPTION_ID_WEEKLY_ROBI -> {

                                when (it.data.data) {

                                    "1AK" ->  AppPreference.subWeeklyRobi = true
                                    "0AK" -> AppPreference.subWeeklyRobi = false
                                }
                            }

                            SUBSCRIPTION_ID_MONTHLY_ROBI -> {

                                when (it.data.data) {

                                    "1AK" ->  AppPreference.subMonthlyRobi = true
                                    "0AK" -> AppPreference.subMonthlyRobi = false
                                }

                            }


                            SUBSCRIPTION_ID_WEEKLY_ROBI_ON_DEMAND -> {

                                when (it.data.data) {

                                    "1AK" ->  AppPreference.subWeeklyRobiOnDemand = true
                                    "0AK" -> AppPreference.subWeeklyRobiOnDemand = false
                                }

                            }

                            SUBSCRIPTION_ID_MONTHLY_ROBI_ON_DEMAND -> {

                                when (it.data.data) {

                                    "1AK" ->  AppPreference.subMonthlyRobiOnDemand = true
                                    "0AK" -> AppPreference.subMonthlyRobiOnDemand = false
                                }

                            }
                        }
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

                    modelSubscription.subscriptionCheckRobi(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_WEEKLY_ROBI
                    )

                    modelSubscription.subscriptionCheckRobi(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_MONTHLY_ROBI
                    )

                    // On demand subs

                    modelSubscription.subscriptionCheckRobi(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_WEEKLY_ROBI_ON_DEMAND
                    )

                    modelSubscription.subscriptionCheckRobi(
                        AppPreference.userNumber!!,
                        SUBSCRIPTION_ID_MONTHLY_ROBI_ON_DEMAND
                    )

                    modelSubscription.checkSslSubStatusMonthly(
                        AppPreference.userNumber!!,
                        SSL_SERVICE_ID_MONTHLY
                    )
                    modelSubscription.checkSslSubStatusHalfYearly(
                        AppPreference.userNumber!!,
                        SSL_SERVICE_ID_HALF_YEARLY
                    )
                    modelSubscription.checkSslSubStatusYearly(
                        AppPreference.userNumber!!,
                        SSL_SERVICE_ID_YEARLY
                    )
                }

            }



        }


    }

    private fun setupUi() {
        ivLogoHome = findViewById(R.id.ivLogoHome)
        pager = findViewById(R.id.pager)
        bottomNav = findViewById(R.id.bottomNav)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        // AppPreference.clearCachedUser()
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
        pager.adapter = adapter
        pager.isUserInputEnabled = false

        fragmentList.addAll(
            listOf(
                HomeFragment.newInstance(),
                LiteratureHomeFragment.newInstance(
                    literatureType = LiteratureType.Hadis
                )
            )
        )

        adapter.setFragmentList(fragmentList)


        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tab_home -> {
                    pager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.tab_my_robi -> {
                    pager.currentItem = 1
                    return@setOnItemSelectedListener true
                }

                /* R.id.tab_compass -> {
                     binding.pager.currentItem = 2
                     return@setOnItemSelectedListener true
                 }*/
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


                surahId?.let {
                    putExtra(SURAH_ID, it)
                }
                surahList?.let {
                    putExtra(SURAH_LIST, it as Serializable)
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

