package com.gakk.noorlibrary.ui.adapter.donation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemDonationBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_DONATE_ZAKAT
import com.gakk.noorlibrary.util.handleClickEvent

class DonationAdapter(
    val literatureList: MutableList<Literature>,
    val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<DonationAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemDonationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var donationBinding: LayoutItemDonationBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemDonationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_donation,
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
                PAGE_DONATE_ZAKAT,
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