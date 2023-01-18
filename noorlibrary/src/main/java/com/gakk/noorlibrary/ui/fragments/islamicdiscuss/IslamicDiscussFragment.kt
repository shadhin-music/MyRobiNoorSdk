package com.gakk.noorlibrary.ui.fragments.islamicdiscuss

import android.annotation.SuppressLint
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
import com.gakk.noorlibrary.databinding.FragmentIslamicDiscussBinding
import com.gakk.noorlibrary.ui.adapter.podcast.IslamicDiscussAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class IslamicDiscussFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentIslamicDiscussBinding
    private lateinit var modelLiterature: LiteratureViewModel
    private lateinit var repository: RestRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            IslamicDiscussFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_islamic_discuss, container, false)

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDetailsCallBack?.setToolBarTitle("ইসলামী আলোচনা")


        lifecycleScope.launch {


            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            modelLiterature = ViewModelProvider(
                this@IslamicDiscussFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            modelLiterature.loadImageBasedLiteratureListBySubCategory(
                "622f0159803daefd93291ba3",
                "undefined",
                "1"
            )

            subscribeObserver()
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeObserver() {
        modelLiterature.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("Islamicdiscuss", "LOADING")
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Log.e("Islamicdiscuss", "SUCCESS")
                    val imageList = it.data?.data
                    binding.rvDiscuss.adapter = imageList?.let { it1 -> IslamicDiscussAdapter(it1) }

                    binding.progressLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    Log.e("Islamicdiscuss", "ERROR")
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }
}