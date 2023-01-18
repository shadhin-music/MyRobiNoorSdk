package com.gakk.noorlibrary.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutFullPlayerContentBinding
import com.gakk.noorlibrary.databinding.LayoutFullPlayerHeaderBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.util.handleClickEvent


const val HEADER = 0
const val CONTENT = 1

internal class FullPlayerAdapter(surahList: MutableList<Data>, clickAction: (Int, String) -> Unit) :
    RecyclerView.Adapter<FullPlayerAdapter.FullPlayerViewHolder>() {


    var mSurahList: MutableList<Data> = surahList
    var mClickAction = clickAction

    inner class FullPlayerViewHolder : RecyclerView.ViewHolder {

        var headerBinding: LayoutFullPlayerHeaderBinding? = null

        @SuppressLint("MissingPermission")
        constructor(binding: LayoutFullPlayerHeaderBinding) : super(binding.root) {
            headerBinding = binding
        }

        var normalContentBinding: LayoutFullPlayerContentBinding? = null

        constructor(binding: LayoutFullPlayerContentBinding) : super(binding.root) {
            normalContentBinding = binding

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullPlayerViewHolder {
        return when (viewType) {
            HEADER -> {
                val binding: LayoutFullPlayerHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_full_player_header,
                    parent,
                    false
                )
                FullPlayerViewHolder(binding)
            }

            else -> {
                val binding: LayoutFullPlayerContentBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_full_player_content,
                    parent,
                    false
                )
                FullPlayerViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: FullPlayerViewHolder, position: Int) {
        holder?.headerBinding?.let {
            it.surah = mSurahList.get(0)
            it.item = ImageFromOnline("bg_quran.png")
        }
        holder?.normalContentBinding?.let {
            var pos = position - 1
            it.surah = mSurahList.get(pos)

            it.root?.handleClickEvent {
                mClickAction(pos, mSurahList!!.get(pos)!!.id!!)
            }

            it?.let {
                when (pos) {
                    0 -> {
                        it.scrim.visibility = VISIBLE
                        it.number.setTextColor(it.root.context.resources.getColor(R.color.colorPrimary))
                        it.title.setTextColor(it.root.context.resources.getColor(R.color.colorPrimary))
                    }
                    else -> {
                        it.scrim.visibility = GONE
                        it.number.setTextColor(it.root.context.resources.getColor(R.color.txt_color_black))
                        it.title.setTextColor(it.root.context.resources.getColor(R.color.txt_color_black))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mSurahList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> CONTENT
        }
    }

    fun updateSurahList(list: MutableList<Data>) {
        mSurahList = list
    }
}