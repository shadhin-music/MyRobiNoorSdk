package com.gakk.noorlibrary.ui.fragments.hajj.hajjguide

import android.content.Context
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
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentHajjGuideDialogBinding
import com.gakk.noorlibrary.ui.adapter.hajjguide.HajjGuideAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.SUB_CAT_ID_UNDEFINED
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private val ARG_PARAM_CALLBACK = "paramCallBack"

class HajjGuideDialoFragment : Fragment() {
    private lateinit var binding: FragmentHajjGuideDialogBinding
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private lateinit var imageChangeListener: ImageChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            imageChangeListener = it?.getSerializable(ARG_PARAM_CALLBACK) as ImageChangeListener
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            imageChangeListener: ImageChangeListener?
        ) =
            HajjGuideDialoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM_CALLBACK, imageChangeListener)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hajj_guide_dialog, container, false)

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
                this@HajjGuideDialoFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.hajj_guide_category_id),
                SUB_CAT_ID_UNDEFINED,
                "1"
            )

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    //binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val literatureList = it.data?.data ?: mutableListOf()
                    Log.e("list", "ss" + literatureList.size)
                    val sortedList =
                        literatureList.sortedByDescending { literatureList.indexOf(it) }
                            .toMutableList()
                    val adapter = HajjGuideAdapter(sortedList, imageChangeListener)
                    binding.rvStep.adapter = adapter
                    /*binding.progressLayout.root.visibility = View.GONE*/
                }
                Status.ERROR -> {
                    // binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }
}