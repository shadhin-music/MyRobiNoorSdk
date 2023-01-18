package com.gakk.noorlibrary.ui.adapter.covid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemCovidFuneralBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.*

internal class CovidServiceHomeAdapter(
    val literatureList: MutableList<Literature>,
    val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<CovidServiceHomeAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemCovidFuneralBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var donationBinding: LayoutItemCovidFuneralBinding? = binding

        init {
            donationBinding?.root?.let {
                it?.resizeView(
                    ViewDimension.HalfScreenWidthMargin,
                    detailsCallBack.getScreenWith(),
                    it.context
                )
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemCovidFuneralBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_covid_funeral,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = literatureList.get(position)

        holder.donationBinding?.literature = listItem

        holder.donationBinding?.cardItem?.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_FUNERAL_DETAILS,
                detailsActivityCallBack = detailsCallBack,
                literature = listItem
            )
            detailsCallBack.addFragmentToStackAndShow(fragment!!)
        }
    }

    override fun getItemCount(): Int {
        return literatureList.size
    }
}