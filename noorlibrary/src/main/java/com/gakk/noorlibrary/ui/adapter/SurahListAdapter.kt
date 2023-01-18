package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.SurahDetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutSurahListItemBinding
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

    inner class SurahListViewHolder : RecyclerView.ViewHolder {

        var mBinding: LayoutSurahListItemBinding? = null
        var laoutTag: String? = null

        constructor(binding: LayoutSurahListItemBinding) : super(binding.root) {
            mBinding = binding
            mBinding?.root?.let {
                it.handleClickEvent {
                    var selectedId=SurahListControl.surahList?.get(adapterPosition)?.id
                    viewHolderSelectionControl.setSelectedId(selectedId!!)
                    viewHolderSelectionControl.toggleSelectionVisibilityForAll()
                }
            }
        }

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
        val binding: LayoutSurahListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_surah_list_item,
            parent,
            false
        )

        return SurahListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahListViewHolder, position: Int) {
        var surah=SurahListControl.surahList?.get(position)
        holder.mBinding?.surah=surah
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
        holder?.laoutTag?.let {
            when (it == selectedId) {
                true -> holder.mBinding?.let { it.imgChecked.visibility = VISIBLE }
                false -> holder.mBinding?.let { it.imgChecked.visibility = INVISIBLE }
            }
        }
    }
}