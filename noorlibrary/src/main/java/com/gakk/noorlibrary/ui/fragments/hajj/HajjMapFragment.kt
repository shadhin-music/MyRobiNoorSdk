package com.gakk.noorlibrary.ui.fragments.hajj

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentHajjMapBinding
import com.gakk.noorlibrary.databinding.LayoutMarkerDetailsDialogBinding
import com.gakk.noorlibrary.util.exH
import com.gakk.noorlibrary.util.setApplicationLanguage


class HajjMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHajjMapBinding
    private var mCallback: DetailsCallBack? = null
    private var mMap: GoogleMap? = null
    private lateinit var trackerTitles: Array<String>
    private lateinit var stepList: ArrayList<LatLng>
    private var mIsSpinnerFirstCall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hajj_map, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(requireContext().getString(R.string.hajj_map_title))
        initAllLocation()
        trackerTitles = requireContext().resources.getStringArray(R.array.hajj_tracker_places)

        initDistanceSpinner()
        setUpMapFragment()
    }

    private fun initAllLocation() {
        stepList = ArrayList()
        stepList.add(LatLng(21.42558, 39.82612))
        stepList.add(LatLng(21.42207, 39.8953))
        stepList.add(LatLng(21.3548, 39.98398))
        stepList.add(LatLng(21.38587, 39.91187))
        stepList.add(LatLng(21.42079, 39.87283))
        stepList.add(LatLng(21.41887, 39.82475))
        stepList.add(LatLng(21.41487, 39.88758))
    }

    private val onItemSelectedListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
            if (!mIsSpinnerFirstCall) {
                drawMarker(position)
            } else {
                mIsSpinnerFirstCall = false
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun initDistanceSpinner() {
        binding.spinnerTo.onItemSelectedListener = onItemSelectedListener
    }

    private fun drawMarker(position: Int) {
        Log.d("drawmarker", "drawMarker: called")
        mMap?.clear()
        val fromPos = binding.spinnerFrom.selectedItemPosition

        if (stepList.isEmpty()) {
            initAllLocation()
        }

        val locationFrom = stepList[fromPos]
        val locationTo = stepList[position]

        val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_hajj_map_marker)

        mMap!!.addMarker(
            MarkerOptions().position(locationFrom).title(trackerTitles[fromPos]).icon(
                bitmap
            )
        )
        mMap!!.addMarker(
            MarkerOptions().position(locationTo).title(trackerTitles[position]).icon(
                bitmap
            )
        )

        animateCamera(locationFrom)
        val line = mMap?.addPolyline(PolylineOptions().add(locationFrom).add(locationTo))
        line?.width = 20f
        line!!.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        mMap?.setOnMarkerClickListener { mMarker ->
            false
        }
    }

    private fun setUpMapFragment() {
        val options = GoogleMapOptions()
        options.zoomControlsEnabled(true).compassEnabled(true)
        val fragment: SupportMapFragment = SupportMapFragment.newInstance()
        var transaction: FragmentTransaction? = null

        if (fragmentManager != null) {
            transaction = fragmentManager?.beginTransaction()?.replace(R.id.flMap, fragment)
        }

        transaction?.commit()

        fragment.getMapAsync(this)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            HajjMapFragment()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    21.383845775184625,
                    39.898824869751024
                )
            )
        )
        this.mMap?.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
        this.mMap?.mapType = 1

        markAllLocationOnInit()
    }

    @SuppressLint("InflateParams")
    private fun markAllLocationOnInit() {
        val bitmapDescriptorArr = arrayOf(
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_a),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_b),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_c),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_d),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_e),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_f),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_g),
                    100,
                    100
                )
            )
        )
        val markerOne = mMap!!.addMarker(
            MarkerOptions().position(stepList[0]).title(trackerTitles.get(0)).icon(
                bitmapDescriptorArr[0]
            )
        )
        val markerTwo = mMap!!.addMarker(
            MarkerOptions().position(stepList[1]).title(trackerTitles.get(1)).icon(
                bitmapDescriptorArr[1]
            )
        )
        val markerThree = mMap!!.addMarker(
            MarkerOptions().position(stepList[2]).title(trackerTitles.get(2)).icon(
                bitmapDescriptorArr[2]
            )
        )
        val markerFour = mMap!!.addMarker(
            MarkerOptions().position(stepList[3]).title(trackerTitles.get(3)).icon(
                bitmapDescriptorArr[3]
            )
        )
        val markerFive = mMap!!.addMarker(
            MarkerOptions().position(stepList[4]).title(trackerTitles.get(4)).icon(
                bitmapDescriptorArr[4]
            )
        )
        val markerSix = mMap!!.addMarker(
            MarkerOptions().position(stepList[5]).title(trackerTitles.get(5)).icon(
                bitmapDescriptorArr[5]
            )
        )
        val markerSeven = mMap!!.addMarker(
            MarkerOptions().position(stepList[6]).title(trackerTitles.get(6)).icon(
                bitmapDescriptorArr[6]
            )
        )

        markerOne?.tag = 0
        markerTwo?.tag = 1
        markerThree?.tag = 2
        markerFour?.tag = 3
        markerFive?.tag = 4
        markerSix?.tag = 5
        markerSeven?.tag = 6

        mMap?.setOnMarkerClickListener { mMarker ->
            val tag = mMarker.tag as Int
            showMarkerDetails(tag)
            false
        }
        showDirection(stepList)
    }

    private fun showDirection(stepList: ArrayList<LatLng>) {
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
            ContextCompat.getColor(requireContext(), R.color.accent),
            ContextCompat.getColor(requireContext(), R.color.orange),
            ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
        )

        for (i in 0 until stepList.size - 1) {
            val line = mMap!!.addPolyline(PolylineOptions().add(stepList[i]).add(stepList[i + 1]))
            line.width = 10f
            line.isClickable = true
            line.color = colors[i]
        }
    }

    private fun showMarkerDetails(tag: Int) {
        val details = requireContext().resources.getStringArray(R.array.hajjDetailsText)

        val dialog = Dialog(requireActivity(), R.style.DialogSlideAnim)
        val dialogBinding: LayoutMarkerDetailsDialogBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()), R.layout.layout_marker_details_dialog,
                null, false
            )
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.BOTTOM)
        }
        dialogBinding.icClose.setOnClickListener { dialog.dismiss() }

        dialogBinding.hajjDayTitle.text = trackerTitles[tag]
        dialogBinding.hajjDetails.text = details[tag]

        dialog.show()
    }


    private fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(
            resources,
            resources.getIdentifier(iconName, "drawable", requireContext().getPackageName())
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(11.0f)
            .build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null)
    }


    override fun onDestroyView() {
        mCallback?.setToolBarTitle(resources.getString(R.string.cat_hajj))
        super.onDestroyView()
        mMap = null
    }
}