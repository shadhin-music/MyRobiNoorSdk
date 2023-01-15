package com.gakk.noorlibrary.ui.adapter.eidjamat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.ItemEidJamatHeaderBinding
import com.gakk.noorlibrary.databinding.LayoutItemEidJamatBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.eidjamat.MapOpenControllerJamat
import com.gakk.noorlibrary.util.handleClickEvent

class EidJamatAdapter(
    val jamatList: MutableList<Literature>,
    detailsCallBack: DetailsCallBack,
    val mapOpenController: MapOpenControllerJamat
) :
    RecyclerView.Adapter<EidJamatAdapter.EidJamatViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_LIST = 1
    val mDetailsCallBack = detailsCallBack

    inner class EidJamatViewHolder : RecyclerView.ViewHolder {
        var bindingHeader: ItemEidJamatHeaderBinding? = null

        constructor(itemView: ItemEidJamatHeaderBinding) : super(itemView.root) {
            bindingHeader = itemView
        }

        var bindingOrganizations: LayoutItemEidJamatBinding? = null

        constructor(itemView: LayoutItemEidJamatBinding) : super(itemView.root) {
            bindingOrganizations = itemView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EidJamatViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val binding: ItemEidJamatHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_eid_jamat_header,
                    parent,
                    false
                )
                return EidJamatViewHolder(binding)
            }

            else -> {
                val binding: LayoutItemEidJamatBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_eid_jamat,
                    parent,
                    false
                )
                return EidJamatViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: EidJamatViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_HEADER -> {
                holder.bindingHeader?.item = ImageFromOnline("header_eid_jamat.png")
            }

            ITEM_LIST -> {
                val listItem = jamatList.get(position - 1)
                holder.bindingOrganizations?.literature = listItem

                holder.bindingOrganizations?.ivDirection?.handleClickEvent {
                    mapOpenController.openMap(
                        listItem.latitude?.toDouble()!!,
                        listItem.longitude?.toDouble()!!
                    )
                }

                holder.bindingOrganizations?.titleMosque?.handleClickEvent {

                    when (holder.bindingOrganizations?.tvLocationMosque?.visibility) {
                        View.VISIBLE -> {
                            listItem.isExand = false
                            holder.bindingOrganizations?.tvLocationMosque?.visibility = View.GONE
                        }
                        View.GONE -> {
                            listItem.isExand = true
                            holder.bindingOrganizations?.tvLocationMosque?.visibility = View.VISIBLE
                        }
                    }
                }

                if (listItem.isExand) {
                    holder.bindingOrganizations?.tvLocationMosque?.visibility = View.VISIBLE
                } else {
                    holder.bindingOrganizations?.tvLocationMosque?.visibility = View.GONE
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