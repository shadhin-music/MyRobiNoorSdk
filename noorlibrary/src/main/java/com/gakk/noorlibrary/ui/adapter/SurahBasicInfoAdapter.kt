package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.databinding.LayoutFavouriteSurahBasicInformationBinding
import com.gakk.noorlibrary.databinding.LayoutFooterBinding
import com.gakk.noorlibrary.databinding.LayoutGeneralSurahBasicInformationBinding
import com.gakk.noorlibrary.databinding.LayoutNoDataBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.fragments.FavUnFavActionCallBack
import com.gakk.noorlibrary.util.FAVOURITE_SURAH
import com.gakk.noorlibrary.util.handleClickEvent

class SurahBasicInfoAdapter(
    surahList: MutableList<Data>?,
    val surahType: String,
    action: ((String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment?)?,
    detailsCallBack: DetailsCallBack,
    pagingViewCallBack: PagingViewCallBack,
    favUnFavActionCallBack: FavUnFavActionCallBack
) :
    RecyclerView.Adapter<SurahBasicInfoAdapter.SurahBasicInfoViewHolder>() {

    val CELL_GENERAL_SURAH = 0
    val CELL_FAVOURITE_SURAH = 1
    val CELL_FOOTER = 2
    val CELL_NO_CONTENT = 3

    val mAction = action
    val mDetailsCallBack = detailsCallBack
    var mSurahList = surahList
    val mPagingViewCallBack = pagingViewCallBack
    val mFavUnFavActionCallBack = favUnFavActionCallBack


    var viewControl: ViewControl = ViewControl()

    inner class SurahBasicInfoViewHolder : RecyclerView.ViewHolder {

        var gerenalSurahBinding: LayoutGeneralSurahBasicInformationBinding? = null

        constructor(binding: LayoutGeneralSurahBasicInformationBinding) : super(binding.root) {
            gerenalSurahBinding = binding
            gerenalSurahBinding?.root?.let {

                it.handleClickEvent {
                    Log.i("AdapterPos", "$adapterPosition")
                    if (adapterPosition != -1) {
                        mSurahList?.let {
                            var id = it.get(adapterPosition).id
                            val fragment = mAction?.invoke(id!!, mDetailsCallBack, it)
                            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                        }
                    }

                }
            }

        }

        var favouriteSurahBinding: LayoutFavouriteSurahBasicInformationBinding? = null

        constructor(binding: LayoutFavouriteSurahBasicInformationBinding) : super(binding.root) {
            favouriteSurahBinding = binding
            favouriteSurahBinding?.root?.let {

                it.handleClickEvent {
                    Log.i("AdapterPos", "$adapterPosition")
                    if (bindingAdapterPosition != -1) {
                        mSurahList?.let {
                            var id = it.get(adapterPosition).id
                            val fragment = mAction?.invoke(id!!, mDetailsCallBack, it)
                            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                        }
                    }
                }
            }


            favouriteSurahBinding?.favourite?.let {

                it.handleClickEvent {
                    var id = mSurahList?.get(adapterPosition)!!.id
                    mFavUnFavActionCallBack.unFavSurah(id!!, adapterPosition)
                }
            }
        }

        var noItemBinding: LayoutNoDataBinding? = null

        constructor(binding: LayoutNoDataBinding) : super(binding.root) {
            noItemBinding = binding
        }

        var footerBinding: LayoutFooterBinding? = null

        constructor(binding: LayoutFooterBinding) : super(binding.root) {
            footerBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahBasicInfoViewHolder {
        var binding: ViewBinding


        return when (viewType) {
            CELL_GENERAL_SURAH -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_general_surah_basic_information,
                    parent,
                    false
                )
                SurahBasicInfoViewHolder(binding as LayoutGeneralSurahBasicInformationBinding)
            }

            CELL_NO_CONTENT -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_no_data,
                    parent,
                    false
                )
                SurahBasicInfoViewHolder(binding as LayoutNoDataBinding)
            }

            CELL_FOOTER -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_footer,
                    parent,
                    false
                )
                SurahBasicInfoViewHolder(binding as LayoutFooterBinding)
            }
            else -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_favourite_surah_basic_information,
                    parent,
                    false
                )
                SurahBasicInfoViewHolder(binding as LayoutFavouriteSurahBasicInformationBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: SurahBasicInfoViewHolder, position: Int) {
        holder.gerenalSurahBinding?.let {
            mSurahList?.let { list ->
                it.surah = list.get(position)
                holder.gerenalSurahBinding?.number?.text = list[position].surahNumber
            }

        }

        holder.favouriteSurahBinding?.let {
            mSurahList?.let { list ->
                it.surah = list.get(position)
            }

        }

        holder.footerBinding?.let {
            when (mPagingViewCallBack.hasMoreData()) {
                true -> mPagingViewCallBack.loadNextPage()
                else -> it.root.visibility = GONE
            }
        }

        holder.noItemBinding?.let {
            it.item = ImageFromOnline("bg_no_data.png")
        }
    }

    fun addItemToList(list: MutableList<Data>) {
        var startPos = mSurahList!!.size
        mSurahList?.addAll(list)
        notifyItemRangeChanged(startPos, list.size)
    }

    fun hideFooter() {
        notifyItemChanged(mSurahList!!.size)
    }

    fun insertItemToList(data: Data) {
        mSurahList?.let {
            it.add(0, data)
            notifyItemInserted(0)
        }
        if (mSurahList == null) {
            mSurahList = mutableListOf()
            mSurahList?.add(0, data)
            notifyItemInserted(0)
        }
    }

    fun removeItemFromList(index: Int) {
        mSurahList!!.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeItemFromListIfExists(data: Data) {
        var index = getIndexOfGivenSurah(data)
        if (index != -1) {
            mSurahList!!.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getIndexOfGivenSurah(data: Data): Int {
        mSurahList?.let {
            for (i in it.indices) {
                if (data.id == it[i].id)
                    return i
            }
        }
        return -1
    }

    override fun getItemViewType(position: Int) = viewControl.getViewType(position)


    override fun getItemCount() = viewControl.getItemCount()

    /* Determines View type & view count of this adapter*/
    inner class ViewControl {

        fun getItemCount(): Int {
            when (mSurahList) {
                null -> return 1
                else -> {
                    return mSurahList!!.size + 1
                }
            }
        }

        fun getViewType(position: Int): Int {
            when {
                mSurahList == null || mSurahList!!.size == 0 -> return CELL_NO_CONTENT
                else -> {
                    if (position == mSurahList!!.size)
                        return CELL_FOOTER
                    else {
                        return when (surahType) {
                            FAVOURITE_SURAH -> CELL_FAVOURITE_SURAH
                            else -> CELL_GENERAL_SURAH
                        }
                    }
                }
            }
        }
    }
}