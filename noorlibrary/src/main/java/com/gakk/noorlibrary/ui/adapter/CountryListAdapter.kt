package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.RowCountryItemBinding
import com.gakk.noorlibrary.model.currency.CurrencyModel
import com.gakk.noorlibrary.ui.fragments.hajj.CurrencyConverterFragment
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.hide
import com.gakk.noorlibrary.util.show

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/28/2021, Wed
 */
internal class CountryListAdapter(
    private val selected: Int,
    private val onItemClickListener: OnItemClickListener,
    private val selection: CurrencyConverterFragment.SELECTION
) :
    ListAdapter<CurrencyModel, CountryListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {

        fun bind(currencyModel: CurrencyModel, position: Int) {
            binding.apply {
                val context = binding.context
//                data = currencyModel
                val image:ImageView = itemView.findViewById(R.id.ic_flag)
                val checkedImg:ImageView = itemView.findViewById(R.id.checked_img)
                val name:AppCompatTextView = itemView.findViewById(R.id.name)
                name.text =currencyModel.entity+" - "+currencyModel.alphabeticCode
                val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
                  Glide.with(context).load(currencyModel.fullImageUrl).into(image)
                if (position == selected) {
                 checkedImg.show()
                  name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                } else {
                    checkedImg.hide()
                    name.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.txt_color_black
                        )
                    )
                }

                binding.handleClickEvent {
                    onItemClickListener.onItemClick(position, currencyModel, selection)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CurrencyModel>() {
            override fun areItemsTheSame(
                oldItem: CurrencyModel,
                newItem: CurrencyModel
            ): Boolean = oldItem.alphabeticCode == newItem.alphabeticCode

            override fun areContentsTheSame(
                oldItem: CurrencyModel,
                newItem: CurrencyModel
            ): Boolean = oldItem.numericCode == newItem.numericCode

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_country_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    interface OnItemClickListener {
        fun onItemClick(
            postion: Int,
            currencyModel: CurrencyModel,
            selection: CurrencyConverterFragment.SELECTION
        )
    }
}