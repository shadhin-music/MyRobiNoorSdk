package com.gakk.noorlibrary.ui.fragments.covid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentAmbulaceListBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.covid.AmbulanceListAdapter
import com.gakk.noorlibrary.util.setApplicationLanguage
import java.io.Serializable

private val ARG_PARAM_AMBULANCE_LIST = "ambulanceList"

internal class AmbulanceListFragment : Fragment(), MapOpenController {

    private lateinit var binding: FragmentAmbulaceListBinding
    private var mCallback: DetailsCallBack? = null
    private var literatureList: MutableList<Literature> = mutableListOf()

    companion object {

        @JvmStatic
        fun newInstance(ambulancelist: MutableList<Literature>) =
            AmbulanceListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM_AMBULANCE_LIST, ambulancelist as Serializable)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            literatureList = it.getSerializable(ARG_PARAM_AMBULANCE_LIST) as MutableList<Literature>
        }

        mCallback = requireActivity() as DetailsCallBack
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ambulace_list,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        binding.rvAmbulance.adapter =
            AmbulanceListAdapter(literatureList, mCallback!!, this)
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
                    mCallback?.showToastMessage("Please allow permission to get accurate info!")
                }
                return
            }
        }
    }

    override fun openMap(lat: Double, log: Double) {
        openLocationInMap(lat, log)
    }

    fun openLocationInMap(lat: Double, log: Double) {
        try {
            val uri =
                "http://maps.google.com/maps?saddr=" + AppPreference.getUserCurrentLocation().lat + "," + AppPreference.getUserCurrentLocation().lng + "&daddr=" + lat + "," + log
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface MapOpenController {
    fun openMap(lat: Double, log: Double)
}