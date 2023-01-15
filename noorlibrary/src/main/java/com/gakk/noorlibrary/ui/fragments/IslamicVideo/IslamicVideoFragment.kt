package com.gakk.noorlibrary.ui.fragments.IslamicVideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.callbacks.SHARE
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentIslamicVideoBinding
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.model.video.category.VideoByGroup
import com.gakk.noorlibrary.ui.activity.VideoPlayerHomeActivity
import com.gakk.noorlibrary.ui.adapter.IslamicVideoHomeAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch

class IslamicVideoFragment : Fragment(), PagingViewCallBack, PlayerCallback {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentIslamicVideoBinding

    private lateinit var videoModel: VideoViewModel
    private lateinit var literatureModel: LiteratureViewModel
    private lateinit var repository: RestRepository

    private lateinit var videosByGroup: MutableList<VideoByGroup>
    private lateinit var videoPlayLists: MutableList<Data>
    var videoList: MutableList<com.gakk.noorlibrary.model.video.category.Data> = mutableListOf()

    private var mPageNo: Int = 0
    private var mHasMoreData = true
    private lateinit var adapter: IslamicVideoHomeAdapter

    private var catId: String? = null
    private var subCatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initPagingProperties()

        lifecycleScope.launch {

            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_islamic_video,
                container,
                false
            )

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            videoModel = ViewModelProvider(
                this@IslamicVideoFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)

            literatureModel = ViewModelProvider(
                this@IslamicVideoFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            videoModel.videoSubCatOrPlayList.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        videosByGroup = it.data ?: mutableListOf()
                        loadData()

                    }
                    Status.LOADING -> {
                        if (mPageNo == 1) {
                            binding.progressLayout.root.visibility = VISIBLE
                        }
                        binding.noInternetLayout.root.visibility = GONE
                    }
                    Status.ERROR -> {
                        Log.e("videoSubCatOrPlayList", "${it.message}")
                        binding.progressLayout.root.visibility = GONE
                        binding.noInternetLayout.root.visibility = VISIBLE
                    }
                }
            })

            literatureModel.subCategoryListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        videoPlayLists = it.data?.data ?: mutableListOf()
                        loadData()
                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = VISIBLE
                        binding.noInternetLayout.root.visibility = GONE
                    }
                    Status.ERROR -> {
                        Log.e("subCategoryListData", "${it.message}")
                        binding.progressLayout.root.visibility = GONE
                        binding.noInternetLayout.root.visibility = VISIBLE
                    }
                }
            })

            videoModel.videoList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.e("videodata", "" + it.data?.data?.size)
                        videoList = it.data?.data!!

                        val intent =
                            Intent(requireContext(), VideoPlayerHomeActivity::class.java)
                        intent.putExtra(VIDEO_CAT_ID, catId)
                        intent.putExtra(VIDEO_SUBCAT_ID, subCatId)
                        intent.putExtra(VIDEO_DATA, videoList.get(0))
                        requireContext().startActivity(intent)
                    }
                    Status.LOADING -> {
                        Log.e("videodata", "Loading")
                    }
                    Status.ERROR -> {
                        Log.e("videodata", "Error")
                    }
                }
            }

            videoModel.loadIslamicVideosAndPlayListsByCatId(ISLAMIC_VIDEO_CAT_ID, "$mPageNo")
            literatureModel.loadSubCategoriesByCatId(ISLAMIC_PLATLIST_CAT_ID, "$mPageNo")


        }

        updateToolbarForThisFragment()

        return binding.root
    }

    fun loadData() {
        if (::videoPlayLists.isInitialized && ::videosByGroup.isInitialized) {
            binding.progressLayout.root.visibility = GONE
            when (mPageNo == 1) {
                true -> {

                    var favouriteVideoGroup: VideoByGroup? = null
                    val firstVideoGrp: MutableList<VideoByGroup> = mutableListOf()
                    for (i in videosByGroup) {
                        if (i?.catId!! == FAV_ISLAMIC_VIDEO_SUB_CAT_ID) {
                            favouriteVideoGroup =
                                VideoByGroup(catId = i.catId, contentList = i.contentList)
                        } else {
                            firstVideoGrp.add(i)
                        }
                    }


                    adapter = IslamicVideoHomeAdapter(
                        firstVideoGrp,
                        favouriteVideoGroup,
                        videoPlayLists,
                        this,
                        detailsCallBack = mDetailsCallBack!!,
                        this
                    )
                    binding.rvVideoHomeList.adapter = adapter
                }
                false -> {
                    if (videosByGroup.size == 0) {
                        mHasMoreData = false
                        adapter.hideFooter()
                    } else {
                        adapter.addItemToList(videosByGroup)
                    }

                }
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            IslamicVideoFragment()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_islamic_video))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeTwo)
        mDetailsCallBack?.setOrUpdateActionButtonTag(SHARE, ActionButtonType.TypeTwo)
    }

    override fun initPagingProperties() {
        mPageNo = 1
        mHasMoreData = true
    }

    override fun loadNextPage() {
        mPageNo++
        videoModel.loadIslamicVideosAndPlayListsByCatId(ISLAMIC_VIDEO_CAT_ID, "$mPageNo")
    }

    override fun hasMoreData() = mHasMoreData


    override fun gotoVideoPlayerPage(mcatId: String?, mSubCatId: String?) {
        catId = mcatId
        subCatId = mSubCatId

        Log.e("ItemClick", "catId$mcatId subCat$mSubCatId")
        videoModel.loadIslamicVideosByCatId(
            mcatId!!,
            mSubCatId!!,
            "1"
        )
    }
}

interface PlayerCallback {
    fun gotoVideoPlayerPage(mcatId: String?, mSubCatId: String?)
}

