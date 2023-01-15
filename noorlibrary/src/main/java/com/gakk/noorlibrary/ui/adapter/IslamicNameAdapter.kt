package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.RowIslamicNameBinding
import com.gakk.noorlibrary.model.islamicName.IslamicName
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/18/2021, Sun
 */
typealias IslamicNameListener = ((IslamicName) -> Unit)?

class IslamicNameAdapter : ListAdapter<IslamicName, IslamicNameAdapter.ViewHolder>(diffUtil) {

    private var onItemClick: IslamicNameListener = null
    fun setOnItemClickListener(listener: IslamicNameListener) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: RowIslamicNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(islamicName: IslamicName, pos: Int) {
            binding.apply {
                var st = pos.toString()
                if (pos < 10) {
                    st = "0$st"
                }
                item = islamicName
                serial = TimeFormtter.getNumberByLocale(st)

                itemView.handleClickEvent {
                    onItemClick.let { click ->
                        click?.invoke(islamicName)
                    }
                }

            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<IslamicName>() {
            override fun areItemsTheSame(oldItem: IslamicName, newItem: IslamicName): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: IslamicName, newItem: IslamicName): Boolean =
                oldItem.hashCode() == newItem.hashCode()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowIslamicNameBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_islamic_name,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position + 1)
    }
}