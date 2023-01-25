package com.gakk.noorlibrary.ui.adapter.donation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack

import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_DONATE_ZAKAT
import com.gakk.noorlibrary.util.handleClickEvent

internal class DonationAdapter(
    val literatureList: MutableList<Literature>,
    val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<DonationAdapter.ViewHolder>() {

    inner class ViewHolder(layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {
       // var donationBinding: LayoutItemDonationBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_donation,parent,false)

        return ViewHolder(view)
//        val binding: LayoutItemDonationBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(parent.context),
//            R.layout.layout_item_donation,
//            parent,
//            false
//        )
//        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = literatureList.get(position)
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.appCompatImageView12)
        Glide.with( holder.itemView.context).load(listItem.fullImageUrl).into(image)
        val tvTitle = holder.itemView.findViewById<AppCompatTextView>(R.id.tvTitle)
        tvTitle.text = listItem.subcategoryName
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
       // holder.donationBinding?.literature = listItem
       val cardItem = holder.itemView.findViewById<CardView>(R.id.cardItem)
            cardItem?.handleClickEvent {
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