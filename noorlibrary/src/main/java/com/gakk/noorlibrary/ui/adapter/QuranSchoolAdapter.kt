package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutQuranSchoolBinding
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.util.handleClickEvent

internal class QuranSchoolAdapter(
    private val listener: OnItemClickListener
) :
    ListAdapter<Data, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val binding: LayoutQuranSchoolBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_quran_school,
            parent,
            false
        )

        viewHolder = QuranSchoolOldViewHolder(binding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: QuranSchoolOldViewHolder =
            holder as QuranSchoolOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class QuranSchoolOldViewHolder(private val binding: LayoutQuranSchoolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.handleClickEvent {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)
                    listener.onItemClickVideo(position, currentList)
                }
            }
        }

        fun bind(listLiterature: Data) {
            binding.item = listLiterature
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