package com.gakk.noorlibrary.ui.adapter.hajjtracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.RowItemHajjUserBinding
import com.gakk.noorlibrary.model.hajjtracker.HajjSharingListResponse
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.BottomSheetDisplay
import com.gakk.noorlibrary.util.handleClickEvent

class HajjSharingListAdapter(
    val sharingList: List<HajjSharingListResponse.Data>?,
    val bottomSheetDisplay: BottomSheetDisplay
) :
    RecyclerView.Adapter<HajjSharingListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowItemHajjUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var inspirationBinding: RowItemHajjUserBinding? = binding

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowItemHajjUserBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_hajj_user,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = sharingList?.get(position)
        holder.inspirationBinding?.user = list

        holder.inspirationBinding?.clShareContainer?.handleClickEvent {
            bottomSheetDisplay.showUserOnMap(list?.trackerPhone, list?.trackerName)
        }
        holder.inspirationBinding?.ivDeleteUser?.handleClickEvent {
           // bottomSheetDisplay.deleteHajjUser(list?.id)
        }

    }

    override fun getItemCount(): Int {
        return sharingList?.size ?: 0
    }
}