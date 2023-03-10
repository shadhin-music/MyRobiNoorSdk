package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.util.PAGE_NEAREST_MOSQUE
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


private const val ARG_MOSQUE_CALL_BACK = "mosqueCallBack"
private const val CATEGORY_TYPE = "categoryType"
private val ARG_PARAM_AMBULANCE_LIST = "ambulanceList"

internal class MapFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var mCallback: DetailsCallBack
    private var mDistanceControl: DistanceControl? = null
    private var categoryType = PAGE_NEAREST_MOSQUE
    var markerList: Array<MarkerOptions>? = null

    companion object {

        @JvmStatic
        fun newInstance(
            distanceControl: DistanceControl? = null,
            categoryType: String? = null,
            markerOptions: Array<MarkerOptions>? = null
        ) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MOSQUE_CALL_BACK, distanceControl)
                    putString(CATEGORY_TYPE, categoryType)
                    putParcelableArray(ARG_PARAM_AMBULANCE_LIST, markerOptions)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mDistanceControl = it.getSerializable(ARG_MOSQUE_CALL_BACK) as? DistanceControl
            categoryType = it.getString(CATEGORY_TYPE) ?: PAGE_NEAREST_MOSQUE
            markerList = it.getParcelableArray(ARG_PARAM_AMBULANCE_LIST) as Array<MarkerOptions>?
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_map,
            container, false
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mCallback.setToolBarTitle(getString(R.string.title_near_mosque))

        val options = GoogleMapOptions()
        options.zoomControlsEnabled(true).compassEnabled(true)
        val fragment: SupportMapFragment = SupportMapFragment.newInstance()
        var transaction: FragmentTransaction? = null

        if (activity?.supportFragmentManager != null) {
            transaction =
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.flMap, fragment)
        }

        transaction?.commit()

        fragment.getMapAsync(this)
    }

    private fun markLocation() {

        mDistanceControl?.let {
            markerList = mDistanceControl?.getList()
        }

        if (markerList?.isNotEmpty() == true) {

            for (markerOptions in markerList!!) {
                if (mMap != null) {
                    mMap!!.addMarker(markerOptions)
                } else {
                    return
                }
            }
            animateCamera()
        }
    }

    private fun animateCamera() {

        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    AppPreference.getUserCurrentLocation().lat!!,
                    AppPreference.getUserCurrentLocation().lng!!
                )
            )
            .zoom(17f)
            .build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("NearestMopFrag", "onDestroView: called")
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        markLocation()
    }
}