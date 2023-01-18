package com.gakk.noorlibrary.ui.fragments.eidjamat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentEidJamatBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.eidjamat.EidJamatAdapter
import com.gakk.noorlibrary.util.PermissionManager
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class EidJamatFragment : Fragment(), MapOpenControllerJamat {

    private lateinit var binding: FragmentEidJamatBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()

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
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_eid_jamat, container, false)

        return binding.root
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


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.eid_jamat_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()
                    val sortedList: MutableList<Literature> =
                        literatureList.sortedByDescending { literatureList.indexOf(it) }
                            .toMutableList()

                    binding.rvEidJamat.adapter =
                        EidJamatAdapter(sortedList, mDetailsCallBack!!, this)
                    binding.progressLayout.root.visibility = View.GONE
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