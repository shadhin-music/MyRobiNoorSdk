package com.gakk.noorlibrary.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.bubbleseekbar.BubbleSeekBar
import com.gakk.noorlibrary.model.nearby.PlaceInfo
import com.gakk.noorlibrary.model.nearby.Result
import com.gakk.noorlibrary.ui.adapter.NearestMosqueAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.NearbyViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.io.Serializable

private const val CATEGORY_TYPE = "categoryType"

internal class NearestMosqueFragment : Fragment(), DistanceControl {

    @Transient
    private lateinit var mCallback: DetailsCallBack

    @Transient
    private lateinit var repository: RestRepository

    @Transient
    private lateinit var model: NearbyViewModel

    @Transient
    var placeInfoList = arrayListOf<PlaceInfo>()

    @Transient
    private var markerOptions: Array<MarkerOptions>? = null

    @Transient
    private var bitmapDescriptor: BitmapDescriptor? = null

    @Transient
    private var categoryType = PAGE_NEAREST_MOSQUE

    @Transient
    private var MAP_TYPE = "mosque"

    @Transient
    private lateinit var adapter: NearestMosqueAdapter

    @Transient
    private lateinit var progressLayout: ConstraintLayout

    @Transient
    private lateinit var rvMosque: RecyclerView

    @Transient
    private lateinit var header: ConstraintLayout

    @Transient
    private lateinit var distanceSeekBar: BubbleSeekBar

    @Transient
    private lateinit var tvLocationNearset: AppCompatTextView

    @Transient
    private lateinit var layoutMap: ConstraintLayout

    companion object {

        @JvmStatic
        fun newInstance(categoryType: String) =
            NearestMosqueFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY_TYPE, categoryType)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(requireContext())

        arguments?.let {
            categoryType = it.getString(CATEGORY_TYPE) ?: PAGE_NEAREST_MOSQUE
            if (categoryType == PAGE_NEAREST_RESTAURANT) MAP_TYPE = "restaurant"
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_nearest_mosque,
            container, false
        )

        lifecycleScope.launch {

            progressLayout = view.findViewById(R.id.progressLayout)
            rvMosque = view.findViewById(R.id.rvMosque)
            header = view.findViewById(R.id.header)
            layoutMap = view.findViewById(R.id.layoutMap)
            distanceSeekBar = header.findViewById(R.id.distanceSeekBar)
            tvLocationNearset = header.findViewById(R.id.tvLocationNearset)

            val resource = R.drawable.ic_mosque_marker

            bitmapDescriptor = BitmapDescriptorFactory.fromResource(resource)


            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@NearestMosqueFragment,
                NearbyViewModel.FACTORY(repository)
            ).get(NearbyViewModel::class.java)

            setUpHeader()
            checkPermission()

            model.nearbyInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("Mosque", "loading")
                        progressLayout.visibility = View.VISIBLE
                        rvMosque.visibility = View.GONE
                    }

                    Status.SUCCESS -> {
                        val resultList: List<Result> = it.data?.results!!
                        markerOptions = Array(resultList.size) { MarkerOptions() }
                        for (i in 0..resultList.size - 1) {
                            val result: Result = resultList[i]
                            val placeInfo = PlaceInfo()
                            placeInfo.name = result.name
                            placeInfo.address = result.vicinity
                            placeInfo.placeLocation = result.geometry?.location
                            placeInfoList.add(placeInfo)


                            markerOptions!![i] = MarkerOptions()
                                .position(
                                    LatLng(
                                        result.geometry?.location
                                            ?.lat!!,
                                        result.geometry?.location
                                            ?.lng!!
                                    )
                                )
                                .title(result.name)
                                .snippet(result.name)
                                .icon(bitmapDescriptor)
                        }

                        if (!this@NearestMosqueFragment::adapter.isInitialized) {

                            rvMosque.visibility = View.VISIBLE

                            adapter = NearestMosqueAdapter(
                                mCallback,
                                categoryType = categoryType,
                                placeInfoList = placeInfoList
                            ).apply {
                                setOnItemClickListener { pi ->

                                    openLocationInMap(pi)
                                }
                            }
                            rvMosque.adapter = adapter
                            progressLayout.visibility = View.GONE
                        } else {
                            progressLayout.visibility = View.GONE

                            adapter.updatePlaceInfo(placeInfoList)
                            adapter.notifyDataSetChanged()
                            rvMosque.visibility = View.VISIBLE
                        }

                        rvMosque.layoutManager = LinearLayoutManager(requireContext())

                    }

                    Status.ERROR -> {
                        Log.e("Mosque", "error${it.message}")
                        progressLayout.visibility = View.GONE

                    }
                }
            }

            distanceSeekBar.setProgress(4f)
        }

        return view
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
                    val locationHelper = LocationHelper(requireContext())
                    locationHelper.requestLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    mCallback.showToastMessage("Please allow permission to get accurate info!")
                }
                return
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            }
        }
    }

    private fun setUpHeader() {

        header.let {
            val sb = StringBuilder(requireContext().getString(R.string.title_near_mosque))
            val city = TimeFormtter.getAdressWithCityName(
                requireContext()
            )
            if (city.isNotBlank()) {
                sb.append(" - ")
                sb.append(city)
            }

            tvLocationNearset.text = sb.toString()
        }

        header.let {

            distanceSeekBar.setCustomSectionTextArray { sectionCount, array ->
                array.clear()
                array.put(
                    0,
                    distanceSeekBar.context.getString(R.string.one_km_txt)
                )
                array.put(
                    1,
                    distanceSeekBar.context.getString(R.string.three_km_txt)
                )
                array.put(
                    2,
                    distanceSeekBar.context.getString(R.string.five_km_text)
                )
                array.put(
                    3,
                    distanceSeekBar.context.getString(R.string.ten_km_txt)
                )
                array
            }

            distanceSeekBar.setOnProgressChangedListener(object :
                BubbleSeekBar.OnProgressChangedListener {
                override fun onProgressChanged(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) = Unit

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float
                ) = Unit

                override fun getProgressOnFinally(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    updateDistance(progress)
                }
            })

        }
    }

    private fun openLocationInMap(pi: PlaceInfo?) {
        pi?.let {
            try {
                val uri =
                    "http://maps.google.com/maps?saddr=" + AppPreference.getUserCurrentLocation().lat + "," + AppPreference.getUserCurrentLocation().lng + "&daddr=" + pi.placeLocation?.lat + "," + pi.placeLocation?.lng
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                requireActivity().startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (categoryType == PAGE_NEAREST_MOSQUE) {
            mCallback.setToolBarTitle(getString(R.string.title_near_mosque))
        } else {
            mCallback.setToolBarTitle(getString(R.string.cat_nearest_retuarant))
        }

        layoutMap.handleClickEvent {
            mCallback.addFragmentToStackAndShow(
                MapFragment.newInstance(
                    this,
                    categoryType
                )
            )
        }

    }

    override fun updateDistance(value: Int) {
        placeInfoList.clear()

        when (value) {
            4 -> {
                getDataList("3000")
            }
            7 -> {
                getDataList("5000")
            }
            10 -> {
                getDataList("10000")
            }
            1 -> {
                getDataList("1000")
            }
        }
    }

    fun getDataList(radius: String) {

        val key = getString(R.string.map_key)

        model.loadNearbyPlaceInfo(
            key,
            radius,
            AppPreference.getUserCurrentLocation(),
            MAP_TYPE,
            AppPreference.language!!
        )
    }


    override fun getList(): Array<MarkerOptions>? {
        return markerOptions
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        Log.d("setMenuVisibility", "setMenuVisibility: $menuVisible")
    }

    fun test() {

    }

}

interface DistanceControl : Serializable {
    fun updateDistance(value: Int)
    fun getList(): Array<MarkerOptions>?
}
