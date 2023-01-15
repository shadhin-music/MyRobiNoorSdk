package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemInstructiveVideoBinding
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.util.ViewDimension
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.resizeView

class InstructiveVideoAdapter(
    private val listener: OnItemClickListener,
    detailsCallBack: DetailsCallBack,
) :
    ListAdapter<Data, RecyclerView.ViewHolder>(diffUtil) {
    val mDetailsCallBack = detailsCallBack

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val binding: LayoutItemInstructiveVideoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_instructive_video,
            parent,
            false
        )

        viewHolder = InstructiveVideoOldViewHolder(binding)


        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: InstructiveVideoOldViewHolder =
            holder as InstructiveVideoOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class InstructiveVideoOldViewHolder(private val binding: LayoutItemInstructiveVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.resizeView(
                ViewDimension.HalfScreenWidthMargin,
                mDetailsCallBack.getScreenWith(),
                binding.root.context
            )
            binding.root
            binding.root.handleClickEvent {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)
                    listener.onItemClickVideo(position, currentList)
                }
            }
        }

        fun bind(listLiterature: Data) {
            binding.video = listLiterature
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean = oldItem.hashCode() == newItem.hashCode()

        }
    }

    interface OnItemClickListener {
        fun onItemClickVideo(postion: Int, currentList: List<Data>)
    }
}