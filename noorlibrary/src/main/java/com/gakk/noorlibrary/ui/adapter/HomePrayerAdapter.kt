package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.databinding.RowListItemDuaBinding
import com.gakk.noorlibrary.model.home.Item
import com.gakk.noorlibrary.util.handleClickEvent

class HomePrayerAdapter(
    val contentBaseUrl: String,
    val duaList: List<Item>,
    callback: MainCallback
) :
    RecyclerView.Adapter<HomePrayerAdapter.ViewHolder>() {

    private val mCallBack: MainCallback

    init {
        mCallBack = callback
    }

    inner class ViewHolder(binding: RowListItemDuaBinding) : RecyclerView.ViewHolder(binding.root) {
        var prayerBinding: RowListItemDuaBinding? = binding

        init {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowListItemDuaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_list_item_dua,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.prayerBinding?.dua = duaList[position]
        holder.prayerBinding?.contentbaseurl = contentBaseUrl

        holder.prayerBinding?.rlShare?.handleClickEvent {
        }
    }
    override fun getItemCount(): Int {
        return duaList.size
    }
}