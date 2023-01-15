package com.gakk.noorlibrary.ui.fragments.covid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentIslamicNameTabBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.covid.AmbulancePagerAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

class AmbulanceTabFragment : Fragment() {

    private lateinit var binding: FragmentIslamicNameTabBinding
    private lateinit var mPageTitles: Array<String>
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var model: LiteratureViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()
    private lateinit var repository: RestRepository
    private var markerOptions: Array<MarkerOptions>? = null
    private var bitmapDescriptor: BitmapDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(requireContext())
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AmbulanceTabFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_islamic_name_tab, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.title_ambulance_service))

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_ambulance_marker)

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@AmbulanceTabFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.corona_funeral_id.getLocalisedTextFromResId(),
                R.string.ambulance_subcategory_id.getLocalisedTextFromResId(),
                "1"
            )

            subscribeObserver()

        }

        mPageTitles = arrayOf(
            requireContext().resources.getString(R.string.ambulace_list),
            requireContext().resources.getString(R.string.see_map)
        )

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

                    markerOptions = Array(literatureList.size) { MarkerOptions() }
                    for (i in 0..literatureList.size - 1) {

                        markerOptions!![i] = MarkerOptions()
                            .position(
                                LatLng(
                                    literatureList[i].latitude?.toDouble()!!,
                                    literatureList[i].longitude?.toDouble()!!
                                )
                            )
                            .title(literatureList[i].title)
                            .snippet(literatureList[i].title)
                            .icon(bitmapDescriptor)
                    }

                    binding.pager.adapter =
                        AmbulancePagerAdapter(
                            childFragmentManager,
                            mPageTitles,
                            mDetailsCallBack,
                            literatureList,
                            markerOptions
                        )
                    binding.tabLayout.setupWithViewPager(binding.pager)
                    mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)


                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.title_ambulance_service))
    }
}