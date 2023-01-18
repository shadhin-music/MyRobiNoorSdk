package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutCategoryNamazRulesCommonBinding
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.util.handleClickEvent

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/26/2021, Mon
 */

typealias SubCategoryListener = ((Data) -> Unit)?

internal class HajjCategoryAdapter : ListAdapter<Data, HajjCategoryAdapter.HajjCatViewHolder>(diffUtil) {

    private var onItemClick: SubCategoryListener = null
    fun setOnItemClickListener(listener: SubCategoryListener) {
        onItemClick = listener
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

    inner class HajjCatViewHolder(private val binding: LayoutCategoryNamazRulesCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Data, onItemClick: SubCategoryListener) {
            binding.apply {
                category = data
                itemView.handleClickEvent {
                    onItemClick.let { click ->
                        click?.invoke(data)
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HajjCatViewHolder {
        val binding: LayoutCategoryNamazRulesCommonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_category_namaz_rules_common,
            parent,
            false
        )
        return HajjCatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HajjCatViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick)
    }

}