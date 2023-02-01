package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
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

    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {
        var listBinding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list_pre_registrations,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = preRegList.get(position)

        val serialNo: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView)
        val title: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView2)
        title.text = listItem.name
        val address: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView3)
        address.text = listItem.permanentAddress
        val trackingNo: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView6)
        trackingNo.text = listItem.trackingNo
        val docNo: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView9)
        docNo.text = listItem.docNumber
        val phnNo: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView12)
        phnNo.text = listItem.phoneNumber
        val preRegistrationNo: AppCompatTextView =
            holder.itemView.findViewById(R.id.appCompatTextView15)
        preRegistrationNo.text = (listItem.preRegistrationNo ?: "Pending").toString()
        val status: AppCompatTextView = holder.itemView.findViewById(R.id.appCompatTextView18)
        status.text = listItem.status
        val serial = TimeFormtter.getNumberByLocale((position + 1).toString())
        serialNo.text = R.string.text_pre_reg.toString() + serial

        val constraintLayout8: ConstraintLayout =
            holder.itemView.findViewById(R.id.constraintLayout8)
        constraintLayout8?.handleClickEvent {
            constraintLayout8?.context?.copyToClipboard(listItem.trackingNo!!)
            Toast.makeText(
                constraintLayout8?.context,
                "Copied to clipboard!",
                Toast.LENGTH_LONG
            ).show()
        }
        val btnRefundRequest: AppCompatButton = holder.itemView.findViewById(R.id.btnRefundRequest)
        btnRefundRequest.setText(listItem.statusPayment)
        btnRefundRequest.isEnabled = listItem.isEnableBtn
        btnRefundRequest.setBackgroundResource(listItem.imageResource)
        btnRefundRequest?.handleClickEvent {

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