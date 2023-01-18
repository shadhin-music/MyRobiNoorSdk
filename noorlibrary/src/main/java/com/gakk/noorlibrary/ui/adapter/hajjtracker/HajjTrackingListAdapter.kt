package com.gakk.noorlibrary.ui.adapter.hajjtracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.RowItemHajjTrackingListBinding
import com.gakk.noorlibrary.model.hajjtracker.HajjTrackingListResponse
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.BottomSheetDisplay
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.TrackerListControl
import com.gakk.noorlibrary.util.VideoDiffCallback
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.videoNewList

internal class HajjTrackingListAdapter(
    val sharingList: List<HajjTrackingListResponse.Data>?,
    val bottomSheetDisplay: BottomSheetDisplay,
    val trackerListControl: TrackerListControl
) :
    RecyclerView.Adapter<HajjTrackingListAdapter.ViewHolder>() {


    inner class ViewHolder(binding: RowItemHajjTrackingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var inspirationBinding: RowItemHajjTrackingListBinding? = binding

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowItemHajjTrackingListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_hajj_tracking_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = sharingList?.get(position)
        holder.inspirationBinding?.user = list

        holder.inspirationBinding?.clContainer?.handleClickEvent {
            bottomSheetDisplay.showUserOnMap(list?.sharerPhone, list?.trackerName)
        }

        holder.inspirationBinding?.ivDeleteUser?.handleClickEvent {
            trackerListControl.deleteHajjUser(list?.id)
        }
    }

    override fun getItemCount(): Int {
        return sharingList?.size ?: 0
    }
}