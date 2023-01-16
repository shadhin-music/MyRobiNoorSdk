package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemIslamicChutiBinding
import com.gakk.noorlibrary.model.literature.Literature


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */
class IslamicChhutiAdapter(val mDataList: MutableList<Literature>) :
    RecyclerView.Adapter<IslamicChhutiAdapter.ViewHolder>() {

    private var listItemClickListener: ListItemClickListener? = null
    // private val mDataList: ArrayList<IslamicChhutiModel> = arrayList

    interface ListItemClickListener {
        fun onItemClick(i: Int, view: View?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val binding: ItemIslamicChutiBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context), R.layout.item_islamic_chuti, viewGroup, false
        )

        return ViewHolder(binding, listItemClickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.binding.literature = mDataList.get(i)
        /*  if (mDataList[i].chutiTitle != null
              && mDataList[i].chutiTitle?.isNotEmpty() == true
              && !mDataList[i].chutiTitle
                  .equals("null", false)
          ) {
              viewHolder.binding.txtVwTitle.text = mDataList[i].chutiTitle
          }
          var dateTV = ""
          if (mDataList[i].gragrian != null && mDataList[i].gragrian?.isNotEmpty() == true && !mDataList[i].gragrian.equals(
                  "null",
                  false
              )
          ) {
              dateTV = mDataList[i].gragrian ?: ""
          }

          if (mDataList[i].iDate != null && mDataList[i].iDate?.isNotEmpty() == true && !mDataList[i].iDate.equals(
                  "null",
                  false
              )
          ) {
              dateTV = dateTV + " • " + mDataList[i].iDate
          }

          viewHolder.binding.txtDate.text = dateTV*/
    }

    fun setOnItemClickListener(listItemClickListener2: ListItemClickListener?) {
        listItemClickListener = listItemClickListener2
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(
        val binding: ItemIslamicChutiBinding,
        private val listItemClickListener: ListItemClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val listItemClickListener2 = listItemClickListener
            listItemClickListener2?.onItemClick(layoutPosition, binding.root)
        }
    }


}