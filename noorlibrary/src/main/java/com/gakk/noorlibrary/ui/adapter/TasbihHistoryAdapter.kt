package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.TasbihHistoryItemBinding
import com.gakk.noorlibrary.model.tasbih.TasbihModel
import com.gakk.noorlibrary.util.TimeFormtter

class TasbihHistoryAdapter(list: List<TasbihModel>) :
    RecyclerView.Adapter<TasbihHistoryAdapter.ViewHolder>() {
    var mList: List<TasbihModel>

    init {
        mList = list
    }


    inner class ViewHolder(binding: TasbihHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: TasbihHistoryItemBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: TasbihHistoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tasbih_history_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model: TasbihModel = mList[position]

        holder.binding?.tvNumber?.setText(TimeFormtter.getNumber(position + 1)?.let {
            TimeFormtter.getNumberByLocale(
                it
            )
        })
        holder.binding?.tvTitle?.setText(model.name)
        holder.binding?.tvCount?.setText(
            TimeFormtter.getNumberByLocale(model.count.toString()) +" "+ holder.binding?.tvNumber?.context?.getString(
                R.string.txt_times
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}