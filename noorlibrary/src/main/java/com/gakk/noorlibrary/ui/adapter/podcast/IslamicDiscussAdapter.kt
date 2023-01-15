package com.gakk.noorlibrary.ui.adapter.podcast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.ItemIslamicDiscussBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.podcast.PodcastActivity
import com.gakk.noorlibrary.util.LIVE_PODCAST_TAG
import com.gakk.noorlibrary.util.handleClickEvent

class IslamicDiscussAdapter(
    val imageList: MutableList<Literature>
) :
    RecyclerView.Adapter<IslamicDiscussAdapter.ViewHolder>() {


    inner class ViewHolder(binding: ItemIslamicDiscussBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var inspirationBinding: ItemIslamicDiscussBinding? = binding

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemIslamicDiscussBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_islamic_discuss,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = imageList[position]
        holder.inspirationBinding?.item = list

        holder.inspirationBinding?.root?.handleClickEvent {
           /* if (AppPreference.cachedUserInfo.firstName.isNullOrEmpty()) {
                Toast.makeText(
                    holder.inspirationBinding?.root?.context,
                    "Please update profile!",
                    Toast.LENGTH_LONG
                ).show()

            } else {*/
                holder.inspirationBinding?.root?.context?.startActivity(
                    Intent(
                        holder.inspirationBinding?.root?.context!!,
                        PodcastActivity::class.java
                    ).putExtra(LIVE_PODCAST_TAG, list.subcategory)
                )
            //}
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}