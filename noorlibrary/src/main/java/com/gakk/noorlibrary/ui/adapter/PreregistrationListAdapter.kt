package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.ItemListPreRegistrationsBinding
import com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.PaymentControl
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.RefundRequestFragment
import com.gakk.noorlibrary.util.STATUS_PENDING
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.copyToClipboard
import com.gakk.noorlibrary.util.handleClickEvent

internal class PreregistrationListAdapter(
    val preRegList: MutableList<HajjPreRegistrationListResponse.Data>,
    val detailsCallBack: DetailsCallBack?,
    var paymentControl: PaymentControl
) :
    RecyclerView.Adapter<PreregistrationListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemListPreRegistrationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var listBinding: ItemListPreRegistrationsBinding? = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemListPreRegistrationsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_list_pre_registrations,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = preRegList.get(position)

        holder.listBinding?.item = listItem
        holder.listBinding?.serial = TimeFormtter.getNumberByLocale((position + 1).toString())

        holder.listBinding?.executePendingBindings()

        holder.listBinding?.constraintLayout8?.handleClickEvent {
            holder.listBinding?.constraintLayout8?.context?.copyToClipboard(listItem.trackingNo!!)
            Toast.makeText(
                holder.listBinding?.constraintLayout8?.context,
                "Copied to clipboard!",
                Toast.LENGTH_LONG
            ).show()
        }

        holder.listBinding?.btnRefundRequest?.handleClickEvent {

            if (listItem.status.equals(STATUS_PENDING)) {
                paymentControl.gotoPaymentPage(listItem.trackingNo, listItem.name, listItem.email)
            } else {
                detailsCallBack?.addFragmentToStackAndShow(
                    RefundRequestFragment.newInstance()
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return preRegList.size
    }
}