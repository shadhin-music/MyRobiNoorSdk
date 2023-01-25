package com.gakk.noorlibrary.ui.adapter.eidjamat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.eidjamat.MapOpenControllerJamat
import com.gakk.noorlibrary.util.handleClickEvent

internal class EidJamatAdapter(
    val jamatList: MutableList<Literature>,
    detailsCallBack: DetailsCallBack,
    val mapOpenController: MapOpenControllerJamat
) :
    RecyclerView.Adapter<EidJamatAdapter.EidJamatViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_LIST = 1
    val mDetailsCallBack = detailsCallBack

    inner class EidJamatViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EidJamatViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.item_eid_jamat_header,parent,false)

                return EidJamatViewHolder(view)

            }

            else -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_eid_jamat,parent,false)

                return EidJamatViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(holder: EidJamatViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_HEADER -> {
                val ivHeader = holder.view.findViewById<AppCompatImageView>(R.id.ivHeader)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
                 Glide.with(holder.view.context).load(ImageFromOnline("header_eid_jamat.png").fullImageUrl).into(ivHeader)
               // holder.bindingHeader?.item =
            }

            ITEM_LIST -> {
                val listItem = jamatList.get(position - 1)
             //   holder.bindingOrganizations?.literature = listItem
               val  titleMosque = holder.view.findViewById<AppCompatTextView>(R.id.titleMosque)
                   titleMosque.text = listItem.title
                val  tvLocationMosque = holder.view.findViewById<AppCompatTextView>(R.id.tvLocationMosque)
                tvLocationMosque.text = listItem.text
                val ivDirection = holder.view.findViewById<AppCompatImageView>(R.id.ivDirection)
              ivDirection?.handleClickEvent {
                    mapOpenController.openMap(
                        listItem.latitude?.toDouble()!!,
                        listItem.longitude?.toDouble()!!
                    )
                }

                titleMosque?.handleClickEvent {

                    when (tvLocationMosque?.visibility) {
                        View.VISIBLE -> {
                            listItem.isExand = false
                  tvLocationMosque?.visibility = View.GONE
                        }
                        View.GONE -> {
                            listItem.isExand = true
                        tvLocationMosque?.visibility = View.VISIBLE
                        }
                    }
                }

                if (listItem.isExand) {
                    tvLocationMosque?.visibility = View.VISIBLE
                } else {
                    tvLocationMosque?.visibility = View.GONE
                }
            }

            else -> {

            }
        }

    }

    override fun getItemCount(): Int {
        return 1 + jamatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_HEADER
            }

            else -> {
                ITEM_LIST
            }
        }
    }
}