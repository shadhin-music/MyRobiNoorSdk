package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

    inner class ViewHolder(private val binding: RowCountryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currencyModel: CurrencyModel, position: Int) {
            binding.apply {
                val context = binding.root.context
                data = currencyModel

                if (position == selected) {
                    binding.checkedImg.show()
                    binding.name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                } else {
                    binding.checkedImg.hide()
                    binding.name.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.txt_color_black
                        )
                    )
                }

                root.handleClickEvent {
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
        val binding: RowCountryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_country_item,
            parent,
            false
        )
        return ViewHolder(binding)
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