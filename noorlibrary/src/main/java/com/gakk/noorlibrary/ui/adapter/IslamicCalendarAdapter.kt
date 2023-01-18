package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemIslamicCalendarBinding
import com.gakk.noorlibrary.model.calender.IslamicCalendarModel
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.invisible
import com.gakk.noorlibrary.util.show


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */
internal class IslamicCalendarAdapter(arrayList: ArrayList<IslamicCalendarModel>) :
    RecyclerView.Adapter<IslamicCalendarAdapter.ViewHolder>() {

    private var listItemClickListener: ListItemClickListener? = null
    private val mDataList = arrayList

    interface ListItemClickListener {
        fun onItemClick(i: Int, view: View)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val binding: ItemIslamicCalendarBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_islamic_calendar, viewGroup, false
        )

        return ViewHolder(
            binding,
            listItemClickListener
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.binding.txtVw1.text = TimeFormtter.getNumberByLocale(mDataList[i].dayTxt)
        if (mDataList[i].isToday) {
            viewHolder.binding.txtVw1.setTextColor(ContextCompat.getColor(viewHolder.binding.root.context, R.color.white))
            viewHolder.binding.llBg.show()
        } else {
            viewHolder.binding.txtVw1.setTextColor(ContextCompat.getColor(viewHolder.binding.root.context, R.color.txt_color_black))
            viewHolder.binding.llBg.invisible()
        }
    }

    fun setOnItemClickListener(listItemClickListener2: ListItemClickListener?) {
        listItemClickListener = listItemClickListener2
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }


    class ViewHolder(
        val binding: ItemIslamicCalendarBinding,
        private val listItemClickListener: ListItemClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        override fun onClick(view: View?) {
            val listItemClickListener2 = listItemClickListener
            listItemClickListener2?.onItemClick(layoutPosition, binding.root)
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }

}