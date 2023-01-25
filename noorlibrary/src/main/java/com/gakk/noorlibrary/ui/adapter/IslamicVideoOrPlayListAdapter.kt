package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    inner class VideoOrPlayListViewHolder(layoutView:View) : RecyclerView.ViewHolder(layoutView) {

        var view: View = layoutView

//        constructor(binding: LayoutVideoBinding) : super(binding.root) {
//            videoBinding = binding
//        }
//
//        var playListBinding: LayoutVideoPlaylistBinding? = null
//
//        constructor(binding: LayoutVideoPlaylistBinding) : super(binding.root) {
//            playListBinding = binding
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoOrPlayListViewHolder {

        when (viewType) {
            VIDEO -> {
                var binding: View =
                    LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_video,
                    parent,
                    false
                )
                return VideoOrPlayListViewHolder(binding)
            }
            else -> {
                var binding2: View =
                    LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_video_playlist,
                    parent,
                    false
                )
                return VideoOrPlayListViewHolder(binding2)
            }
        }

    }

    override fun onBindViewHolder(holder: VideoOrPlayListViewHolder, position: Int) {
        var videoData: com.gakk.noorlibrary.model.video.category.Data? = null
        var playListData: com.gakk.noorlibrary.model.subcategory.Data? = null

            mVideoList?.let {
               // video = it.get(position)
                videoData = it.get(position)
            }
       val imgThumb:AppCompatImageView = holder.itemView.findViewById(R.id.imgThumb)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
        Glide.with(holder.itemView.context).load(videoData?.fullImageUrl?.replace("<size>", "1280")).into(imgThumb)
        val textView: AppCompatTextView =  holder.itemView.findViewById(R.id.tvTitle)
        textView.text = videoData?.contenTtitle
        val textView2: AppCompatTextView =  holder.itemView.findViewById(R.id.tvOtherInfo)
        textView2.text = videoData?.miniSummary
        val constraint :ConstraintLayout = holder.view.findViewById(R.id.constraint)
        constraint.handleClickEvent {

                val intent =
                    Intent(holder.itemView.context, VideoPlayerHomeActivity::class.java)
                intent.putExtra(VIDEO_CAT_ID, mCatId)
                intent.putExtra(VIDEO_SUBCAT_ID, mSubCatId)
                intent.putExtra(VIDEO_DATA, videoData)
                holder.itemView.context!!.startActivity(intent)
            }

//        holder.playListBinding?.let { root ->
            mPlayList?.let {
               // root.playList = it.get(position)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
                playListData = it.get(position)
                val imgThumb:AppCompatImageView = holder.itemView.findViewById(R.id.imgThumb)

                Glide.with(holder.itemView.context).load(playListData?.fullImageUrl).into(imgThumb)
            }
       // }
    }

    override fun getItemCount(): Int {
        when (mViewType) {
            VIDEO -> return mVideoList?.size!!
            else -> return mPlayList?.size!!
        }
    }

    override fun getItemViewType(position: Int) = mViewType

}