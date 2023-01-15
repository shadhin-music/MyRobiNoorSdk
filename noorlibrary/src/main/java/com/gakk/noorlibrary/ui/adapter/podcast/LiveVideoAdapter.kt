package com.gakk.noorlibrary.ui.adapter.podcast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemLiveVideosBinding
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.activity.podcast.ItemClickListener
import com.gakk.noorlibrary.util.*

class LiveVideoAdapter(
    val videoList: List<LiveVideosResponse.Data?>?,
    val callBack: ItemClickListener
) :
    RecyclerView.Adapter<LiveVideoAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemLiveVideosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var itemLiveVideosBinding: ItemLiveVideosBinding? = binding

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLiveVideosBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_live_videos,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = videoList?.get(position)
        holder.itemLiveVideosBinding?.item = listItem

        holder.itemView.handleClickEvent {
            if (Util.checkSub()) {
                listItem?.let { callBack.setData(it) }
            } else {
                    holder.itemView.context.startActivity(
                        Intent(holder.itemView.context, DetailsActivity::class.java)
                            .putExtra(PAGE_NAME, PAGE_SUBSCRIPTION_WITH_G_PAY)
                    )
            }
        }
    }

    override fun getItemCount(): Int {
        return videoList?.size ?: 0
    }
}