package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutItemSavedJakatBinding
import com.gakk.noorlibrary.databinding.LayoutNoDataBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.ui.fragments.zakat.DeleteOperation
import com.gakk.noorlibrary.util.handleClickEvent

const val ZAKAT_CALC_VIEW = 1

class ZakatListAdapter(zakatList: List<ZakatDataModel>, deleteOperation: DeleteOperation) :
    RecyclerView.Adapter<ZakatListAdapter.ViewHolder>() {

    val mDeleteOperation: DeleteOperation
    var mZakatList = zakatList

    init {
        mDeleteOperation = deleteOperation
    }

    inner class ViewHolder :
        RecyclerView.ViewHolder {


        var jakatBinding: LayoutItemSavedJakatBinding? = null

        constructor(itemView: LayoutItemSavedJakatBinding) : super(itemView.root) {
            jakatBinding = itemView
        }

        var noDataBinding: LayoutNoDataBinding? = null

        constructor(itemView: LayoutNoDataBinding) : super(itemView.root) {
            noDataBinding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding

        when (viewType) {
            ZAKAT_CALC_VIEW -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_saved_jakat,
                    parent,
                    false
                )


                return ViewHolder(binding as LayoutItemSavedJakatBinding)
            }

            NO_DATA -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_no_data,
                    parent,
                    false
                )

                return ViewHolder(binding as LayoutNoDataBinding)
            }

            else -> throw IllegalStateException("Illegal view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.noDataBinding?.let {
            it.item = ImageFromOnline("bg_no_data.png")
        }

        holder.jakatBinding?.let {
            it.data = mZakatList[position]
        }


        holder.jakatBinding?.let {
            it.ivDelete!!.handleClickEvent {
                mDeleteOperation.deleteData(mZakatList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        var size = mZakatList.size
        if (size == 0)
            return 1
        return size
    }

    override fun getItemViewType(position: Int): Int {
        when {
            mZakatList.size > 0 -> return ZAKAT_CALC_VIEW
            else -> return NO_DATA
        }
    }

    fun updateZakatList(list: List<ZakatDataModel>) {
        mZakatList = listOf()
        mZakatList = list
        // notifyDataSetChanged()
    }
}