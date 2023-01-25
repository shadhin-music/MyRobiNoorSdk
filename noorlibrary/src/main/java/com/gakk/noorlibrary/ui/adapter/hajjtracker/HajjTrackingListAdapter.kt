package com.gakk.noorlibrary.ui.adapter.hajjtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.hajjtracker.HajjTrackingListResponse
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.BottomSheetDisplay
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.TrackerListControl
import com.gakk.noorlibrary.util.CircleImageView
import com.gakk.noorlibrary.util.handleClickEvent

internal class HajjTrackingListAdapter(
    val sharingList: List<HajjTrackingListResponse.Data>?,
    val bottomSheetDisplay: BottomSheetDisplay,
    val trackerListControl: TrackerListControl
) :
    RecyclerView.Adapter<HajjTrackingListAdapter.ViewHolder>() {


    inner class ViewHolder(layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val  view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_hajj_tracking_list,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = sharingList?.get(position)

        val image = holder.itemView.findViewById<CircleImageView>(R.id.ivUser)
        val ivDeleteUser = holder.itemView.findViewById<AppCompatImageView>(R.id.ivDeleteUser)
        Glide.with(holder.view.context).load(list?.fullImageUrl).into(image)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
        //  holder.inspirationBinding?.user = list
        val tvTitle = holder.itemView.findViewById<AppCompatTextView>(R.id.userNameTracker)
        tvTitle.text = list?.trackerName
        val tvPhone = holder.itemView.findViewById<AppCompatTextView>(R.id.userNumberTracker)
        tvPhone.text = list?.sharerPhone
        val clContainer: ConstraintLayout = holder.view.findViewById(R.id.clContainer)
        clContainer?.handleClickEvent {
            bottomSheetDisplay.showUserOnMap(list?.sharerPhone, list?.trackerName)
        }
        ivDeleteUser?.handleClickEvent {
            trackerListControl.deleteHajjUser(list?.id)

        }
    }

    override fun getItemCount(): Int {
        return sharingList?.size ?: 0
    }
}