package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.home.Item
import com.gakk.noorlibrary.util.*

class IslamPillerAdapter(
    val contentBaseUrl: String,
    val pillerList: List<Item>,
    val mCallBack: MainCallback
) :
    RecyclerView.Adapter<IslamPillerAdapter.ViewHolder>() {

    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {
            var view = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.row_list_item_islam_piller,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = pillerList[position]
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        val imgThumb: AppCompatImageView = holder.itemView.findViewById(R.id.imgBgPiller)

        Glide.with(holder.itemView.context).load(contentBaseUrl+'/'+item.imageUrl).into(imgThumb)
//      val  contentbaseurl = contentBaseUrl

        holder.itemView.handleClickEvent {
            when (position) {
                0 -> {
                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY
                    )
                }
                1 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_NAMAZ_RULES
                    )
                }
                2 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_JAKAT
                    )
                }
                3 -> {
                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_ROZA
                    )
                }
                4 -> {

                    mCallBack.openDetailsActivityWithPageName(
                        PAGE_HAJJ_HOME
                    )
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return pillerList.size
    }
}