package com.gakk.noorlibrary.ui.fragments.hajj.hajjguide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.util.CustomMapTileProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.Serializable


internal class HajjGuideFragment : Fragment(), OnMapReadyCallback, ImageChangeListener {

    @Transient
    private var mMap: GoogleMap? = null

    @Transient
    private var bitmapDescriptor: BitmapDescriptor? = null

    @Transient
    lateinit var markerOptions: MarkerOptions

    @Transient
    var marker: Marker? = null

    @Transient
    val locationList = arrayListOf<LatLng>()

    @Transient
    val markerlist: HashMap<Int, Marker> = HashMap()

    @Transient
    lateinit var imagesGreen: Array<Int>

    @Transient
    lateinit var imagesYellow: Array<Int>

    @Transient
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    @Transient
    private var mCallback: DetailsCallBack? = null

    @Transient
    private lateinit var bottomSheet: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjGuideFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_tracker,
            container, false
        )

        bottomSheet = view.findViewById(R.id.bottomSheet)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment: SupportMapFragment = SupportMapFragment.newInstance()
        var transaction: FragmentTransaction? = null

        if (activity?.supportFragmentManager != null) {
            transaction =
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.flMap, fragment)
        }

        transaction?.commit()
        fragment.getMapAsync(this)

        sheetBehavior.peekHeight = 300

        mCallback?.setToolBarTitle(resources.getString(R.string.cat_hajj))
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        imagesYellow =
            arrayOf(
                R.drawable.ic_hajj_guide_marker_one,
                R.drawable.ic_hajj_guide_marker_two,
                R.drawable.ic_hajj_guide_marker_three,
                R.drawable.ic_hajj_guide_marker_four,
                R.drawable.ic_hajj_guide_marker_five,
                R.drawable.ic_hajj_guide_marker_six,
                R.drawable.ic_hajj_guide_marker_seven,
                R.drawable.ic_hajj_guide_marker_eight,
                R.drawable.ic_hajj_guide_marker_nine,
                R.drawable.ic_hajj_guide_marker_ten,
                R.drawable.ic_hajj_guide_marker_eleven,
                R.drawable.ic_hajj_guide_marker_twelve
            )

        imagesGreen =
            arrayOf(
                R.drawable.ic_hajj_guide_marker_green_one,
                R.drawable.ic_hajj_guide_marker_green_two,
                R.drawable.ic_hajj_guide_marker_green_three,
                R.drawable.ic_hajj_guide_marker_green_four,
                R.drawable.ic_hajj_guide_marker_green_five,
                R.drawable.ic_hajj_guide_marker_green_six,
                R.drawable.ic_hajj_guide_marker_green_seven,
                R.drawable.ic_hajj_guide_marker_green_eight,
                R.drawable.ic_hajj_guide_marker_green_nine,
                R.drawable.ic_hajj_guide_marker_green_ten,
                R.drawable.ic_hajj_guide_marker_green_eleven,
                R.drawable.ic_hajj_guide_marker_green_twelve
            )

        mMap!!.setMinZoomPreference(3.0f)
        mMap!!.setMaxZoomPreference(3.9f)

        val builder = LatLngBounds.Builder()
        val location = LatLng(-68.43788674730129, -160.23516171500916)

        builder.include(location)
        val bounds = builder.build()
        val padding = 10

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap!!.animateCamera(cu)

        mMap!!.mapType = GoogleMap.MAP_TYPE_NONE

        mMap!!.addTileOverlay(
            TileOverlayOptions().tileProvider(
                CustomMapTileProvider()
            )
        )

        val upd =
            CameraUpdateFactory.newLatLngZoom(LatLng(-68.43788674730129, -160.23516171500916), 1f)
        mMap!!.moveCamera(upd)


        locationList.add(LatLng(-79.28005412531402, -154.2734581232071))
        locationList.add(LatLng(-75.68528942191732, -156.59094471484423))
        locationList.add(LatLng(-57.59908617224549, -148.73943127691746))
        locationList.add(LatLng(-42.59519840098348, -104.62145760655403))
        locationList.add(LatLng(-25.052907681841713, -65.61464980244637))
        locationList.add(LatLng(-36.02505472143232, -29.94937989860773))
        locationList.add(LatLng(-63.41600223125036, -14.447573348879814))
        locationList.add(LatLng(-71.78685598109638, -35.71048591285944))
        locationList.add(LatLng(-75.70492008008473, -52.393541671335704))
        locationList.add(LatLng(-80.19656234743935, -77.4493083357811))
        locationList.add(LatLng(-68.80046221476547, -116.44006345421077))
        locationList.add(LatLng(-61.31957139217275, -93.88521932065487))


        for (i in 0..locationList.size - 1) {

            if (AppPreference.loadHajjGuideStep((i + 1).toString() + AppPreference.userNumber)) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(imagesGreen[i])
            } else {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(imagesYellow[i])
            }

            markerOptions = MarkerOptions()
                .position(
                    locationList.get(i)
                )
            marker = mMap!!.addMarker(markerOptions)
            marker?.setIcon(bitmapDescriptor)

            markerlist.put(i, marker!!)
        }

        replaceFragment(HajjGuideDialoFragment.newInstance(this))
    }

    fun replaceFragment(fragment: Fragment) {
        val manager: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            ft.replace(R.id.fargment_host, fragment)
        }
        ft.commit()
    }

    override fun onRadioBtnClick(position: Int, btnName: String) {
        Log.e("position", "$position")

        val pos = position - 1

        if (markerlist.containsKey(pos)) {
            val mapMarker = markerlist.get(pos)
            if (btnName.equals("Done")) {
                mapMarker?.setIcon(BitmapDescriptorFactory.fromResource(imagesGreen[pos]))
                AppPreference.saveHajjGuideStep(true, position.toString())
            } else {
                mapMarker?.setIcon(BitmapDescriptorFactory.fromResource(imagesYellow[pos]))
                AppPreference.saveHajjGuideStep(false, position.toString())
            }

            val cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        mapMarker?.position?.latitude!!,
                        mapMarker.position.longitude
                    )
                )
                .zoom(3f)
                .build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null)
        }

        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}

interface ImageChangeListener : Serializable {
    fun onRadioBtnClick(position: Int, btnName: String)
}