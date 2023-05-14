package com.gakk.noorlibrary.ui.fragments.eidjamat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.eidjamat.EidJamatAdapter
import com.gakk.noorlibrary.util.PAGE_EID_JAMAT
import com.gakk.noorlibrary.util.PermissionManager
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class EidJamatFragment : Fragment(), MapOpenControllerJamat {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private lateinit var modelUserTracking: AddUserTrackigViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()

    //view

    private lateinit var progressLayout: ConstraintLayout
    private lateinit var rvEidJamat: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EidJamatFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_eid_jamat,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view: View) {
        progressLayout = view.findViewById(R.id.progressLayout)
        rvEidJamat = view.findViewById(R.id.rvEidJamat)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_eid_jamat))

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@EidJamatFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            modelUserTracking = ViewModelProvider(
                this@EidJamatFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.eid_jamat_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, PAGE_EID_JAMAT)
            }

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()
                    val sortedList: MutableList<Literature> =
                        literatureList.sortedByDescending { literatureList.indexOf(it) }
                            .toMutableList()

                    rvEidJamat.adapter =
                        EidJamatAdapter(sortedList, mDetailsCallBack!!, this)
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

    override fun openMap(lat: Double, log: Double) {
        if (PermissionManager.isLocationPermissionGiven(requireContext())) {
            openLocationInMap(lat, log)
        } else {
            PermissionManager.requestPermissionForLocation(
                requireActivity()
            ) {
                openLocationInMap(lat, log)
            }
        }
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

interface MapOpenControllerJamat {
    fun openMap(lat: Double, log: Double)
}