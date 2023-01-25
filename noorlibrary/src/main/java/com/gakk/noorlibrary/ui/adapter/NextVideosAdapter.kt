package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.VideoDataCallback
import com.gakk.noorlibrary.util.handleClickEvent

internal class NextVideosAdapter(
    val videoList: MutableList<Data>,
    val calback: VideoDataCallback
) :
    RecyclerView.Adapter<NextVideosAdapter.ViewHolder>() {

    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {
        var videoBinding = binding
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.layout_item_next_video,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val video = videoList[position]
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.imgThumb)
        Glide.with(holder.itemView.context).load(video.fullImageUrl).into(image)
        val progressBar =holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
        val tvTitle = holder.itemView.findViewById<AppCompatTextView>(R.id.tvTitle)
        tvTitle.text = video.contenTtitle
        val tvText = holder.itemView.findViewById<AppCompatTextView>(R.id.tvOtherInfo)
        tvText.text = video.miniSummary
        holder.itemView.handleClickEvent {
            calback.setData(videoList[position])
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}