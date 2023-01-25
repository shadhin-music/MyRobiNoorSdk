package com.gakk.noorlibrary.ui.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class BaseViewHolder<T> internal constructor(
    private val view: View,
    private val experssion: (T, Int, View) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bind(item: T, position: Int) {
        experssion(item, position, view)
    }
}