package com.gakk.noorlibrary.ui.adapter.hut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.onlinehut.OnlineHutLocationwiseFrgment
import com.gakk.noorlibrary.util.CITY_NAME_NORTH
import com.gakk.noorlibrary.util.CITY_NAME_SOUTH
import com.gakk.noorlibrary.util.handleClickEvent

internal class OnlineHutHomeAdapter(
    val literatureList: MutableList<Literature>,
    val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<OnlineHutHomeAdapter.ViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_LOCATION = 1
    val ITEM_TITLE = 2
    val ITEM_HUT = 3

    inner class ViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            ITEM_HEADER -> {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_online_hut, parent, false)
                return ViewHolder(view)

            }

            ITEM_LOCATION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_locationwise_hut, parent, false)
                return ViewHolder(view)
            }


            ITEM_TITLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_title_hut, parent, false)
                return ViewHolder(view)
            }


            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_online_hut, parent, false)
                return ViewHolder(view)
            }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {

            ITEM_HEADER -> {
                val image = holder.itemView.findViewById<AppCompatImageView>(R.id.ivHeaderHut)
                Glide.with(holder.view.context).load(ImageFromOnline("ic_header_online_hut.png").fullImageUrl).into(image)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
            }
            ITEM_LOCATION -> {
                val ivNorthCity = holder.itemView.findViewById<AppCompatImageView>(R.id.ivNorthCity)
                Glide.with(holder.view.context).load(ImageFromOnline("ic_north_city_corporation.png").fullImageUrl).into(ivNorthCity)
                ivNorthCity.scaleType = ImageView.ScaleType.CENTER_CROP
                val ivSouthCity = holder.itemView.findViewById<AppCompatImageView>(R.id.ivSouthCity)
                Glide.with(holder.view.context).load( ImageFromOnline("ic_south_city_corporation.png").fullImageUrl).into(ivSouthCity)
                ivSouthCity.scaleType = ImageView.ScaleType.CENTER_CROP
                val progressBarNorth = holder.view.findViewById<ProgressBar>(R.id.progressBarNorth)
                progressBarNorth.visibility = View.GONE
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE

               ivNorthCity?.handleClickEvent {
                    detailsCallBack.addFragmentToStackAndShow(
                        OnlineHutLocationwiseFrgment.newInstance(
                            CITY_NAME_NORTH
                        )
                    )
                }
              ivSouthCity?.handleClickEvent {
                    detailsCallBack.addFragmentToStackAndShow(
                        OnlineHutLocationwiseFrgment.newInstance(
                            CITY_NAME_SOUTH
                        )
                    )
                }
            }
            ITEM_HUT -> {

                val listItem = literatureList.get(position - 3)
                val image = holder.itemView.findViewById<AppCompatImageView>(R.id.img)
                Glide.with(holder.view.context).load(listItem.fullImageUrl).into(image)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
              //  holder.bindingOnlineHut?.item = listItem
                 val constraintLayout:ConstraintLayout = holder.view.findViewById(R.id.constraint)
                constraintLayout.handleClickEvent {
                    listItem.refUrl?.let { detailsCallBack.openUrl(it) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 3 + literatureList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_HEADER
            }

            1 -> {
                ITEM_LOCATION
            }
            2 -> {
                ITEM_TITLE
            }
            else -> {
                ITEM_HUT
            }
        }
    }
}