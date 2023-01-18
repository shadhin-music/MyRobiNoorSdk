package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.util.Log
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
import com.gakk.noorlibrary.databinding.FragmentHajjPreRegistrationHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.ui.adapter.HajjCategoryAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

internal class HajjPreRegistrationFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentHajjPreRegistrationHomeBinding
    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel
    private lateinit var viewModel: PreregistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hajj_pre_registration_home,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        binding.item = ImageFromOnline("ic_hajj_header_image.png")

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjPreRegistrationFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
            loadData()

            binding.noInternetLayout.btnRetry.handleClickEvent {
                loadData()
            }

        }
        binding.btnHajjPreReg.handleClickEvent {
            viewModel.gotoNext(0)
            mCallback?.addFragmentToStackAndShow(
                HajjpreRegistrationDetailsFragment.newInstance()
            )
        }
    }

    fun updateToolbarForThisFragment() {
        mCallback?.setToolBarTitle(resources.getString(R.string.cat_hajj))
    }

    private fun subscribeObserver() {
        model.subCategoryListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    binding.noInternetLayout.root.visibility = View.GONE

                    val list = it.data?.data ?: mutableListOf()

                    val filteredList =
                        list.filter { it.id == "625d20b93e65c410063b7360" || it.id == "625d20db3e65c410063b7361" || it.id == "625d21403e65c410063b7364" || it.id == "625d20ec3e65c410063b7362" } as MutableList<Data>

                    setUpRV(filteredList)
                }
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                    binding.noInternetLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                    binding.noInternetLayout.root.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpRV(list: MutableList<Data>) {
        binding.rvOthersCat.apply {
            adapter = HajjCategoryAdapter().apply {
                submitList(list)
                setOnItemClickListener {

                    val preRegistrationsId = "625d20b93e65c410063b7360"

                    it.category?.let { catID ->
                        when (it.id) {
                            preRegistrationsId -> {
                                mCallback?.addFragmentToStackAndShow(
                                    HajjPreRegistrationListFragment.newInstance()
                                )
                            }

                            else -> {
                                val fragment = FragmentProvider.getFragmentByName(
                                    name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                    detailsActivityCallBack = mCallback,
                                    catId = catID,
                                    subCatId = it.id,
                                    isFav = false,
                                    pageTitle = it.name
                                )
                                fragment?.let { it1 -> mCallback?.addFragmentToStackAndShow(it1) }
                            }
                        }
                    }

                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjPreRegistrationFragment()
    }

    fun loadData() {
        val mCatId =
            requireContext().resources.getString(R.string.hajj_pre_registration_category_id)
        model.loadSubCategoriesByCatId(mCatId, "1")
    }
}