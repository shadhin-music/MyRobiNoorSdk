package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.home.Item
import com.gakk.noorlibrary.ui.fragments.tabs.HomeCellItemControl
import com.gakk.noorlibrary.util.handleClickEvent

internal class HomePrayerAdapter(
    val contentBaseUrl: String,
    val duaList: List<Item>,
    callback: MainCallback,
    val homeCellItemControl: HomeCellItemControl
) :
    RecyclerView.Adapter<HomePrayerAdapter.ViewHolder>() {

    private val mCallBack: MainCallback

    init {
        mCallBack = callback
    }

    inner class ViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
         var view: View = layoutView

        init {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.row_list_item_dua,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val duaItem = duaList[position]
       val imgBg = holder.view.findViewById<AppCompatImageView>(R.id.imgBg)
        val image = contentBaseUrl+"/"+duaItem.imageUrl
        Glide.with(holder.view.context).load(image).into(imgBg)
        val tvTitleDua = holder.view.findViewById<AppCompatTextView>(R.id.tvTitleDua)
        tvTitleDua.text= duaItem.contentName
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        val rlShare = holder.view.findViewById<RelativeLayout>(R.id.rlShare)
       // holder.prayerBinding?.dua = duaItem


        rlShare?.handleClickEvent {
            homeCellItemControl.shareImage(contentBaseUrl+"/"+duaItem.imageUrl)
        }
    }
    override fun getItemCount(): Int {
        return duaList.size
    }
}