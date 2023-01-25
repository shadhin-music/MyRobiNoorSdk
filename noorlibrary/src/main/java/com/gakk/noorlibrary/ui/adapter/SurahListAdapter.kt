package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView

import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.SurahDetailsCallBack

import com.gakk.noorlibrary.util.SurahListControl
import com.gakk.noorlibrary.util.handleClickEvent

class SurahListAdapter(surahDetailsCallBack: SurahDetailsCallBack) : RecyclerView.Adapter<SurahListAdapter.SurahListViewHolder>() {

    private val msurahDetailsCallBack:SurahDetailsCallBack
    private val viewHolderSelectionControl: SurahListViewHolderSelectionControl


    fun getViewHolderSelectionControl()=viewHolderSelectionControl



    init {
        msurahDetailsCallBack=surahDetailsCallBack
        viewHolderSelectionControl = SurahListViewHolderSelectionControl(SurahListControl.getSelectedSurahId()!!)
    }

    inner class SurahListViewHolder (layoutView: View): RecyclerView.ViewHolder(layoutView) {

        var mBinding: View= layoutView
        var laoutTag: String? = null



        fun setTag(tag: String?) {
            laoutTag?.let {
                viewHolderSelectionControl.getViewHolderMap().remove(it)
            }
            laoutTag = tag
            laoutTag?.let {
                viewHolderSelectionControl.getViewHolderMap()[it] = this
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahListViewHolder {
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.layout_surah_list_item,
            parent,
            false
        )

        return SurahListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahListViewHolder, position: Int) {
        var surah=SurahListControl.surahList?.get(position)
        val tvSurahNumber:AppCompatTextView = holder.itemView.findViewById(R.id.tvSurahNumber)
        val tvSurahName:AppCompatTextView = holder.itemView.findViewById(R.id.tvSurahName)
        tvSurahNumber.text = surah?.surahNumber
        tvSurahName.text = surah?.name
        holder.itemView.handleClickEvent {
            var selectedId=SurahListControl.surahList?.get(position)?.id
            viewHolderSelectionControl.setSelectedId(selectedId!!)
            viewHolderSelectionControl.toggleSelectionVisibilityForAll()
        }

        holder.setTag("${surah!!.id}")
        viewHolderSelectionControl.toggleSelectionVisibility(holder)
    }

    override fun getItemCount(): Int {
        return SurahListControl.surahList?.size?:0
    }
}

class SurahListViewHolderSelectionControl(id:String) {

    private var selectedId: String
    private var viewHolderMap: HashMap<String, SurahListAdapter.SurahListViewHolder?>

    init {
        selectedId = id
        viewHolderMap = HashMap()
    }

    fun setSelectedId(id: String) {
        selectedId = id
    }
    fun getSelectedId()=selectedId

    fun getViewHolderMap() = viewHolderMap

    fun toggleSelectionVisibilityForAll() {
        for (viewHolder in viewHolderMap) {
            toggleSelectionVisibility(viewHolder.value)
        }
    }

     fun toggleSelectionVisibility(holder: SurahListAdapter.SurahListViewHolder?) {
         val imgChecked: ImageView? = holder?.itemView?.findViewById(R.id.imgChecked)
        holder?.laoutTag?.let {
            when (it == selectedId) {
                true -> holder.itemView?.let { imgChecked?.visibility = VISIBLE }
                false -> holder.itemView?.let { imgChecked?.visibility = INVISIBLE }
            }
        }
    }
}