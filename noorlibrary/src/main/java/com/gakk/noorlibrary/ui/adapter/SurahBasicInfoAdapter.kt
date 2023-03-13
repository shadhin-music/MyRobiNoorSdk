package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.fragments.FavUnFavActionCallBack
import com.gakk.noorlibrary.util.FAVOURITE_SURAH
import com.gakk.noorlibrary.util.NoDataLayout
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.views.TextViewNormalArabic

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
    val CELL_OTHER = 4

    val mAction = action
    val mDetailsCallBack = detailsCallBack
    var mSurahList = surahList
    val mPagingViewCallBack = pagingViewCallBack
    val mFavUnFavActionCallBack = favUnFavActionCallBack

    var viewControl: ViewControl = ViewControl()

    inner class SurahBasicInfoViewHolder(layoutId: Int, layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {

         var view: View = layoutView

        val layoutTag = layoutId

        init {

            when(layoutId)
            {
                CELL_GENERAL_SURAH ->
                {

                    view.let {

                        it.handleClickEvent {
                            Log.i("AdapterPos", "$adapterPosition")
                            if (absoluteAdapterPosition != -1) {
                                mSurahList?.let {
                                    var id = it.get(absoluteAdapterPosition).id
                                    val fragment = mAction?.invoke(id!!, mDetailsCallBack, it)
                                    mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                                }
                            }

                        }
                    }
                }


                CELL_OTHER ->
                {

                    view.let {

                        it.handleClickEvent {
                            if(absoluteAdapterPosition!=-1){
                                mSurahList?.let {
                                    var id=it.get(absoluteAdapterPosition).id
                                    val fragment=mAction?.invoke(id!!, mDetailsCallBack, it)
                                    mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                                }
                            }
                        }
                    }

                    val favourite = view.findViewById<ImageView>(R.id.favourite)

                    favourite.handleClickEvent {

                        mSurahList?.let {
                            var id= it[absoluteAdapterPosition].id
                            mFavUnFavActionCallBack.unFavSurah(id!!, absoluteAdapterPosition)
                            // Log.e("SURAH FAV",id)
                        }


                    }

                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahBasicInfoViewHolder {

        lateinit var view: View


        return when (viewType) {
            CELL_GENERAL_SURAH -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_general_surah_basic_information,parent,false)
                SurahBasicInfoViewHolder(CELL_GENERAL_SURAH,view)
            }

            CELL_NO_CONTENT -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_data,parent,false)


                SurahBasicInfoViewHolder(CELL_NO_CONTENT,view)
            }

            CELL_FOOTER -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,parent,false)

                SurahBasicInfoViewHolder(CELL_FOOTER,view)
            }
            else -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_favourite_surah_basic_information,parent,false)

                SurahBasicInfoViewHolder(CELL_OTHER,view)
            }
        }
    }

    override fun onBindViewHolder(holder: SurahBasicInfoViewHolder, position: Int) {

        when(holder.layoutTag)
        {
            CELL_GENERAL_SURAH ->
            {
                mSurahList?.let { list ->
                    val number = holder.view.findViewById<AppCompatTextView>(R.id.number)
                    val title = holder.view.findViewById<AppCompatTextView>(R.id.title)
                    val nameAyasLoc = holder.view.findViewById<AppCompatTextView>(R.id.nameAyasLoc)
                    val textArabic = holder.view.findViewById<TextViewNormalArabic>(R.id.textArabic)
                    number?.text = list[position].surahNumber
                    title?.text = list[position].name
                    nameAyasLoc?.text = list[position].surahBasicInfo
                    textArabic?.text = list[position].nameInArabic

                }

            }

            CELL_OTHER ->
            {
                mSurahList?.let { list ->
                    val number = holder.view.findViewById<AppCompatTextView>(R.id.number)
                    val title = holder.view.findViewById<AppCompatTextView>(R.id.title)

                    number?.text = list[position].surahNumber
                    title?.text = list[position].name
                }
            }

            CELL_FOOTER ->
            {
                when (mPagingViewCallBack.hasMoreData()) {
                    true -> mPagingViewCallBack.loadNextPage()
                    else -> holder.view.visibility = GONE
                }
            }

            CELL_NO_CONTENT ->
            {
                NoDataLayout(holder.view)
            }
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