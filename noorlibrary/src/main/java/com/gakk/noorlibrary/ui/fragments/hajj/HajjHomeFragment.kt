package com.gakk.noorlibrary.ui.fragments.hajj

import android.content.Intent
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
import com.gakk.noorlibrary.databinding.FragmentHajjHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.activity.KafelaPlayerActivity
import com.gakk.noorlibrary.ui.adapter.HajjCategoryAdapter
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPreRegistrationFragment
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch


internal class HajjHomeFragment : Fragment() {

    private lateinit var binding: FragmentHajjHomeBinding
    private var mCallback: DetailsCallBack? = null

    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_hajj_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()

        if (AppPreference.language.equals(LAN_BANGLA)) {
            binding.hajjPreRegistration.visibility = View.VISIBLE
            binding.virtualKafela.visibility = View.VISIBLE
        }

        binding.item = ImageFromOnline("hajj_page_top_image.png")

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjHomeFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
            loadData()

            binding.noInternetLayout.btnRetry.handleClickEvent {
                loadData()
            }

        }
        binding.hajjPreRegistration.handleClickEvent {
            mCallback?.addFragmentToStackAndShow(
                HajjPreRegistrationFragment.newInstance()
            )
        }

        binding.virtualKafela.handleClickEvent {
            if (isNetworkConnected(requireContext())) {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        KafelaPlayerActivity::class.java
                    )
                )
            } else {
                mCallback?.showToastMessage(getString(R.string.txt_check_internet))
            }

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
                    val subCatId = requireContext().getString(R.string.hajj_guide_sub_category_id)
                    val filteredList =
                        list.filter { it.id != subCatId } as MutableList<Data>

                    val sortedListRobi =
                        list.sortedByDescending { list.indexOf(it) }
                            .toMutableList()

                    setUpRV(sortedListRobi)
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
        binding.subCatRv.apply {
            adapter = HajjCategoryAdapter().apply {
                submitList(list)
                setOnItemClickListener {
                    Log.d("itemClicked", "setUpRV: " + it.id)

                    val mapCat = requireContext().getString(R.string.hajj_map_cateogry_id)
                    val currencyCat = requireContext().getString(R.string.hajj_currency_cateogry_id)
                    val hajjGuideCat =
                        requireContext().getString(R.string.hajj_guide_sub_category_id)
                    it.category?.let { catID ->
                        when (it.id) {
                            mapCat -> {

                                when (Util.checkSub()) {
                                    true -> {
                                        mCallback?.addFragmentToStackAndShow(
                                            HajjMapFragment.newInstance()
                                        )
                                    }
                                    else -> {
                                        requireContext().startActivity(
                                            Intent(requireContext(), DetailsActivity::class.java)
                                                .putExtra(PAGE_NAME, PAGE_SUBSCRIPTION)
                                        )
                                    }
                                }

                            }
                            currencyCat -> {
                                mCallback?.addFragmentToStackAndShow(
                                    CurrencyConverterFragment.newInstance()
                                )
                            }

                            hajjGuideCat -> {
                                Log.e("hajjGuide", "Called")

                                if (Util.checkSub()) {
                                    if (isNetworkConnected(requireContext())) {
                                        val fragment = FragmentProvider.getFragmentByName(
                                            PAGE_HAJJ_GUIDE,
                                            detailsActivityCallBack = mCallback
                                        )
                                        mCallback?.addFragmentToStackAndShow(fragment!!)
                                    } else {
                                        mCallback?.showToastMessage(getString(R.string.txt_check_internet))
                                    }
                                } else {
                                    requireContext().startActivity(
                                        Intent(requireContext(), DetailsActivity::class.java)
                                            .putExtra(PAGE_NAME, PAGE_SUBSCRIPTION)
                                    )
                                }
                            }

                            else -> {
                                val fragment = FragmentProvider.getFragmentByName(
                                    name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                    detailsActivityCallBack = mCallback,
                                    catId = catID,
                                    subCatId = it.id,
                                    isFav = false,
                                    /*pageTitle = category.name*/
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
            HajjHomeFragment()
    }

    fun loadData() {
        val mCatId = requireContext().resources.getString(R.string.hajj_cateogry_id)
        model.loadSubCategoriesByCatId(mCatId, "1")
    }
}