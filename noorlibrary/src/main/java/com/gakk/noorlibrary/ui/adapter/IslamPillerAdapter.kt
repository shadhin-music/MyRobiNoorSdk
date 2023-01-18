package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.databinding.RowListItemIslamPillerBinding
import com.gakk.noorlibrary.model.home.Item
import com.gakk.noorlibrary.util.*

class IslamPillerAdapter(
    val contentBaseUrl: String,
    val pillerList: List<Item>,
    val mCallBack: MainCallback
) :
    RecyclerView.Adapter<IslamPillerAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowListItemIslamPillerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var islamPillerBinding: RowListItemIslamPillerBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowListItemIslamPillerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_list_item_islam_piller,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.islamPillerBinding?.item = pillerList[position]
        holder.islamPillerBinding?.contentbaseurl = contentBaseUrl

        holder.itemView.handleClickEvent {
            when (position) {
                0 -> {
                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY
                    )
                }
                1 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_NAMAZ_RULES
                    )
                }
                2 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_JAKAT
                    )
                }
                3 -> {
                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_ROZA
                    )
                }
                4 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_HAJJ_HOME
                    )
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return pillerList.size
    }
}