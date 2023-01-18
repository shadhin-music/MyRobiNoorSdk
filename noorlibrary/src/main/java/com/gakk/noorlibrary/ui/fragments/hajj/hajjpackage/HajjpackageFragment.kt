package com.gakk.noorlibrary.ui.fragments.hajj.hajjpackage

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
import com.gakk.noorlibrary.databinding.ItemHajjPackagesBinding
import com.gakk.noorlibrary.databinding.PackageFragmentHBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.base.BaseAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class HajjpackageFragment : Fragment() {

    private lateinit var binding: PackageFragmentHBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel
    private lateinit var modelLiterature: LiteratureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjpackageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.package_fragment_h, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        updateToolbarForThisFragment()

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjpackageFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)


            modelLiterature = ViewModelProvider(
                this@HajjpackageFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            modelLiterature.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.hajj_package_id),
                SUB_CAT_ID_UNDEFINED,
                "1"
            )

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {

        modelLiterature.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val mAdapter = BaseAdapter<Literature>()
                    val literatureList = it.data?.data ?: mutableListOf()
                    mAdapter.listOfItems = literatureList

                    mAdapter.expressionViewHolderBinding = { eachItem, positionItem, viewBinding ->
                        val view = viewBinding as ItemHajjPackagesBinding
                        view.listItem = eachItem
                        view.root.setOnClickListener {
                            val fragment = FragmentProvider.getFragmentByName(
                                HAJJ_PACKAGE_DETAILS,
                                detailsActivityCallBack = mDetailsCallBack,
                                literature = eachItem
                            )
                            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                        }
                    }

                    mAdapter.expressionOnCreateViewHolder = { viewGroup ->

                        ItemHajjPackagesBinding.inflate(
                            LayoutInflater.from(viewGroup.context),
                            viewGroup,
                            false
                        )
                    }

                    binding.rvPackagesHajj.adapter = mAdapter
                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle("হজ্জ প্যাকেজ")
    }
}