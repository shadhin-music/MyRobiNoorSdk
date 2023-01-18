package com.gakk.noorlibrary.ui.fragments.covid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentCovidServiceHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.covid.CovidServiceHomeAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


internal class CovidServiceHomeFragment : Fragment() {

    private lateinit var binding: FragmentCovidServiceHomeBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var literatureList: MutableList<Literature> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CovidServiceHomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_covid_service_home,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDetailsCallBack?.setToolBarTitle(getString(R.string.cat_corona_funeral_service))

        binding.item = ImageFromOnline("ic_img_ambulance.png")

        binding.layoutTitleView.tvTitle.setText(getString(R.string.title_ambulance_service))
        binding.layoutTitleView.tvTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.txt_color_title
            )
        )

        binding.layoutCoronaTitleView.tvTitle.setText(getString(R.string.cat_corona_funeral_service))
        binding.layoutCoronaTitleView.tvTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.txt_color_title
            )
        )

        binding.ivAmbulance.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_AMBULANCE, detailsActivityCallBack = mDetailsCallBack
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@CovidServiceHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.corona_funeral_id.getLocalisedTextFromResId(),
                R.string.corona_funeral_subcategory_id.getLocalisedTextFromResId(),
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

                    binding.rvService.adapter =
                        CovidServiceHomeAdapter(literatureList, mDetailsCallBack!!)

                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }
}