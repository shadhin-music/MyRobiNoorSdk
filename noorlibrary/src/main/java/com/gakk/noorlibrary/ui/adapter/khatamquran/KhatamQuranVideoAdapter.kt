package com.gakk.noorlibrary.ui.adapter.khatamquran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutItemKhatamQuranBinding
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.ui.activity.khatamquran.ItemClickControl
import com.gakk.noorlibrary.util.VideoDiffCallback
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.videoNewList

internal class KhatamQuranVideoAdapter(
    var videoList: MutableList<KhatamQuranVideosResponse.Data>,
    val callBack: ItemClickControl
) :
    RecyclerView.Adapter<KhatamQuranVideoAdapter.ViewHolder>() {

    var currentMediaId: String? = null

    inner class ViewHolder(binding: LayoutItemKhatamQuranBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var itemLiveVideosBinding: LayoutItemKhatamQuranBinding? = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemKhatamQuranBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_khatam_quran,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = videoList.get(position)
        holder.itemLiveVideosBinding?.video = listItem

        holder.itemView.handleClickEvent {
            listItem.let { callBack.setVideoData(it, position) }
        }

        when (listItem.isPlaying) {
            true -> {
                holder.itemLiveVideosBinding?.ivVideo?.setImageResource(R.drawable.ic_pause_green)
            }
            else -> {
                holder.itemLiveVideosBinding?.ivVideo?.setImageResource(R.drawable.ic_play_2)
            }
        }
        if (listItem.isSelected == true) {
            makeViewSelected(holder)
        } else {
            makeViewUnSelected(holder)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun makeViewSelected(holder: ViewHolder) {
        holder.itemLiveVideosBinding?.clParentQuran?.setBackgroundResource(R.drawable.rounded_green_one)
        holder.itemLiveVideosBinding?.tvVideoTitle?.setTextColor(
            ContextCompat.getColor(
                holder.itemLiveVideosBinding?.clParentQuran?.context!!,
                R.color.txt_color_black
            )
        )
        /* holder.binding.tvDuration.setTextColor(
             ContextCompat.getColor(
                 holder.binding.layoutVideo.context,
                 R.color.colorTextSecondary
             )
         )*/

        holder.itemLiveVideosBinding?.ivVideo?.setColorFilter(
            ContextCompat.getColor(holder.itemLiveVideosBinding?.ivVideo?.context!!,R.color.colorPrimary))
    }

    fun makeViewUnSelected(holder: ViewHolder) {
        holder.itemLiveVideosBinding?.clParentQuran?.setBackgroundResource(R.drawable.bg_border_rounded_gray)
        holder.itemLiveVideosBinding?.tvVideoTitle?.setTextColor(
            ContextCompat.getColor(
                holder.itemLiveVideosBinding?.clParentQuran?.context!!,
                R.color.ash
            )
        )

        /* holder.binding.tvVideoTitle.setTextColor(
             ContextCompat.getColor(
                 holder.binding.layoutVideo.context,
                 R.color.txt_color_black
             )
         )

         holder.binding.tvDuration.setTextColor(
             ContextCompat.getColor(
                 holder.binding.layoutVideo.context,
                 R.color.ash
             )
         )*/

        holder.itemLiveVideosBinding?.ivVideo?.setColorFilter(
            ContextCompat.getColor(holder.itemLiveVideosBinding?.ivVideo?.context!!,R.color.ash))
    }

    fun setPlayingSong(mediaId: String?, isPlaying: Boolean) {
        currentMediaId = mediaId

        val newList = videoList.videoNewList(mediaId, isPlaying)

        val callback = VideoDiffCallback(videoList, newList)
        val result = DiffUtil.calculateDiff(callback)
        videoList.clear()
        videoList.addAll(newList)
        result.dispatchUpdatesTo(this)
    }
}