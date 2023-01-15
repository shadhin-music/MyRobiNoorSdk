package com.gakk.noorlibrary.ui.fragments.onlinehut

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
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentOnlineHutHomeBinding
import com.gakk.noorlibrary.ui.adapter.hut.OnlineHutHomeAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

class OnlintHutHomeFragment : Fragment() {

    private lateinit var binding: FragmentOnlineHutHomeBinding
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var mCallback: DetailsCallBack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OnlintHutHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_online_hut_home,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.title_online_hut))

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@OnlintHutHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.online_hut_category_id),
                getString(R.string.online_hut_subcategory_id),
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
                    val literatureList = it.data?.data ?: mutableListOf()
                    val adapter = OnlineHutHomeAdapter(literatureList, mCallback!!)
                    binding.rvHut.adapter = adapter
                    binding.progressLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }
}