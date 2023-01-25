package com.gakk.noorlibrary.ui.adapter.podcast

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemLiveVideosBinding
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.activity.podcast.ItemClickListener
import com.gakk.noorlibrary.util.*

internal class LiveVideoAdapter(
    val videoList: List<LiveVideosResponse.Data?>?,
    val callBack: ItemClickListener
) :
    RecyclerView.Adapter<LiveVideoAdapter.ViewHolder>() {

    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {
        var view: View = binding
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_live_videos,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = videoList?.get(position)

       val item = listItem

        val scholars_img = holder.view.findViewById<ImageView>(R.id.scholars_img)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
        val title = holder.view.findViewById<AppCompatTextView>(R.id.title)
        val description = holder.view.findViewById<AppCompatTextView>(R.id.description)

        title.text = item?.title
        description.text = item?.text

        setImageFromUrl(scholars_img,item?.fullImageUrl,progressBar,PLACE_HOLDER_16_9)

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