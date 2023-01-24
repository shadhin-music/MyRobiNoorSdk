package com.mcc.noor.ui.adapter.umrahhajj

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.mcc.noor.model.umrah_hajj.UmrahHajjRegData
import org.w3c.dom.Text
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UmrahPaymentHistoryAdapter(
    private var paymentList: List<UmrahHajjRegData?>

    ):RecyclerView.Adapter<UmrahPaymentHistoryAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {

        fun bind(paylist: UmrahHajjRegData, position: Int) {
            binding.apply {

                val data = paylist
                val statustextview = binding.findViewById<TextView>(R.id.status)
                val statusIcon = binding.findViewById<ImageView>(R.id.statusIcon)
                val statusBar = binding.findViewById<CardView>(R.id.statusBar)

                if(data.paymentStatus.equals("paid"))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        statustextview.setTextColor(resources.getColor(R.color.colorPrimary,binding.context.theme))

                    }else {
                        statustextview.setTextColor(resources.getColor(R.color.colorPrimary))

                    }

                    statusIcon.setBackgroundResource(R.drawable.ic_check_green)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        statusBar.backgroundTintList = resources.getColorStateList(R.color.status_success,binding.context.theme)

                    }else {
                        statusBar.backgroundTintList = resources.getColorStateList(R.color.status_success)

                    }

                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        statustextview.setTextColor(resources.getColor(R.color.ash,binding.context.theme))

                    }else {
                        statustextview.setTextColor(resources.getColor(R.color.ash))

                    }

                    statusIcon.setBackgroundResource(R.drawable.ic_info)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        statusBar.backgroundTintList = resources.getColorStateList(R.color.status_failed,binding.context.theme)

                    }else {
                        statusBar.backgroundTintList = resources.getColorStateList(R.color.status_failed)

                    }
                }

                binding.findViewById<TextView>(R.id.title).text = data.umrahPackTitle


                binding.findViewById<TextView>(R.id.amount).text = context.getString(R.string.umrah_hajj_pay_history_price).format("বুকিংয়ের পরিমাণ : ",paylist.packPrice," টাকা")
                if(paylist.paymentStatus == "paid")
                    statustextview.text = "সম্পন্ন"
                else
                    statustextview.text = "ব্যর্থ"

                binding.findViewById<TextView>(R.id.datetime).text = formatDate(paylist.registrationDate.toString())

            }
        }
    }

    private fun formatDate(dateString: String): String? {
        try {
            var sd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val d: Date = sd.parse(dateString)
            sd = SimpleDateFormat("HH:mm aa • dd MMMM ,yyyy")
            return sd.format(d)
        } catch (e: ParseException) {
        }
        return ""
    }

        companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UmrahHajjRegData>() {
            override fun areItemsTheSame(
                oldItem: UmrahHajjRegData,
                newItem: UmrahHajjRegData
            ): Boolean = oldItem.passportNumber == newItem.passportNumber

            override fun areContentsTheSame(
                oldItem: UmrahHajjRegData,
                newItem: UmrahHajjRegData
            ): Boolean = oldItem.passportNumber == newItem.passportNumber

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_umrah_hajj_payment_history,parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = paymentList[position]
        if (item != null) {
            holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(
            postion: Int,
            payment_list: UmrahHajjRegData
        )
    }


}