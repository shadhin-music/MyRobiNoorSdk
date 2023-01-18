package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutItemNextVideoBinding
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.VideoDataCallback
import com.gakk.noorlibrary.util.handleClickEvent

internal class NextVideosAdapter(
    val videoList: MutableList<Data>,
    val calback: VideoDataCallback
) :
    RecyclerView.Adapter<NextVideosAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemNextVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var videoBinding: LayoutItemNextVideoBinding? = binding
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemNextVideoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_next_video,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoBinding?.video = videoList[position]
        holder.itemView.handleClickEvent {
            calback.setData(videoList[position])
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}