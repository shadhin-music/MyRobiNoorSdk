package com.gakk.noorlibrary.ui.adapter.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

internal class BaseViewHolder<T> internal constructor(
    private val binding: ViewBinding,
    private val experssion: (T, Int, ViewBinding) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: T, position: Int) {
        experssion(item, position, binding)
    }
}