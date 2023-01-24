package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.TasbihHistoryItemBinding
import com.gakk.noorlibrary.model.tasbih.TasbihModel
import com.gakk.noorlibrary.util.TimeFormtter

internal class TasbihHistoryAdapter(list: List<TasbihModel>) :
    RecyclerView.Adapter<TasbihHistoryAdapter.ViewHolder>() {
    var mList: List<TasbihModel>

    init {
        mList = list
    }


    inner class ViewHolder(layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView
       // var binding: TasbihHistoryItemBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tasbih_history_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model: TasbihModel = mList[position]
        val tvNumber :AppCompatTextView = holder.view.findViewById(R.id.tvNumber)
        val tvTitle :AppCompatTextView = holder.view.findViewById(R.id.tvTitle)
        val tvCount :AppCompatTextView = holder.view.findViewById(R.id.tvCount)
        tvNumber?.setText(TimeFormtter.getNumber(position + 1)?.let {
            TimeFormtter.getNumberByLocale(
                it
            )
        })
       tvTitle?.setText(model.name)
       tvCount?.setText(
            TimeFormtter.getNumberByLocale(model.count.toString()) +" "+ tvNumber?.context?.getString(
                R.string.txt_times
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}