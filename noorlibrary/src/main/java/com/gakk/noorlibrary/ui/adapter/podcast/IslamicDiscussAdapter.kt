package com.gakk.noorlibrary.ui.adapter.podcast

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.podcast.PodcastActivity
import com.gakk.noorlibrary.util.LIVE_PODCAST_TAG
import com.gakk.noorlibrary.util.PLACE_HOLDER_16_9
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl

internal class IslamicDiscussAdapter(
    val imageList: MutableList<Literature>
) :
    RecyclerView.Adapter<IslamicDiscussAdapter.ViewHolder>() {


    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {

        var view: View = binding
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_islamic_discuss,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = imageList[position]
        val item = list

        val ivTalk = holder.view.findViewById<AppCompatImageView>(R.id.ivTalk)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)

        setImageFromUrl(ivTalk,item.fullImageUrl,progressBar,PLACE_HOLDER_16_9)

        holder.view.handleClickEvent {

                holder.view.context?.startActivity(
                    Intent(
                        holder.view.context!!,
                        PodcastActivity::class.java
                    ).putExtra(LIVE_PODCAST_TAG, list.subcategory)
                )

        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}