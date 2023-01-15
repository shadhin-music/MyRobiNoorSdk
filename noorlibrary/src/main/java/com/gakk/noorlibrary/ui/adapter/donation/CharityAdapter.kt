package com.gakk.noorlibrary.ui.adapter.donation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemCharityOrganizationsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.ORGANIZATION_DETAILS
import com.gakk.noorlibrary.util.handleClickEvent

class CharityAdapter(
    val literatureList: MutableList<Literature>,
                     val detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<CharityAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemCharityOrganizationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var charityBinding: LayoutItemCharityOrganizationsBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemCharityOrganizationsBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_charity_organizations,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = literatureList.get(position)
        holder.charityBinding?.literature = listItem

        holder.charityBinding?.clItem?.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                ORGANIZATION_DETAILS,
                detailsActivityCallBack = detailsCallBack,
                literature = listItem
            )
            detailsCallBack.addFragmentToStackAndShow(fragment!!)
        }
    }

    override fun getItemCount(): Int {
        return literatureList.size
    }
}