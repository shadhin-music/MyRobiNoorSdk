package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R

import com.gakk.noorlibrary.model.quranSchool.Scholar
import com.gakk.noorlibrary.util.handleClickEvent

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/5/2021, Mon
 */
typealias ClickListener = ((Scholar) -> Unit)?

//internal class ScholarListAdapter() : ListAdapter<Scholar, ScholarListAdapter.ScholarViewHolder>(diffUtil) {
//
////    private var onItemClick: ClickListener = null
////    fun setOnItemClickListener(listener: ClickListener) {
////        onItemClick = listener
////    }
////
////    class ScholarViewHolder(private val binding: LayoutScholarsSingleItemBinding) :
////        RecyclerView.ViewHolder(binding.root) {
////
////        fun bind(scholar: Scholar, onItemClick: ClickListener) {
////            binding.apply {
////                data = scholar
////                itemView.handleClickEvent {
////                    onItemClick.let { click ->
////                        click?.invoke(scholar)
////                    }
////                }
////
////            }
////        }
////    }
////
////
////    companion object {
////        val diffUtil = object : DiffUtil.ItemCallback<Scholar>() {
////            override fun areItemsTheSame(
////                oldItem: Scholar,
////                newItem: Scholar
////            ): Boolean = oldItem.id == newItem.id
////
////            override fun areContentsTheSame(
////                oldItem: Scholar,
////                newItem: Scholar
////            ): Boolean = oldItem.hashCode() == newItem.hashCode()
////
////        }
////    }
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScholarViewHolder {
////        val binding: LayoutScholarsSingleItemBinding = DataBindingUtil.inflate(
////            LayoutInflater.from(parent.context),
////            R.layout.layout_scholars_single_item,
////            parent,
////            false
////        )
////        return ScholarViewHolder(binding)
////    }
////
////    override fun onBindViewHolder(holder: ScholarViewHolder, position: Int) {
////        val item = getItem(position)
////        holder.bind(item, onItemClick)
////    }
//
//
//}