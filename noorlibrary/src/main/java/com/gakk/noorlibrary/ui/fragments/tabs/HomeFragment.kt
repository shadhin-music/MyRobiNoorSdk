package com.gakk.noorlibrary.ui.fragments.tabs

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.BottomSheetItem
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.model.billboard.Data
import com.gakk.noorlibrary.model.tracker.SalahStatus
import com.gakk.noorlibrary.ui.adapter.HomeFragmentAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.HomeViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class HomeFragment : Fragment(), BillboardItemControl, HomeCellItemControl {

    private lateinit var mCallback: MainCallback
    private lateinit var repository: RestRepository
    private lateinit var model: HomeViewModel
    private lateinit var biilboradList: List<Data>
    private lateinit var prayerTimeCalculator: PrayerTimeCalculator
    private var fromMonth: Date? = null
    private lateinit var toMonth: String
    private val dateFormatForDisplaying =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private lateinit var adapter: HomeFragmentAdapter
    var prayerDataList: List<com.gakk.noorlibrary.model.tracker.Data>? = null
    private lateinit var upCommingPrayer: UpCommingPrayer
    private lateinit var dua: Array<String>

    //view

    private lateinit var homeRecycle: RecyclerView
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var noDataLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton

    private lateinit var modelUserTracking: AddUserTrackigViewModel

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

        val view = inflater.inflate(
            R.layout.fragment_home,
            container, false
        )
        initView(view)

        return view
    }

    private fun initView(view: View) {
        homeRecycle = view.findViewById(R.id.homeRecycle)
        progressLayout = view.findViewById(R.id.progressLayout)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        noDataLayout = view.findViewById(R.id.noDataLayout)
        btnRetry = view.findViewById(R.id.btnRetry)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prayerTimeCalculator = PrayerTimeCalculator(requireContext())
        setPrayerWaqt()

        lifecycleScope.launch {

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


            modelUserTracking = ViewModelProvider(
                this@HomeFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            subscribeObserver()

            fromMonth = Util.getFirstDateOfMonth(Date())

            toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))

            if (isNetworkConnected(requireContext())) {
                model.getBillboradData()
            } else {
                noInternetLayout.visibility = View.VISIBLE
            }

            btnRetry.handleClickEvent {
                model.getBillboradData()
            }
        }
    }
    fun getBottomSheetItemList(): List<BottomSheetItem> {
        val bottomSheetItems: ArrayList<BottomSheetItem> = ArrayList()
        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_quran,
                getString(R.string.cat_quran)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_roja,
                getString(R.string.cat_roja)
            )
        )

        bottomSheetItems.add(BottomSheetItem(R.drawable.ic_cat_dua, getString(R.string.cat_dua)))

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_hadis,
                getString(R.string.cat_hadith)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_jakat,
                getString(R.string.txt_jakat_calculator)
            )
        )

        /* bottomSheetItems.add(
             BottomSheetItem(
                 R.drawable.ic_cat_mosque,
                 getString(R.string.cat_nearest_mosque)
             )
         )*/

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_islamic_podcast,
                getString(R.string.cat_islamic_podcast)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_namaz_sikhha,
                getString(R.string.cat_namaz_sikhha)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_hajj,
                getString(R.string.cat_hajj)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_umrah_hajj,
                getString(R.string.cat_umrah_hajj)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_donation,
                getString(R.string.cat_donation)
            )
        )


        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_eid_jamater_location,
                getString(R.string.cat_eid_jamat)
            )
        )





        return bottomSheetItems
    }
    private fun subscribeObserver() {
        model.billboardResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                    noInternetLayout.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    when (it.data?.status) {
                        200 -> {
                            val list = it.data.data
                            biilboradList = list.filterNot {
                                it.categoryName.trim().equals("আল্লাহর ৯৯ নাম")
                                        || it.categoryName.trim().equals("ট্র্যাকার")
                                        || it.categoryName.trim().equals("ডিজিটাল কুরআন ক্লাস")
                                        || it.categoryName.trim().equals("খতমে কুরআন")
                            }
                            model.getHomeData()
                        }
                        else -> {
                            noDataLayout.visibility = View.VISIBLE
                        }
                    }

                    if (noInternetLayout.isVisible) {
                        noInternetLayout.visibility = View.GONE
                    }

                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.VISIBLE
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

                            var homeList: MutableList<com.gakk.noorlibrary.model.home.Data> =
                                mutableListOf()
                            homeList.clear()
                            homeList =
                                it.data.data as MutableList<com.gakk.noorlibrary.model.home.Data>

                            val sortedList = homeList.filterNot { it.about.equals("Virtual Kafela") ||
                                    it.about.equals("Nearest Mosque")
                            }

                            val menuList = getBottomSheetItemList()

                            adapter = HomeFragmentAdapter(menuList,
                                sortedList as MutableList<com.gakk.noorlibrary.model.home.Data>,

                                mCallback,
                                this@HomeFragment,
                                prayerTimeCalculator,
                                this@HomeFragment
                            )

                            homeRecycle.adapter = adapter
                            adapter.nextWaqt = upCommingPrayer.nextWaqtNameTracker

                            AppPreference.userNumber?.let { userNumber ->
                                modelUserTracking.addTrackDataUser(userNumber, PAGE_HOME)
                            }
                        }
                        STATUS_NO_DATA -> {
                            noDataLayout.visibility = View.VISIBLE
                        }
                    }

                    progressLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }

        modelUserTracking.trackUser.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("trackUser", "LOADING")
                }
                Status.ERROR -> {
                    Log.e("trackUser", "ERROR")
                }

                Status.SUCCESS -> {
                    Log.e("trackUser", "SUCCESS")
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

        when ((prayerDataList?.count() ?: 0) == 0) {

            true -> {
                val salahStatus = SalahStatus(
                    statusSalah?.get("Fajr")!!,
                    statusSalah.get("Zuhr")!!,
                    statusSalah.get("Asar")!!,
                    statusSalah.get("Maghrib")!!,
                    statusSalah.get("Isha")!!,
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
            }

        }
    }

    override fun shareBitMap(bitmap: Bitmap?) {
        val uri = bitmap?.saveToInternalStorage(requireContext())

        requireActivity().shareCacheDirBitmapV2(uri)
    }

    override fun shareImage(imageUrl: String) {
        lifecycleScope.launch {
            getBitmapFromUrlX(imageUrl, activity)
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

    override fun onResume() {
        super.onResume()
    }
}

interface BillboardItemControl : Serializable {
    fun getitem(position: Int): Data
    fun getListSize(): Int
}

interface HomeCellItemControl : Serializable {
    fun btnClick()
    fun shareBitMap(bitmap: Bitmap?)
    fun shareImage(imageUrl: String)
}