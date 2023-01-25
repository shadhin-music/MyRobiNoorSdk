package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.databinding.RowListItemRomjanAmolBinding
import com.gakk.noorlibrary.model.home.Item
import com.gakk.noorlibrary.util.handleClickEvent

internal class RomjanAmolAdapter(
    private val contentBaseUrl: String,
    private val callback: MainCallback
) :
    ListAdapter<Item, RomjanAmolAdapter.ViewHolder>(diffUtil) {

    private val mCallBack: MainCallback = callback

    inner class ViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {

        fun bind(item: Item) {
            binding.apply {
                val imgBg = binding.findViewById<AppCompatImageView>(R.id.imgBg)
                val image = contentBaseUrl+"/"+item.imageUrl
                Glide.with(binding.context).load(image).into(imgBg)


                val progressBar = binding.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE

                val rlShare = binding.findViewById<RelativeLayout>(R.id.rlShare)
//                contentbaseurl = contentBaseUrl
//                romjanamol = item

                rlShare.handleClickEvent {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.row_list_item_romjan_amol,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.contentId.equals(newItem.contentId)
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}