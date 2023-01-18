package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.databinding.LayoutFavIslamicVideosBinding
import com.gakk.noorlibrary.databinding.LayoutFooterBinding
import com.gakk.noorlibrary.databinding.LayoutHorizontalListBinding
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.model.video.category.VideoByGroup
import com.gakk.noorlibrary.ui.activity.VideoPlayerHomeActivity
import com.gakk.noorlibrary.ui.fragments.IslamicVideo.PlayerCallback
import com.gakk.noorlibrary.util.VIDEO_CAT_ID
import com.gakk.noorlibrary.util.VIDEO_DATA
import com.gakk.noorlibrary.util.VIDEO_SUBCAT_ID
import com.gakk.noorlibrary.util.handleClickEvent


internal class IslamicVideoHomeAdapter(
    videoGroups: MutableList<VideoByGroup>,
    favouriteVideoGroup: VideoByGroup?,
    playLists: MutableList<Data>,
    pagingViewCallBack: PagingViewCallBack,
    detailsCallBack: DetailsCallBack,
    val playerCallback: PlayerCallback
) :
    RecyclerView.Adapter<IslamicVideoHomeAdapter.IslamicVideoHomeViewHolder>() {

    val HORIZONTAL_LIST = 0
    val FAVOURITE_VIDEOS = 3
    val FOOTER = 4

    val mPagingViewCallBack = pagingViewCallBack
    var mVideoByGroup = videoGroups
    var mFavouriteVideoGroup = favouriteVideoGroup
    var mPlayList = playLists
    var mViewControl: IslamicVideoHomeAdapterViewControl = IslamicVideoHomeAdapterViewControl()
    val mDetailsCallBack = detailsCallBack


    inner class IslamicVideoHomeViewHolder : RecyclerView.ViewHolder {
        var listBinding: LayoutHorizontalListBinding? = null

        constructor(binding: LayoutHorizontalListBinding) : super(binding.root) {
            listBinding = binding
        }

        var favVideosBinding: LayoutFavIslamicVideosBinding? = null

        constructor(binding: LayoutFavIslamicVideosBinding) : super(binding.root) {
            favVideosBinding = binding
            mFavouriteVideoGroup?.let {
                favVideosBinding?.populateLayout(it)
            }

        }

        var footerBinding: LayoutFooterBinding? = null

        constructor(binding: LayoutFooterBinding) : super(binding.root) {
            footerBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IslamicVideoHomeViewHolder {
        when (viewType) {
            HORIZONTAL_LIST -> {
                val binding: LayoutHorizontalListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_horizontal_list,
                    parent,
                    false
                )
                return IslamicVideoHomeViewHolder(binding)
            }

            FOOTER -> {
                val binding: LayoutFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_footer,
                    parent,
                    false
                )
                return IslamicVideoHomeViewHolder(binding)
            }
            else -> {
                var binding: LayoutFavIslamicVideosBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_fav_islamic_videos,
                    parent,
                    false
                )
                return IslamicVideoHomeViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: IslamicVideoHomeViewHolder, position: Int) {
        holder.listBinding?.let {

            when {
                position == 0 -> {
                    val catId = mVideoByGroup.get(0).contentList.get(
                        0
                    ).category

                    val subcatId = mVideoByGroup.get(0).contentList.get(
                        0
                    ).subcategory

                    it.rvHorizontalList.adapter =
                        IslamicVideoOrPlayListAdapter(
                            videoList = mVideoByGroup.get(0).contentList,
                            detailsCallBack = mDetailsCallBack,
                            playerCallback = playerCallback,
                            videoCatId = catId!!,
                            videoSubCatId = subcatId
                        )
                    it.layoutTitleView.tvTitle.setText(
                        mVideoByGroup.get(0).contentList.get(
                            0
                        ).subcategoryName
                    )
                }

                position == 1 -> {
                    val catId = mPlayList.get(0).category

                    val subcatId = mPlayList.get(position).id
                    it.rvHorizontalList.adapter =
                        IslamicVideoOrPlayListAdapter(
                            playList = mPlayList, detailsCallBack = mDetailsCallBack,
                            playerCallback = playerCallback,
                            videoCatId = catId!!,
                            videoSubCatId = subcatId
                        )
                    it.layoutTitleView.tvTitle.setText(mPlayList.get(0).categoryName)
                }

                position == 2 -> {
                    val catId = mVideoByGroup.get(1).contentList.get(
                        0
                    ).category
                    val subcatId = mVideoByGroup.get(1).contentList.get(
                        0
                    ).subcategory

                    it.rvHorizontalList.adapter =
                        IslamicVideoOrPlayListAdapter(
                            videoList = mVideoByGroup.get(1).contentList,
                            detailsCallBack = mDetailsCallBack,
                            playerCallback = playerCallback,
                            videoCatId = catId!!,
                            videoSubCatId = subcatId
                        )
                    it.layoutTitleView.tvTitle.setText(
                        mVideoByGroup.get(1).contentList.get(
                            0
                        ).subcategoryName
                    )

                }
                else -> {
                    val catId = mVideoByGroup.get(position - 1).contentList.get(
                        0
                    ).category
                    val subcatId = mVideoByGroup.get(position - 1).contentList.get(
                        0
                    ).subcategory

                    it.rvHorizontalList.adapter =
                        IslamicVideoOrPlayListAdapter(
                            videoList = mVideoByGroup.get(position - 1).contentList,
                            detailsCallBack = mDetailsCallBack,
                            playerCallback = playerCallback,
                            videoCatId = catId!!,
                            videoSubCatId = subcatId
                        )
                    it.layoutTitleView.tvTitle.setText(
                        mVideoByGroup.get(position - 1).contentList.get(
                            0
                        ).subcategoryName
                    )

                }

            }


            it.rvHorizontalList.layoutManager =
                LinearLayoutManager(it.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
        holder.footerBinding?.let {
            when (mPagingViewCallBack.hasMoreData()) {
                true -> mPagingViewCallBack.loadNextPage()
                false -> {
                    it.root.layoutParams.height = 0
                }
            }
        }

    }

    override fun getItemCount() = mViewControl.getItemCount(mVideoByGroup)

    override fun getItemViewType(position: Int) =
        mViewControl.getItemViewType(mVideoByGroup, position)


    inner class IslamicVideoHomeAdapterViewControl {
        fun getItemCount(videoGroups: MutableList<VideoByGroup>): Int {
            return videoGroups.size + 2
        }

        fun getItemViewType(videoGroup: MutableList<VideoByGroup>, position: Int): Int {
            when {
                position == 3 -> {
                    Log.e("ADPPOS", "$position->FAVOURITE_VIDEOS")
                    return FAVOURITE_VIDEOS
                }
                videoGroup.size + 1 == position -> {
                    Log.e("ADPPOS", "$position->FOOTER")
                    return FOOTER
                }
                else -> {
                    Log.e("ADPPOS", "$position->VIDEO")
                    return VIDEO
                }
            }
        }
    }

    fun addItemToList(videoGroups: MutableList<VideoByGroup>) {
        var startPos = mVideoByGroup.size + 1
        when (videoGroups.size > 0) {
            true -> {
                mVideoByGroup.addAll(videoGroups)
                notifyItemRangeChanged(startPos, videoGroups.size)
            }
            else -> {
                notifyItemChanged(startPos)
            }
        }

    }

    fun hideFooter() {
        notifyItemChanged(mVideoByGroup.size + 1)
    }
}


fun LayoutFavIslamicVideosBinding.populateLayout(videoGrp: VideoByGroup) {

    this.layoutTopContainer.video = videoGrp.contentList.get(0)
    this.layout2ndRowLeftContainer.video = videoGrp.contentList.get(1)
    this.layout2ndRowRightContainer.video = videoGrp.contentList.get(2)
    this.layout3rdRowLeftContainer.video = videoGrp.contentList.get(3)
    this.layout3rdRowRightContainer.video = videoGrp.contentList.get(4)

    this.layoutTopContainer.root.handleClickEvent {
        val intent =
            Intent(this.layoutTopContainer.root.context, VideoPlayerHomeActivity::class.java)
        intent.putExtra(VIDEO_CAT_ID, videoGrp.contentList.get(0).category)
        intent.putExtra(VIDEO_SUBCAT_ID, videoGrp.contentList.get(0).subcategory)
        intent.putExtra(VIDEO_DATA, videoGrp.contentList.get(0))
        this.layoutTopContainer.root.context!!.startActivity(intent)
    }

    this.layout2ndRowLeftContainer.root.handleClickEvent {
        val intent =
            Intent(this.layout2ndRowLeftContainer.root.context, VideoPlayerHomeActivity::class.java)
        intent.putExtra(VIDEO_CAT_ID, videoGrp.contentList.get(1).category)
        intent.putExtra(VIDEO_SUBCAT_ID, videoGrp.contentList.get(1).subcategory)
        intent.putExtra(VIDEO_DATA, videoGrp.contentList.get(1))
        this.layout2ndRowLeftContainer.root.context!!.startActivity(intent)
    }
    this.layout2ndRowRightContainer.root.handleClickEvent {
        val intent = Intent(
            this.layout2ndRowRightContainer.root.context,
            VideoPlayerHomeActivity::class.java
        )
        intent.putExtra(VIDEO_CAT_ID, videoGrp.contentList.get(2).category)
        intent.putExtra(VIDEO_SUBCAT_ID, videoGrp.contentList.get(2).subcategory)
        intent.putExtra(VIDEO_DATA, videoGrp.contentList.get(2))
        this.layout2ndRowRightContainer.root.context!!.startActivity(intent)
    }
    this.layout3rdRowLeftContainer.root.handleClickEvent {
        val intent =
            Intent(this.layout3rdRowLeftContainer.root.context, VideoPlayerHomeActivity::class.java)
        intent.putExtra(VIDEO_CAT_ID, videoGrp.contentList.get(3).category)
        intent.putExtra(VIDEO_SUBCAT_ID, videoGrp.contentList.get(3).subcategory)
        intent.putExtra(VIDEO_DATA, videoGrp.contentList.get(3))
        this.layout3rdRowLeftContainer.root.context!!.startActivity(intent)
    }

    this.layout3rdRowRightContainer.root.handleClickEvent {
        val intent = Intent(
            this.layout3rdRowRightContainer.root.context,
            VideoPlayerHomeActivity::class.java
        )
        intent.putExtra(VIDEO_CAT_ID, videoGrp.contentList.get(4).category)
        intent.putExtra(VIDEO_SUBCAT_ID, videoGrp.contentList.get(4).subcategory)
        intent.putExtra(VIDEO_DATA, videoGrp.contentList.get(4))
        this.layout3rdRowRightContainer.root.context!!.startActivity(intent)
    }
}
