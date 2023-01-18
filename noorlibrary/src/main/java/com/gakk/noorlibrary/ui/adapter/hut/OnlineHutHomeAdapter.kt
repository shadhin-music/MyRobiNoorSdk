package com.gakk.noorlibrary.ui.adapter.hut

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R

import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.ItemHeaderOnlineHutBinding
import com.gakk.noorlibrary.databinding.LayoutItemLocationwiseHutBinding
import com.gakk.noorlibrary.databinding.LayoutItemOnlineHutBinding
import com.gakk.noorlibrary.databinding.LayoutItemTitleHutBinding
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

    inner class ViewHolder : RecyclerView.ViewHolder {

        var bindingItemHeaderOnlineHutBinding: ItemHeaderOnlineHutBinding? = null

        constructor(itemView: ItemHeaderOnlineHutBinding) : super(itemView.root) {
            bindingItemHeaderOnlineHutBinding = itemView
        }

        var bindingLocationwiseHut: LayoutItemLocationwiseHutBinding? = null

        constructor(itemView: LayoutItemLocationwiseHutBinding) : super(itemView.root) {
            bindingLocationwiseHut = itemView
        }

        var bindingTitleHut: LayoutItemTitleHutBinding? = null

        constructor(itemView: LayoutItemTitleHutBinding) : super(itemView.root) {
            bindingTitleHut = itemView
        }

        var bindingOnlineHut: LayoutItemOnlineHutBinding? = null

        constructor(itemView: LayoutItemOnlineHutBinding) : super(itemView.root) {
            bindingOnlineHut = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val binding: ItemHeaderOnlineHutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_header_online_hut,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }

            ITEM_LOCATION -> {
                val binding: LayoutItemLocationwiseHutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_locationwise_hut,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }

            ITEM_TITLE -> {
                val binding: LayoutItemTitleHutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_title_hut,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }

            else -> {
                val binding: LayoutItemOnlineHutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_online_hut,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {

            ITEM_HEADER -> {
                holder.bindingItemHeaderOnlineHutBinding?.item = ImageFromOnline("ic_header_online_hut.png")
            }
            ITEM_LOCATION -> {
                holder.bindingLocationwiseHut?.itemNorth =
                    ImageFromOnline("ic_north_city_corporation.png")
                holder.bindingLocationwiseHut?.item =
                    ImageFromOnline("ic_south_city_corporation.png")
                holder.bindingLocationwiseHut?.ivNorthCity?.handleClickEvent {
                    detailsCallBack.addFragmentToStackAndShow(
                        OnlineHutLocationwiseFrgment.newInstance(
                            CITY_NAME_NORTH
                        )
                    )
                }

                holder.bindingLocationwiseHut?.ivSouthCity?.handleClickEvent {
                    detailsCallBack.addFragmentToStackAndShow(
                        OnlineHutLocationwiseFrgment.newInstance(
                            CITY_NAME_SOUTH
                        )
                    )
                }
            }
            ITEM_HUT -> {

                val listItem = literatureList.get(position - 3)
                holder.bindingOnlineHut?.item = listItem

                holder.bindingOnlineHut?.root?.handleClickEvent {
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