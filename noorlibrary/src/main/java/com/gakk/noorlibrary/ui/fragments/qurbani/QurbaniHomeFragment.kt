package com.gakk.noorlibrary.ui.fragments.qurbani

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
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.databinding.FragmentQurbaniHomeBinding
import com.gakk.noorlibrary.databinding.LayoutLiteratureBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.VideoPlayerHomeActivity
import com.gakk.noorlibrary.ui.adapter.base.BaseAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch

internal class QurbaniHomeFragment : Fragment() {

    private lateinit var binding: FragmentQurbaniHomeBinding
    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private lateinit var videoModel: VideoViewModel
    private var literatureListWrapper: LiteratureListWrapper? = null
    var videoList: MutableList<Data> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(
        ) =
            QurbaniHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_qurbani_home,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        updateToolbarForThisFragment()

        binding.item = ImageFromOnline("qurbani_header.png")

        if (AppPreference.language.equals(LAN_BANGLA)) {
            binding.clHut.visibility = View.VISIBLE

            binding.clHut.handleClickEvent {
                val fragment = FragmentProvider.getFragmentByName(
                    ONLINE_HUT_HOME,
                    detailsActivityCallBack = mCallback
                )
                mCallback?.addFragmentToStackAndShow(fragment!!)
            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@QurbaniHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            videoModel = ViewModelProvider(
                this@QurbaniHomeFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)

            if (AppPreference.language.equals(LAN_BANGLA)) {
                videoModel.loadIslamicVideosByCatId(
                    getString(R.string.qurbani_cateogry_id),
                    SUB_CAT_ID_UNDEFINED,
                    "1"
                )
            } else {
                model.loadTextBasedLiteratureListBySubCategory(
                    getString(R.string.qurbani_cateogry_id),
                    SUB_CAT_ID_UNDEFINED,
                    "1"
                )
            }

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {

        videoModel.videoList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("videodata", "" + it.data?.data?.size)
                    videoList = it.data?.data!!
                    model.loadTextBasedLiteratureListBySubCategory(
                        getString(R.string.qurbani_cateogry_id),
                        SUB_CAT_ID_UNDEFINED,
                        "1"
                    )
                }
                Status.LOADING -> {
                    Log.e("videodata", "Loading")
                }
                Status.ERROR -> {
                    Log.e("videodata", "Error")
                }
            }
        }

        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val mAdapter = BaseAdapter<Literature>()
                    val literatureList = it.data?.data ?: mutableListOf()
                    mAdapter.listOfItems = literatureList
                    mAdapter.expressionViewHolderBinding = { eachItem, positionItem, viewBinding ->

                        val view = viewBinding as LayoutLiteratureBinding
                        view.literature = eachItem
                        view.root.setOnClickListener {
                            val qurbaniDiscussTitle = eachItem.title?.trim()?.replace(" ", "")

                            if (qurbaniDiscussTitle.equals("কুরবানিবিষয়কআলোচনা")) {
                                val intent =
                                    Intent(requireContext(), VideoPlayerHomeActivity::class.java)
                                intent.putExtra(
                                    VIDEO_CAT_ID,
                                    R.string.qurbani_cateogry_id.getLocalisedTextFromResId()
                                )
                                intent.putExtra(VIDEO_SUBCAT_ID, SUB_CAT_ID_UNDEFINED)
                                intent.putExtra(VIDEO_DATA, videoList.get(0))
                                requireContext().startActivity(intent)
                            } else {

                                literatureListWrapper = LiteratureListWrapper(literatureList)

                                val fragment = FragmentProvider.getFragmentByName(
                                    name = PAGE_LITERATURE_DETAILS,
                                    detailsActivityCallBack = mCallback,
                                    literatureListWrapper = literatureListWrapper,
                                    selectedLiteratureIndex = positionItem,
                                    currentPageNo = 1,
                                    isFav = false
                                )

                                if (mCallback != null) {
                                    mCallback?.addFragmentToStackAndShow(fragment!!)
                                }
                            }
                        }
                    }

                    mAdapter.expressionOnCreateViewHolder = { viewGroup ->

                        LayoutLiteratureBinding.inflate(
                            LayoutInflater.from(viewGroup.context),
                            viewGroup,
                            false
                        )
                    }

                    binding.rvLiteratureList.adapter = mAdapter
                    binding.progressLayout.root.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    fun updateToolbarForThisFragment() {
        mCallback?.setToolBarTitle(resources.getString(R.string.cat_qurbani))
        mCallback?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mCallback?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }
}