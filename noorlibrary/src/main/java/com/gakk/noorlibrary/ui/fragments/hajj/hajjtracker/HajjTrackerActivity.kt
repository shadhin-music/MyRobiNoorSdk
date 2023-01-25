package com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.hajjtracker.HajjSharingListResponse
import com.gakk.noorlibrary.service.DataUpdate
import com.gakk.noorlibrary.service.HajjLocationShareService
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


internal class HajjTrackerActivity : BaseActivity(), OnMapReadyCallback, BottomSheetDisplay,
    DataUpdate {

    private var mMap: GoogleMap? = null
    private lateinit var model: HajjViewModel
    private lateinit var repository: RestRepository
    private var sharerPhone: String? = null
    private var segment: String? = null
    private var mBound: Boolean = false
    private lateinit var mService: HajjLocationShareService
    private lateinit var shareFragment: ShareDialogFragment
    private lateinit var trackerFragment: TrackerDialogFragment
    lateinit var latLng: LatLng
    private var nameUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_hajj_tracker)

        MapsInitializer.initialize(this)

        checkPermission()

        val options = GoogleMapOptions()
        options.zoomControlsEnabled(true).compassEnabled(true)
        val fragment: SupportMapFragment = SupportMapFragment.newInstance()
        var transaction: FragmentTransaction? = null

        supportFragmentManager.let {
            transaction = supportFragmentManager.beginTransaction().replace(R.id.flMap, fragment)
        }


        transaction?.commit()

        fragment.getMapAsync(this)

        shareFragment = ShareDialogFragment()
        trackerFragment = TrackerDialogFragment()

        intent.let {
            if (intent.getStringExtra("Tacker").equals("notification")) {
                sharerPhone = intent.getStringExtra(USER_NUMBER)
                segment = intent.getStringExtra(HAJJ_TRACKER_SEGMENT_TAG)
                intent.getStringExtra(USER_NAME_TAG)?.let { it1 -> showTrackRequestDialog(it1) }
            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjTrackerActivity,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as HajjLocationShareService.HajjBinder
            mService = binder.getService()
            mBound = true
            mService.myListener = this@HajjTrackerActivity
            Log.i(
                "HajjShareService",
                "onServiceConnected:} }"
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("HajjShareService", "onServiceDisconnected: ${name?.className}")
            mBound = false
        }


    }

    private fun subscribeObserver() {
        model.trackLocation.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("trackLocation", "SUCCESS")
                    if (segment.equals(NOTIFICATION_SEGMENT_VALUE)) {
                        replaceFragment(trackerFragment)
                    } else {
                        model.loadTrackingList()
                    }

                }

                Status.LOADING -> {
                    Log.e("trackLocation", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("trackLocation", "ERROR")
                }
            }
        }

        model.trackList.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    replaceFragment(shareFragment)
                    Log.e("trackList", "SUCCESS${it.data?.data.toString()}")
                    it.data?.data?.let { it1 -> trackerFragment.setListData(it1) }
                }

                Status.LOADING -> {
                    Log.e("trackList", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("trackList", "ERROR")
                }
            }
        }

        model.hajjLocation.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("hajjLocation", "SUCCESS")

                    animateCamera(
                        it.data?.data?.latitude, it.data?.data?.longitude,
                        nameUser
                    )
                }

                Status.LOADING -> {
                    Log.e("hajjLocation", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("hajjLocation", "ERROR")
                }
            }
        }


    }

    private fun checkPermission() {
        if (!PermissionManager.isLocationPermissionGiven(this)) {
            PermissionManager.requestPermissionForLocation(
                this
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        animateCamera(
            AppPreference.getUserCurrentLocation().lat!!,
            AppPreference.getUserCurrentLocation().lng!!,
            ""
        )

        replaceFragment(HajjTrackerHomeDialogFragment())
    }

    private fun animateCamera(lat: Double?, lng: Double?, title: String?) {

        mMap?.clear()
        //Place current location marker
        latLng = LatLng(
            lat!!,
            lng!!
        )
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(title)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_hajj))
        mMap!!.addMarker(markerOptions)
        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    lat, lng
                )
            )
            .zoom(17f)
            .build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null)
    }

    fun replaceFragment(fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            ft.replace(R.id.fargment_host, fragment)
        }
        ft.commit()
    }

    override fun showBottomSheet(type: Int) {

        if (type == 0) {
            replaceFragment(shareFragment)
        } else if (type == 1) {
            replaceFragment(trackerFragment)
        } else {
            replaceFragment(HajjTrackerHomeDialogFragment())
        }
    }

    override fun startLocationShareService() {

        val intent = Intent(this, HajjLocationShareService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun showUserOnMap(msisdn: String?, name: String?) {

        Log.e("HajjTrackerActivity", ": $msisdn");
        nameUser = name
        if (msisdn != null) {
            model.getHajjLocation(msisdn)
        }
    }

    fun showTrackRequestDialog(userName: String) {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )

        val dialogView: View = layoutInflater.inflate(
            R.layout.dialog_hajj_location_track_request,
            null
        )

        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        val tvTitleTrack: AppCompatTextView = dialogView.findViewById(R.id.tvTitleTrack)
        val btnDecline: AppCompatButton = dialogView.findViewById(R.id.btnDecline)
        val btnAccept: AppCompatButton = dialogView.findViewById(R.id.btnAccept)

        tvTitleTrack.setText("$userName wants to share location")
        btnDecline.handleClickEvent {
            alertDialog.dismiss()
        }

        btnAccept.handleClickEvent {
            if (segment.equals(NOTIFICATION_SEGMENT_VALUE)) {
                sharerPhone?.let { model.locationTrackRequestFromTracker(it, "") }
            } else {
                sharerPhone?.let { model.locationTrackRequestFromTracker("", it) }
            }

            alertDialog.dismiss()
        }
    }

    override fun updateData(list: List<HajjSharingListResponse.Data>) {
        Log.e("listSharingactivity", "get$list.toString()")
        shareFragment.setListData(list)
    }
}

interface BottomSheetDisplay {
    fun showBottomSheet(type: Int)
    fun startLocationShareService()
    fun showUserOnMap(msisdn: String?, name: String?)
}