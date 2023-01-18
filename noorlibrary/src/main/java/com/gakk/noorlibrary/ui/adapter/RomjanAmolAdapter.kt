package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

    inner class ViewHolder(private val binding: RowListItemRomjanAmolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.apply {
                contentbaseurl = contentBaseUrl
                romjanamol = item

                rlShare.handleClickEvent {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowListItemRomjanAmolBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
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