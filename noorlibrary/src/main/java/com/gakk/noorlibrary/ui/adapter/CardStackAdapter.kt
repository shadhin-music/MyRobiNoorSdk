package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemAllahNamesBinding

import com.gakk.noorlibrary.model.names.Data

internal class CardStackAdapter(
    private var items: List<Data> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAllahNamesBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_allah_names,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = items[position]
        holder.spotBinding?.allahName = spot
        holder.spotBinding?.itemImage?.setImageResource(R.drawable.text)

        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, spot.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<Data>) {
        this.items = items
    }

    fun getItems(): List<Data> {
        return items
    }

    inner class ViewHolder(binding: ItemAllahNamesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var spotBinding: ItemAllahNamesBinding? = binding
    }

}
