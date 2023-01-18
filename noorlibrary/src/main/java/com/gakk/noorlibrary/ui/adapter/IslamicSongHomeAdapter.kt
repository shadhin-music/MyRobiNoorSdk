package com.gakk.noorlibrary.ui.adapter

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


internal class IslamicSongHomeAdapter(
    videoGroups: MutableList<VideoByGroup>,
    pagingViewCallBack: PagingViewCallBack,
    detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<IslamicSongHomeAdapter.IslamicSongHomeViewHolder>() {

    val HORIZONTAL_LIST = 0
    val FAVOURITE_VIDEOS = 1
    val FOOTER = 2

    val mPagingViewCallBack = pagingViewCallBack
    var mVideoByGroup = videoGroups
    var mViewControl: IslamicVideoHomeAdapterViewControl = IslamicVideoHomeAdapterViewControl()
    val mDetailsCallBack = detailsCallBack



    inner class IslamicSongHomeViewHolder : RecyclerView.ViewHolder {
        var listBinding: LayoutHorizontalListBinding? = null

        constructor(binding: LayoutHorizontalListBinding) : super(binding.root) {
            listBinding = binding
        }



        var footerBinding: LayoutFooterBinding? = null

        constructor(binding: LayoutFooterBinding) : super(binding.root) {
            footerBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IslamicSongHomeViewHolder {
        when (viewType) {
            HORIZONTAL_LIST -> {
                var binding: LayoutHorizontalListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_horizontal_list,
                    parent,
                    false
                )
                return IslamicSongHomeViewHolder(binding)
            }

            else -> {
                var binding: LayoutFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_footer,
                    parent,
                    false
                )
                return IslamicSongHomeViewHolder(binding)
            }

        }
    }

    override fun onBindViewHolder(holder: IslamicSongHomeViewHolder, position: Int) {
      /*  holder.listBinding?.let {
            it.rvHorizontalList.adapter =
                IslamicVideoOrPlayListAdapter(videoList = mVideoByGroup.get(position - 1).contentList,
                    detailsCallBack = mDetailsCallBack)
            it.layoutTitleView.tvTitle.setText(
                mVideoByGroup.get(position - 1).contentList.get(
                    0
                ).subcategoryName
            )



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
        }*/

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
                position == 0 -> {
                    Log.i("ADPPOS", "$position->FAVOURITE_VIDEOS")
                    return FAVOURITE_VIDEOS
                }
                videoGroup.size + 1 == position -> {
                    Log.i("ADPPOS", "$position->FOOTER")
                    return FOOTER
                }
                else -> {
                    Log.i("ADPPOS", "$position->VIDEO")
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



