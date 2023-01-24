package com.gakk.noorlibrary.ui.adapter.khatamquran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    inner class ViewHolder(layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {
        var view= layoutView
       // var itemLiveVideosBinding: LayoutItemKhatamQuranBinding? = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_khatam_quran,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = videoList.get(position)
       // holder.itemLiveVideosBinding?.video = listItem
      val  tvVideoTitle:AppCompatTextView = holder.view.findViewById(R.id.tvVideoTitle)
            tvVideoTitle.text = listItem.title
        val  tvSubTitle:AppCompatTextView = holder.view.findViewById(R.id.tvSubTitle)
        tvSubTitle.text = listItem.text
        val  tvDuration:AppCompatTextView = holder.view.findViewById(R.id.tvDuration)
        tvDuration.text = listItem.textInArabic
        val ivVideo = holder.itemView.findViewById<AppCompatImageView>(R.id.ivVideo)

        holder.itemView.handleClickEvent {
            listItem.let { callBack.setVideoData(it, position) }
        }

        when (listItem.isPlaying) {
            true -> {
           ivVideo?.setImageResource(R.drawable.ic_pause_green)
            }
            else -> {
              ivVideo?.setImageResource(R.drawable.ic_play_2)
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
        val clParentQuran:ConstraintLayout = holder.view.findViewById(R.id.clParentQuran)
      clParentQuran?.setBackgroundResource(R.drawable.rounded_green_one)
        val  tvVideoTitle:AppCompatTextView = holder.view.findViewById(R.id.tvVideoTitle)
        val ivVideo = holder.itemView.findViewById<AppCompatImageView>(R.id.ivVideo)
        tvVideoTitle?.setTextColor(
            ContextCompat.getColor(
              clParentQuran?.context!!,
                R.color.txt_color_black
            )
        )
        /* holder.binding.tvDuration.setTextColor(
             ContextCompat.getColor(
                 holder.binding.layoutVideo.context,
                 R.color.colorTextSecondary
             )
         )*/

       ivVideo?.setColorFilter(
            ContextCompat.getColor(ivVideo?.context!!,R.color.colorPrimary))
    }

    fun makeViewUnSelected(holder: ViewHolder) {
        val clParentQuran:ConstraintLayout = holder.view.findViewById(R.id.clParentQuran)
        val  tvVideoTitle:AppCompatTextView = holder.view.findViewById(R.id.tvVideoTitle)
       clParentQuran?.setBackgroundResource(R.drawable.bg_border_rounded_gray)
        val ivVideo = holder.itemView.findViewById<AppCompatImageView>(R.id.ivVideo)
      tvVideoTitle?.setTextColor(
            ContextCompat.getColor(
       clParentQuran?.context!!,
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

        ivVideo?.setColorFilter(
            ContextCompat.getColor(ivVideo?.context!!,R.color.ash))
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