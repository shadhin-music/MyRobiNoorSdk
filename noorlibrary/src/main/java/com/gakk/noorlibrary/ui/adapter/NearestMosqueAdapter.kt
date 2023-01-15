package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.RowItemNearestMosqueBinding
import com.gakk.noorlibrary.model.nearby.PlaceInfo
import com.gakk.noorlibrary.util.PAGE_NEAREST_MOSQUE
import com.gakk.noorlibrary.util.handleClickEvent


class NearestMosqueAdapter(
    val callback: DetailsCallBack,
    val categoryType: String,
    placeInfoList: MutableList<PlaceInfo>
) :
    RecyclerView.Adapter<NearestMosqueAdapter.MosqueViewHolder>() {

    var placeInfoList: MutableList<PlaceInfo>?


    init {
        this.placeInfoList = placeInfoList
    }

    fun updatePlaceInfo(list: MutableList<PlaceInfo>) {
        placeInfoList = list
    }

    inner class MosqueViewHolder(itemView: RowItemNearestMosqueBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        var bindingMosqueHList: RowItemNearestMosqueBinding? = itemView

        fun bind(placeInfo: PlaceInfo, onItemClick: MapItemClickListener) {
            if (categoryType == PAGE_NEAREST_MOSQUE) {
                bindingMosqueHList?.imgMosque?.setImageResource(R.drawable.ic_mosque)
            } else {
                bindingMosqueHList?.imgMosque?.setImageResource(R.drawable.ic_restaurant)
            }

            bindingMosqueHList?.root?.handleClickEvent {
                onItemClick.let { click ->
                    click?.invoke(placeInfo)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MosqueViewHolder {

        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_nearest_mosque,
            parent,
            false
        )

        return MosqueViewHolder(binding as RowItemNearestMosqueBinding)
    }

    override fun onBindViewHolder(holder: MosqueViewHolder, position: Int) {

        holder.bindingMosqueHList?.let { binding ->
            placeInfoList?.let {
                val placeInfo = it[position]
                placeInfo.let { pi ->
                    binding.placeinfo = pi
                    holder.bind(pi, onItemClick)
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return placeInfoList!!.size
    }


    private var onItemClick: MapItemClickListener = null

    fun setOnItemClickListener(listener: MapItemClickListener) {
        onItemClick = listener
    }

}
typealias MapItemClickListener = ((PlaceInfo?) -> Unit)?
