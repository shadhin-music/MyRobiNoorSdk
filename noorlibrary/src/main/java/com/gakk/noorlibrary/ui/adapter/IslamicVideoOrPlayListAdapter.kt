package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutVideoBinding
import com.gakk.noorlibrary.databinding.LayoutVideoPlaylistBinding
import com.gakk.noorlibrary.ui.activity.VideoPlayerHomeActivity
import com.gakk.noorlibrary.util.VIDEO_CAT_ID
import com.gakk.noorlibrary.util.VIDEO_DATA
import com.gakk.noorlibrary.util.VIDEO_SUBCAT_ID
import com.gakk.noorlibrary.util.handleClickEvent


val VIDEO = 0
val PLAY_LIST = 1

class IslamicVideoOrPlayListAdapter(
    videoList: MutableList<com.gakk.noorlibrary.model.video.category.Data>? = null,
    playList: MutableList<com.gakk.noorlibrary.model.subcategory.Data>? = null,
    detailsCallBack: DetailsCallBack,
    videoCatId: String? = null,
    videoSubCatId: String? = null
) : RecyclerView.Adapter<IslamicVideoOrPlayListAdapter.VideoOrPlayListViewHolder>() {

    val mVideoList = videoList
    val mPlayList = playList
    var mViewType: Int
    val mDetailsCallBack = detailsCallBack
    val mCatId = videoCatId
    val mSubCatId = videoSubCatId

    init {
        if (mVideoList != null) {
            mViewType = VIDEO
        } else {
            mViewType = PLAY_LIST
        }
    }

    inner class VideoOrPlayListViewHolder : RecyclerView.ViewHolder {

        var videoBinding: LayoutVideoBinding? = null

        constructor(binding: LayoutVideoBinding) : super(binding.root) {
            videoBinding = binding
        }

        var playListBinding: LayoutVideoPlaylistBinding? = null

        constructor(binding: LayoutVideoPlaylistBinding) : super(binding.root) {
            playListBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoOrPlayListViewHolder {

        when (viewType) {
            VIDEO -> {
                var binding: LayoutVideoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_video,
                    parent,
                    false
                )
                return VideoOrPlayListViewHolder(binding)
            }
            else -> {
                var binding: LayoutVideoPlaylistBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_video_playlist,
                    parent,
                    false
                )
                return VideoOrPlayListViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: VideoOrPlayListViewHolder, position: Int) {
        var videoData: com.gakk.noorlibrary.model.video.category.Data? = null
        var playListData: com.gakk.noorlibrary.model.subcategory.Data? = null
        holder.videoBinding?.let { root ->
            mVideoList?.let {
                root.video = it.get(position)
                videoData = it.get(position)
            }

            holder.videoBinding?.root?.handleClickEvent {

                val intent =
                    Intent(holder.videoBinding?.root?.context, VideoPlayerHomeActivity::class.java)
                intent.putExtra(VIDEO_CAT_ID, mCatId)
                intent.putExtra(VIDEO_SUBCAT_ID, mSubCatId)
                intent.putExtra(VIDEO_DATA, videoData)
                holder.videoBinding?.root?.context!!.startActivity(intent)
            }
        }
        holder.playListBinding?.let { root ->
            mPlayList?.let {
                root.playList = it.get(position)
                playListData = it.get(position)
            }
        }
    }

    override fun getItemCount(): Int {
        when (mViewType) {
            VIDEO -> return mVideoList?.size!!
            else -> return mPlayList?.size!!
        }
    }

    override fun getItemViewType(position: Int) = mViewType

}