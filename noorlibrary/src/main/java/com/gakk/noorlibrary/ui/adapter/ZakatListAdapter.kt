package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.ui.fragments.zakat.DeleteOperation
import com.gakk.noorlibrary.util.NoDataLayout
import com.gakk.noorlibrary.util.handleClickEvent

const val ZAKAT_CALC_VIEW = 1

class ZakatListAdapter(zakatList: List<ZakatDataModel>, deleteOperation: DeleteOperation) :
    RecyclerView.Adapter<ZakatListAdapter.ViewHolder>() {

    val mDeleteOperation: DeleteOperation
    var mZakatList = zakatList

    init {
        mDeleteOperation = deleteOperation
    }

    inner class ViewHolder(layoutId: Int, layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {

         var view: View = layoutView
         val layoutTag = layoutId


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var view:View

        when (viewType) {
            ZAKAT_CALC_VIEW -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_saved_jakat,parent,false)

                return ViewHolder(ZAKAT_CALC_VIEW,view)
            }

            NO_DATA -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_data,parent,false)

                return ViewHolder(NO_DATA,view)
            }

            else -> throw IllegalStateException("Illegal view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when(holder.layoutTag)
        {
            ZAKAT_CALC_VIEW ->
            {
                val  tvDate = holder.view.findViewById<AppCompatTextView>(R.id.tvDate)
                val ivDelete = holder.view.findViewById<AppCompatImageView>(R.id.ivDelete)
                val data = mZakatList[position]
                tvDate.text = data.date

                ivDelete.handleClickEvent {

                    mDeleteOperation.deleteData(mZakatList[position])
                }
            }

            NO_DATA ->
            {
                NoDataLayout(holder.view)
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