package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.FavUnFavCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature

import com.gakk.noorlibrary.ui.fragments.LiteratureItemClickCallBack
import com.gakk.noorlibrary.util.*


const val NO_DATA = 0
const val LITERATURE = 1
const val FOOTER = 2
val DOWNLOADABLE = 3
const val JAKAT_HEADER = 4
const val JAKAT_QUERY = 5
const val OTHER = 6

internal class LiteratureListAdapter(
    list: MutableList<Literature>?,
    isFavList: Boolean,
    detailsCallBack: DetailsCallBack? = null,
    pagingViewCallBack: PagingViewCallBack,
    favCallBack: FavUnFavCallBack?,
    itemClickCallBack: LiteratureItemClickCallBack?
) : RecyclerView.Adapter<LiteratureListAdapter.LiteratureViewHolder>() {

    val mDetailsCallBack = detailsCallBack
    val mPagingViewCallBack = pagingViewCallBack
    val mItemClickCallBack = itemClickCallBack

    val mFavCallBack = favCallBack
    val mIsFavList = isFavList
    var mList = list
    private val actionImageControl = LiteratureListLayoutActionImageControl()
    private val viewTypeControl = LiteratureListItemViewTypeControl()
    private val itemCountControl = LiteratureItemCountControl()

    var catId: String = list?.get(0)?.category ?: "-1"


    fun getLiteratureList(): MutableList<Literature> {
        return mList ?: mutableListOf()
    }

    inner class LiteratureViewHolder(layoutId: Int,layoutView: View) :
        RecyclerView.ViewHolder(layoutView) {

         val view:View = layoutView

        val layoutTag = layoutId


        fun toggleJakatDescriptionVisibility(view: View, binding: View) {
            view.handleClickEvent {

                val tvDesc = binding.findViewById<AppCompatTextView>(R.id.tvDesc)
                val btnToggleCollapse = binding.findViewById<AppCompatImageView>(R.id.btnToggleCollapse)


                when (tvDesc.visibility) {

                    VISIBLE -> {
                        btnToggleCollapse.setImageResource(R.drawable.ic_plus)
                        tvDesc.visibility = GONE
                    }
                    GONE -> {
                        btnToggleCollapse.setImageResource(R.drawable.ic_minus)
                        tvDesc.visibility = VISIBLE
                    }
                    INVISIBLE -> Unit
                }
            }
        }

        init {

            when(layoutId)
            {
                OTHER ->
                {

                    view.apply {

                        actionImageControl.setLayoutActionImage(this, mIsFavList)

                        this.findViewById<ImageView>(R.id.imgAction).handleClickEvent {
                            val literature = mList?.get(bindingAdapterPosition)
                            when (mIsFavList) {
                                true -> {
                                    var lCId = literature?.category!!
                                    var lscId = literature.subcategory ?: ""
                                    var id = literature.id!!
                                    var pos = adapterPosition
                                    mFavCallBack?.performFavOrUnFavAction(lCId, lscId, id, pos, false)
                                }
                                false -> {
                                    mItemClickCallBack?.goToListeratureDetailsFragment(
                                        adapterPosition,
                                        mIsFavList
                                    )
                                }
                            }
                        }
                        view.handleClickEvent {
                            mItemClickCallBack?.goToListeratureDetailsFragment(adapterPosition, mIsFavList)
                        }

                    }
                }

                DOWNLOADABLE ->
                {

                    view.handleClickEvent {
                        mItemClickCallBack?.goToListeratureDetailsFragment(adapterPosition, false)
                    }

                    view.let {
                        it.resizeView(
                            ViewDimension.HalfScreenWidth,
                            mDetailsCallBack?.getScreenWith(),
                            it.context
                        )
                    }
                }


                JAKAT_HEADER ->
                {

                    view.apply {

                        val item = ImageFromOnline("ic_zakat_img.png")
                        val imgJakatHeader = this.findViewById<AppCompatImageView>(R.id.imgJakatHeader)
                        val progressBar = this.findViewById<ProgressBar>(R.id.progressBar)

                        setImageFromUrl(imgJakatHeader,item.fullImageUrl,progressBar)
                    }


                }

                JAKAT_QUERY ->
                {


                    view.apply {

                        toggleJakatDescriptionVisibility(this,this)

                        val btnToggleCollapse = this.findViewById<AppCompatImageView>(R.id.btnToggleCollapse)

                        btnToggleCollapse?.let {
                            toggleJakatDescriptionVisibility(it,this)

                        }
                    }

                }

            }
        }
    }

    fun getSerialNumber(number: Int): String {
        return if (number < 10) {
            "0$number"
        } else number.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiteratureViewHolder {

        lateinit var view:View

        when (viewType) {
            NO_DATA -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_data,parent,false)

                return LiteratureViewHolder(NO_DATA, view)
            }
            DOWNLOADABLE -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_downloadable,parent,false)

                return LiteratureViewHolder(DOWNLOADABLE, view)
            }
            FOOTER -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,parent,false)


                return LiteratureViewHolder(FOOTER, view)
            }


            JAKAT_HEADER -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_jakt_header,parent,false)

                return LiteratureViewHolder(JAKAT_HEADER, view)
            }
            JAKAT_QUERY -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_jakat_query,parent,false)

                return LiteratureViewHolder(JAKAT_QUERY, view)
            }
            //common literature Layout binding
            else -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_literature,parent,false)

                return LiteratureViewHolder(OTHER, view)
            }
        }

    }

    override fun onBindViewHolder(holder: LiteratureViewHolder, position: Int) {


        when(holder.layoutTag)
        {
            NO_DATA ->
            {
                NoDataLayout(holder.view)
            }

            OTHER ->
            {
                val tvTitle = holder.view.findViewById<AppCompatTextView>(R.id.tvTitle)
                val literature = mList?.get(position)
                tvTitle?.text = literature?.title

            }

            DOWNLOADABLE ->
            {

                val imgThumb = holder.view.findViewById<ImageView>(R.id.imgThumb)
                val downloadable = mList?.get(position)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                if (progressBar != null) {
                    setImageFromUrl(imgThumb,downloadable?.fullImageUrl,progressBar,PLACE_HOLDER_2_3)
                }

            }

            FOOTER ->
            {
                when (mPagingViewCallBack.hasMoreData()) {
                    true -> mPagingViewCallBack.loadNextPage()
                    false -> {
                        holder.view.layoutParams?.height = 0
                        holder.view.visibility = GONE
                    }
                }
            }

            JAKAT_QUERY ->
            {
                mList?.let {

                    val tvTitle = holder.view.findViewById<AppCompatTextView>(R.id.tvTitle)
                    val tvDesc = holder.view.findViewById<AppCompatTextView>(R.id.tvDesc)
                    val query = it[position - 1]
                    tvTitle?.text = query.title
                    tvDesc?.text = query.text
                    val number = holder.view.findViewById<AppCompatTextView>(R.id.number)
                    number?.text =
                        TimeFormtter.getNumberByLocale(getSerialNumber(position))
                }
            }
        }


    }


    override fun getItemCount(): Int {
        return itemCountControl.getItemCount(mList)
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeControl.getViewType(mList, position, catId)
    }

    fun addLiteratureToList(list: MutableList<Literature>?) {
        when (catId) {
            R.string.jakat_cat_id.getLocalisedTextFromResId() -> {
                list?.let {
                    var startPos = mList!!.size + 1
                    mList?.addAll(list)
                    notifyItemRangeChanged(startPos, list.size)
                }
            }
            else -> {
                list?.let {
                    var startPos = mList!!.size
                    mList?.addAll(list)
                    notifyItemRangeChanged(startPos, list.size)
                }
            }
        }

    }

    fun hideFooter() {
        if (mList == null) {
            notifyItemChanged(0)
        }
        when (catId) {
            R.string.jakat_cat_id.getLocalisedTextFromResId() -> {
                mList?.let {
                    notifyItemChanged(it.size + 1)
                }
            }
            else -> {
                mList?.let {
                    notifyItemChanged(it.size)
                }
            }
        }

    }

    fun removeItemFromListIfExists(literature: Literature) {
        var pos = -1
        mList?.let {
            for (i in 0..it.size - 1) {
                if (literature.id == it.get(i).id) {
                    pos = i
                    break
                }
            }

        }
        if (pos != -1) {
            removeItemAtPosition(pos)
        }
    }

    fun removeItemAtPosition(position: Int) {
        mList?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList?.size ?: 0)
    }

    fun addItemToList(literature: Literature) {
        if (mList == null) {
            mList = mutableListOf()
        }
        mList?.let {
            it.add(literature)
            notifyItemInserted(it.size - 1)
        }
    }

}

private class LiteratureListLayoutActionImageControl() {

    fun setLayoutActionImage(binding: View, isFavList: Boolean) {
        when (isFavList) {
            true -> {
                binding.findViewById<ImageView>(R.id.imgAction).setImageResource(R.drawable.ic_favorite_filled)
            }
            false -> {
                binding.findViewById<ImageView>(R.id.imgAction).setImageResource(R.drawable.ic_chevron_right)
            }
        }
    }


}

private class LiteratureListItemViewTypeControl() {
    fun getViewType(list: MutableList<Literature>?, position: Int, catId: String): Int {
        var viewType = 0

        var size = list?.size ?: 0

        if (size == 0)
            return NO_DATA

        when (catId) {
            R.string.jakat_cat_id.getLocalisedTextFromResId() -> {

                when (position) {
                    size + 1 -> viewType = FOOTER
                    0 -> viewType = JAKAT_HEADER
                    else -> viewType = JAKAT_QUERY
                }
            }
            else -> {
                when (position) {
                    size -> viewType = FOOTER
                    else -> {
                        when (catId) {
                            R.string.animation_cat_id.getLocalisedTextFromResId(), R.string.wallpaper_cat_id.getLocalisedTextFromResId() -> viewType =
                                DOWNLOADABLE
                            else -> viewType = LITERATURE
                        }
                    }
                }
            }


        }

        return viewType
    }


}

private class LiteratureItemCountControl() {
    fun getItemCount(list: MutableList<Literature>?): Int {
        var size = 0
        list?.let {
            size = it.size
        }
        when (size == 0) {
            true -> return 1
            else -> {
                when (list?.get(0)?.category) {

                    R.string.jakat_cat_id.getLocalisedTextFromResId() -> return size + 2
                    else -> return size + 1
                }
            }
        }
    }
}