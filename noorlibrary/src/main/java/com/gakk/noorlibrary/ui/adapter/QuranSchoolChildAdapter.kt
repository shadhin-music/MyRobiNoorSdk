package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutQuranSchoolOldChildItemBinding
import com.gakk.noorlibrary.model.quranSchool.QuranSchoolModel
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.hide
import com.gakk.noorlibrary.util.show

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/1/2021, Thu
 */
class QuranSchoolChildAdapter(
    private val listener: OnItemClickListener
) :
    ListAdapter<QuranSchoolModel, RecyclerView.ViewHolder>(diffUtil) {

    private val VIEW_TYPE_LIVE_CONTENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val binding: LayoutQuranSchoolOldChildItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_quran_school_old_child_item,
            parent,
            false
        )

        viewHolder = QuranSchoolChildOldViewHolder(binding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: QuranSchoolChildOldViewHolder =
            holder as QuranSchoolChildOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class QuranSchoolChildOldViewHolder(private val binding: LayoutQuranSchoolOldChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.handleClickEvent {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onItemClick(position, currentList)
                }
            }
        }

        fun bind(quranSchoolModel: QuranSchoolModel) {
            if (quranSchoolModel.isLive == true) {
                binding.iconLive.show()
                binding.dateTv.hide()
                binding.description.hide()
            } else {
                binding.iconLive.hide()
                binding.dateTv.show()
                binding.description.show()
            }
            binding.item = quranSchoolModel
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuranSchoolModel>() {
            override fun areItemsTheSame(
                oldItem: QuranSchoolModel,
                newItem: QuranSchoolModel
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: QuranSchoolModel,
                newItem: QuranSchoolModel
            ): Boolean = oldItem.hashCode() == newItem.hashCode()

        }
    }


    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_LIVE_CONTENT
    }

    interface OnItemClickListener {
        fun onItemClick(postion: Int, currentList: List<QuranSchoolModel>)
    }
}