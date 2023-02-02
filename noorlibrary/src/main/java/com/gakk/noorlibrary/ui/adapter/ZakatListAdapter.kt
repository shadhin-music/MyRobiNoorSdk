package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.zakat.ZakatModel
import com.gakk.noorlibrary.util.*

const val ZAKAT_CALC_VIEW = 1

class ZakatListAdapter(
    zakatList: List<ZakatModel>,
    private val onItemClickListener: OnItemClickListener?,
) :
    RecyclerView.Adapter<ZakatListAdapter.ViewHolder>() {

    var mZakatList = zakatList


    inner class ViewHolder(layoutId: Int, layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {

        var view: View = layoutView
        val layoutTag = layoutId


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var view: View

        when (viewType) {
            ZAKAT_CALC_VIEW -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_saved_jakat, parent, false)

                return ViewHolder(ZAKAT_CALC_VIEW, view)
            }

            NO_DATA -> {


                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_no_data, parent, false)

                return ViewHolder(NO_DATA, view)
            }

            else -> throw IllegalStateException("Illegal view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.layoutTag) {
            ZAKAT_CALC_VIEW -> {

                val tvDate = holder.view.findViewById<AppCompatTextView>(R.id.tvDate)
                val ivDelete = holder.view.findViewById<AppCompatImageView>(R.id.ivDelete)
                val tvContent = holder.view.findViewById<AppCompatTextView>(R.id.tvContent)
                val tvContentJakat = holder.view.findViewById<AppCompatTextView>(R.id.tvContentJakat)

                val data = mZakatList[position]

                val totalAsset =
                    data.cash!! + data.cashInBankaccount!! + data.valueOfGold!! + data.valueOfSilver!! + data.stockMarketInvestment!! + data.otherInvestments!! + data.houseRent!! + data.property!! + data.businessPayment!! + data.products!! + data.pension!! + data.familyLoansAndOthers!! + data.otherCapital!! + data.agriculture!!

                val totalLiabilities =
                    data.creditCardPayment!! + data.carPayment!! + data.businessPayment!! + data.familyLoan!! + data.otherLoans!!


                tvDate.text = data.createdOn?.let { formatDate(it) }
                tvContent.text = totalAsset.toString()
                tvContentJakat.text = totalLiabilities.toString()

                ivDelete.handleClickEvent {
                    onItemClickListener?.onItemClick(position, mZakatList)
                }
            }

            NO_DATA -> {

                val imgNoInternet = holder.view.findViewById<ImageView>(R.id.imgNoInternet)

                val itemNoData = ImageFromOnline("bg_no_data.png")
                setImageFromUrlNoProgress(imgNoInternet,itemNoData.fullImageUrl)

                holder.view.hide()

            }
        }

    }

    override fun getItemCount(): Int {
        return mZakatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            mZakatList.size > 0 -> ZAKAT_CALC_VIEW
            else -> NO_DATA
        }
    }

    fun updateZakatList(list: List<ZakatModel>) {
        mZakatList = listOf()
        mZakatList = list
    }

    interface OnItemClickListener {
        fun onItemClick(
            postion: Int,
            zakat: List<ZakatModel>
        )
    }
}