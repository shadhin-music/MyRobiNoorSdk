package com.gakk.noorlibrary.ui.adapter.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

internal class BaseAdapter<T>: RecyclerView.Adapter<BaseViewHolder<T>>() {
    var listOfItems:MutableList<T> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var expressionViewHolderBinding: ((T, Int, View) -> Unit)? = null
    var expressionOnCreateViewHolder:((ViewGroup)->View)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let { it(parent) }?.let { BaseViewHolder(it, expressionViewHolderBinding!!) }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(listOfItems[position], position)
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }
}