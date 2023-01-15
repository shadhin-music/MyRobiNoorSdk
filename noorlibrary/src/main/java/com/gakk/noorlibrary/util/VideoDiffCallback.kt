package com.gakk.noorlibrary.util

import androidx.recyclerview.widget.DiffUtil
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse

class VideoDiffCallback(
    private val old: List<KhatamQuranVideosResponse.Data>,
    private val new: List<KhatamQuranVideosResponse.Data>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
