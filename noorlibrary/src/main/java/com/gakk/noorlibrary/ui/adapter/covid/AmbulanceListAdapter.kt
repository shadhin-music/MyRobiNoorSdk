package com.gakk.noorlibrary.ui.adapter.covid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemAmbulanceBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.covid.MapOpenController
import com.gakk.noorlibrary.util.handleClickEvent

internal class AmbulanceListAdapter(
    val literatureList: MutableList<Literature>,
    val detailsCallBack: DetailsCallBack,
    val mapOpenController: MapOpenController
) :
    RecyclerView.Adapter<AmbulanceListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemAmbulanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var donationBinding: LayoutItemAmbulanceBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemAmbulanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_ambulance,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = literatureList.get(position)

        holder.donationBinding?.literature = listItem

        holder.donationBinding?.clCall?.handleClickEvent {
            detailsCallBack.showDialer(listItem.textInArabic)
        }

        holder.donationBinding?.clMap?.handleClickEvent {

            mapOpenController.openMap(
                listItem.latitude?.toDouble()!!,
                listItem.longitude?.toDouble()!!
            )

        }
    }

    override fun getItemCount(): Int {
        return literatureList.size
    }

}