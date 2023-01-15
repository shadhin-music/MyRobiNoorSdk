package com.gakk.noorlibrary.ui.fragments.onlinehut

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
import com.gakk.noorlibrary.databinding.FragmentHajjPreRegistrationListBinding
import com.gakk.noorlibrary.databinding.ItemLocationwiseHutBinding
import com.gakk.noorlibrary.databinding.LayoutItemLocationwiseHutBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.base.BaseAdapter
import com.gakk.noorlibrary.util.CITY_NAME_NORTH
import com.gakk.noorlibrary.util.PermissionManager
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private const val ARG_CITY_TYPE = "cityName"

class OnlineHutLocationwiseFrgment : Fragment() {

    private lateinit var binding: FragmentHajjPreRegistrationListBinding
    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var mCityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCityName = it.getString(ARG_CITY_TYPE)
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(
            cityName: String
        ) =
            OnlineHutLocationwiseFrgment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CITY_TYPE, cityName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hajj_pre_registration_list,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@OnlineHutLocationwiseFrgment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            val subCatId: String
            if (mCityName.equals(CITY_NAME_NORTH)) {
                subCatId = getString(R.string.online_hut_north_subcategory_id)
            } else {
                subCatId = getString(R.string.online_hut_sounth_subcategory_id)
            }

            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.online_hut_category_id),
                subCatId,
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
                Status.SUCCESS -> {
                    val mAdapter = BaseAdapter<Literature>()
                    val literatureList = it.data?.data ?: mutableListOf()
                    mAdapter.listOfItems = literatureList
                    mAdapter.expressionViewHolderBinding = { eachItem, positionItem, viewBinding ->

                        val view = viewBinding as ItemLocationwiseHutBinding
                        view.model = eachItem
                        view.root.setOnClickListener {
                            if (PermissionManager.isLocationPermissionGiven(requireContext())) {
                                openLocationInMap(
                                    eachItem.latitude?.toDouble()!!,
                                    eachItem.longitude?.toDouble()!!
                                )
                            } else {
                                PermissionManager.requestPermissionForLocation(
                                    requireActivity()
                                ) {
                                    openLocationInMap(
                                        eachItem.latitude?.toDouble()!!,
                                        eachItem.longitude?.toDouble()!!
                                    )
                                }
                            }
                        }
                    }

                    mAdapter.expressionOnCreateViewHolder = { viewGroup ->

                        ItemLocationwiseHutBinding.inflate(
                            LayoutInflater.from(viewGroup.context),
                            viewGroup,
                            false
                        )
                    }

                    binding.rvPreregistration.adapter = mAdapter
                    binding.progressLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
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