package com.gakk.noorlibrary.ui.fragments.hajj.hajjguide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.adapter.hajjguide.HajjGuideAdapter
import com.gakk.noorlibrary.util.PAGE_HAJJ_GUIDE
import com.gakk.noorlibrary.util.PAGE_HAJJ_PRE_REGISTRATION
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.SUB_CAT_ID_UNDEFINED
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private val ARG_PARAM_CALLBACK = "paramCallBack"

internal class HajjGuideDialoFragment : Fragment() {

    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private lateinit var imageChangeListener: ImageChangeListener
    private lateinit var rvStep: RecyclerView
    private lateinit var modelUserTracking: AddUserTrackigViewModel

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

        val view = inflater.inflate(
            R.layout.fragment_hajj_guide_dialog,
            container, false
        )
        rvStep = view.findViewById(R.id.rvStep)

        return view

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

            modelUserTracking = ViewModelProvider(
                this@HajjGuideDialoFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)


            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, PAGE_HAJJ_GUIDE)
            }

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
                }
                Status.SUCCESS -> {
                    val literatureList = it.data?.data ?: mutableListOf()
                    Log.e("list", "ss" + literatureList.size)
                    val sortedList =
                        literatureList.sortedByDescending { literatureList.indexOf(it) }
                            .toMutableList()
                    val adapter = HajjGuideAdapter(sortedList, imageChangeListener)
                    rvStep.adapter = adapter
                }
                Status.ERROR -> {
                }
            }
        }

        modelUserTracking.trackUser.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("trackUser", "LOADING")
                }
                Status.ERROR -> {
                    Log.e("trackUser", "ERROR")
                }

                Status.SUCCESS -> {
                    Log.e("trackUser", "SUCCESS")
                }
            }
        }
    }
}