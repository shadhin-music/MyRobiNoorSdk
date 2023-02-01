package com.gakk.noorlibrary.ui.adapter.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack

import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.ORGANIZATION_DETAILS
import com.gakk.noorlibrary.util.handleClickEvent

internal class CharityAdapter(
    val literatureList: MutableList<Literature>,
                     val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<CharityAdapter.ViewHolder>() {

    inner class ViewHolder(layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_charity_organizations,parent,false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = literatureList.get(position)
      //  holder.charityBinding?.literature = listItem
       val tvTitle = holder.itemView.findViewById<AppCompatTextView>(R.id.textViewNormal9)
        tvTitle.text = listItem.subcategoryName
        val tvText = holder.itemView.findViewById<AppCompatTextView>(R.id.textViewNormal20)
        tvText.text = listItem.textInArabic
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.appCompatImageView10)
        Glide.with(holder.view.context).load(listItem.fullImageUrl).into(image)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
             progressBar.visibility = GONE
        holder.itemView?.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                ORGANIZATION_DETAILS,
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