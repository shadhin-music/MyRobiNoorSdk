package com.gakk.noorlibrary.ui.fragments.islamicSong

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
import androidx.lifecycle.ViewModelProviders
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
import com.gakk.noorlibrary.ui.adapter.IslamicSongHomeAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch


private const val ARG_DETAILS_CALL_BACK = "detailsCallBack"


internal class IslamicSongFragment : Fragment(), PagingViewCallBack {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentIslamicVideoBinding

    private lateinit var videoModel: VideoViewModel
    private lateinit var literatureModel: LiteratureViewModel
    private lateinit var repository: RestRepository

    private lateinit var videosByGroup: MutableList<VideoByGroup>
    private lateinit var videoPlayLists: MutableList<Data>

    private var mPageNo: Int = 0
    private var mHasMoreData = true
    private lateinit var adapter: IslamicSongHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            mDetailsCallBack = it.getSerializable(ARG_DETAILS_CALL_BACK) as DetailsCallBack

        }*/
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

            videoModel = ViewModelProviders.of(
                this@IslamicSongFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)

            literatureModel = ViewModelProviders.of(
                this@IslamicSongFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            videoModel.videoSubCatOrPlayList.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        videosByGroup = it.data ?: mutableListOf()
                        Log.e("songs","success"+videosByGroup.size)
                        loadData()

                    }
                    Status.LOADING -> {
                        Log.e("songs","LOADING")

                        if (mPageNo == 1) {
                            binding.progressLayout.root.visibility = VISIBLE
                        }
                        binding.noInternetLayout.root.visibility = GONE
                    }
                    Status.ERROR -> {
                        Log.e("songs","ERROR")

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
                        binding.progressLayout.root.visibility = GONE
                        binding.noInternetLayout.root.visibility = VISIBLE
                    }
                }
            })

            videoModel.loadIslamicAudiosAndPlayListsByCatId(ISLAMIC_SONG_CAT_ID, "$mPageNo")
        }

        updateToolbarForThisFragment()

        return binding.root
    }

    fun loadData() {
        if (::videoPlayLists.isInitialized && ::videosByGroup.isInitialized) {
            binding.progressLayout.root.visibility = GONE
            when (mPageNo == 1) {
                true -> {

                    val firstVideoGrp: MutableList<VideoByGroup> = mutableListOf()
                    for (i in videosByGroup) {
                        firstVideoGrp.add(i)
                       /* if (i?.catId!! == FAV_ISLAMIC_VIDEO_SUB_CAT_ID) {
                            favouriteVideoGroup =
                                VideoByGroup(catId = i.catId, contentList = i.contentList)
                        } else {
                            firstVideoGrp.add(i)
                        }*/
                    }

                    Log.e("ddd","check"+firstVideoGrp.size)
                    adapter = IslamicSongHomeAdapter(
                        firstVideoGrp,
                        this,
                        detailsCallBack = mDetailsCallBack!!
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
        fun newInstance(detailsCallBack: DetailsCallBack) =
            IslamicSongFragment().apply {
               /* arguments = Bundle().apply {
                    putSerializable(ARG_DETAILS_CALL_BACK, detailsCallBack)
                }*/
            }


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
        videoModel.loadIslamicAudiosAndPlayListsByCatId(ISLAMIC_SONG_CAT_ID ,"$mPageNo")
    }

    override fun hasMoreData() = mHasMoreData


}

