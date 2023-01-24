package com.gakk.noorlibrary.ui.adapter.donation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.HeaderDonateZakatBinding
import com.gakk.noorlibrary.databinding.LayoutItemCharityOrganizationsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.util.*

internal class DonateZakatAdapter(
    val organizationList: MutableList<Literature>,
    val literature: Literature,
    detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<DonateZakatAdapter.DonateZakatViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_ORGANIZATIONS = 1
    val mDetailsCallBack = detailsCallBack

    inner class DonateZakatViewHolder (layoutView: View): RecyclerView.ViewHolder (layoutView){
        var view: View = layoutView
//        var bindingHeader: HeaderDonateZakatBinding? = null
//
//        constructor(itemView: HeaderDonateZakatBinding) : super(itemView.root) {
//            bindingHeader = itemView
//        }
//
//        var bindingOrganizations: LayoutItemCharityOrganizationsBinding? = null
//
//        constructor(itemView: LayoutItemCharityOrganizationsBinding) : super(itemView.root) {
//            bindingOrganizations = itemView
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateZakatViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.header_donate_zakat,parent,false)

                return DonateZakatViewHolder(view)
//                val binding: HeaderDonateZakatBinding = DataBindingUtil.inflate(
//                    LayoutInflater.from(parent.context),
//                    R.layout.header_donate_zakat,
//                    parent,
//                    false
//                )
//                return DonateZakatViewHolder(binding)
            }

            else -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_charity_organizations,parent,false)
                return DonateZakatViewHolder(view)
//                val binding: LayoutItemCharityOrganizationsBinding = DataBindingUtil.inflate(
//                    LayoutInflater.from(parent.context),
//                    R.layout.layout_item_charity_organizations,
//                    parent,
//                    false
//                )
//                return DonateZakatViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: DonateZakatViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_HEADER -> {
//                holder.bindingHeader?.item = literature
                 val details = holder.view.findViewById<AppCompatTextView>(R.id.tvDetails)
                 details.text = literature.text
                val constraintLayout2 = holder.view.findViewById<ConstraintLayout>(R.id.constraintLayout2)
                   constraintLayout2?.handleClickEvent {
                    Intent(constraintLayout2?.context, DetailsActivity::class.java).apply {
                        this.putExtra(PAGE_NAME, PAGE_JAKAT_NEW_CALCULATION)
                        constraintLayout2?.context?.startActivity(this)
                    }
                }
            }

            ITEM_ORGANIZATIONS -> {
                val listItem = organizationList.get(position - 1)
//                holder.bindingOrganizations?.literature = listItem
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
               val clItem = holder.view.findViewById<ConstraintLayout>(R.id.clItem)
                  clItem?.handleClickEvent {
                      val fragment = FragmentProvider.getFragmentByName(
                          PAGE_DONATION_FORM,
                          detailsActivityCallBack = mDetailsCallBack,
                          literature = listItem
                      )
                      mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                  }
            }

            else -> {

            }
        }

    }

    override fun getItemCount(): Int {
        return 1 + organizationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_HEADER
            }

            else -> {
                ITEM_ORGANIZATIONS
            }
        }
    }
}