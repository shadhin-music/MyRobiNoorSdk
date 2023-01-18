package com.gakk.noorlibrary.ui.fragments.quranSchool

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentScholarsListBinding
import com.gakk.noorlibrary.model.quranSchool.Scholar
import com.gakk.noorlibrary.ui.adapter.ScholarListAdapter
import com.gakk.noorlibrary.util.PAGE_QURAN_SCHOOL
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.QuranSchoolViewModel
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/4/2021, Sun
 */

internal class ScholarsListFragment : Fragment() {
    private var mCallback: DetailsCallBack? = null
    private var mainCallback: MainCallback? = null
    private lateinit var binding: FragmentScholarsListBinding


    private lateinit var viewModel: QuranSchoolViewModel
    private lateinit var repository: RestRepository


    companion object {
        @JvmStatic
        fun newInstance(callback: DetailsCallBack? = null, mainCallback: MainCallback? = null) =
            ScholarsListFragment().apply {
                arguments = Bundle().apply {
                   // putSerializable(ARG_DETAILS_CALL_BACK, callback)
                    //putSerializable(ARG_MAIN_CALL_BACK, mainCallback)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      /*  arguments?.let {
            mCallback = it.getSerializable(ARG_DETAILS_CALL_BACK) as? DetailsCallBack
            mainCallback = it.getSerializable(ARG_MAIN_CALL_BACK) as? MainCallback
        }*/

        mCallback = requireActivity() as DetailsCallBack
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_scholars_list, container, false)

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewModel = ViewModelProviders.of(
                this@ScholarsListFragment,
                QuranSchoolViewModel.FACTORY(repository)

            ).get(QuranSchoolViewModel::class.java)

            initViews()
            subscribeObserver()
        }

        return binding.root
    }

    private fun subscribeObserver() {
        viewModel.scholarsLiveData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("scholars", "loading")
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    Log.e("scholars", "success")
                    binding.progressLayout.root.visibility = View.GONE
                    val scholars = it.data?.scholarsResponse
                    updateScholarsView(scholars)
                }

                Status.ERROR -> {
                    Log.e("scholars", "error${it.message}")
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
        )

        viewModel.getAllScholars()
    }

    private fun updateScholarsView(scholars: List<Scholar>?) {
        scholars?.let {
            binding.apply {
                scholarsRv.apply {
                    setHasFixedSize(true)
                    adapter = ScholarListAdapter().apply {
                        submitList(it)
                        setOnItemClickListener { scholar ->
                            mCallback?.let {
                                it.addFragmentToStackAndShow(
                                    QuranSchoolHomeFragment.newInstance(
                                        scholar
                                    )
                                )
                            } ?: kotlin.run {
                                mainCallback?.openDetailsActivityWithPageName(
                                    PAGE_QURAN_SCHOOL,
                                    scholar = scholar,
                                  //  literatures = literatureListAdapter?.getLiteratureList()
                                )
                            }


                        }
                    }
                }
            }
        }
    }


    private fun initViews() {
        mCallback?.setToolBarTitle(getString(R.string.cat_quran_school))
    }

}