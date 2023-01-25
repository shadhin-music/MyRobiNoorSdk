package com.gakk.noorlibrary.extralib.country_code_picker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.ItemCcpBinding
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl
import org.w3c.dom.Text
import kotlin.collections.ArrayList


class ccpAdapter(
    private var ccpList: ArrayList<CCPmodel>,
    private val onItemClickListener: OnItemClickListener?,
) : RecyclerView.Adapter<ccpAdapter.ViewHolder>(),Filterable {

    var ccp_filter: ArrayList<CCPmodel> = ccpList

    inner class ViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {

        fun bind(ccp_model: CCPmodel, position: Int) {
            binding.apply {

                val ic_flag = this.findViewById<ImageView>(R.id.ic_flag)
                val progressBar = this.findViewById<ProgressBar>(R.id.progressBar)
                val country_name = this.findViewById<TextView>(R.id.country_name)
                val country_code = this.findViewById<TextView>(R.id.country_code)
                val data = ccp_model

                setImageFromUrl(ic_flag,data.fullImageUrl,progressBar)

                country_name.text = data.countryName
                country_code.text = data.dialCode

                this.handleClickEvent {
                    onItemClickListener?.onItemClick(position, ccp_filter)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CCPmodel>() {
            override fun areItemsTheSame(
                oldItem: CCPmodel,
                newItem: CCPmodel
            ): Boolean = oldItem.countryCode == newItem.countryCode

            override fun areContentsTheSame(
                oldItem: CCPmodel,
                newItem: CCPmodel
            ): Boolean = oldItem.dialCode == newItem.dialCode

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ccp,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ccp_filter[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return ccp_filter.size
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) ccp_filter = ccpList else {
                    val filteredList = ArrayList<CCPmodel>()
                    ccpList
                        .filter {
                            (it.countryName.lowercase().contains(constraint.toString().lowercase())) or
                                    (it.countryCode.lowercase().contains(constraint.toString().lowercase()))

                        }
                        .forEach { filteredList.add(it) }
                    ccp_filter = filteredList

                }
                return FilterResults().apply { values = ccp_filter }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                ccp_filter = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<CCPmodel>

                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            postion: Int,
            ccp_list: ArrayList<CCPmodel>
        )
    }

}
