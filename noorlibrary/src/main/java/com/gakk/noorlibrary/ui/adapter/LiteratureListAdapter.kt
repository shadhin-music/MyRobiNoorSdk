package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.FavUnFavCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.*
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

    inner class LiteratureViewHolder : RecyclerView.ViewHolder {

        var literatureBinding: LayoutLiteratureBinding? = null

        constructor(binding: LayoutLiteratureBinding) : super(binding.root) {
            literatureBinding = binding
            literatureBinding?.let {
                actionImageControl.setLayoutActionImage(it, mIsFavList)

                it.imgAction.handleClickEvent {
                    val literature = mList?.get(bindingAdapterPosition)
                    when (mIsFavList) {
                        true -> {
                            var lCId = literature?.category!!
                            var lscId = literature?.subcategory ?: ""
                            var id = literature?.id!!
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
                it.root.handleClickEvent {
                    mItemClickCallBack?.goToListeratureDetailsFragment(adapterPosition, mIsFavList)
                }

            }
        }

        var noDataBinding: LayoutNoDataBinding? = null

        constructor(binding: LayoutNoDataBinding) : super(binding.root) {
            noDataBinding = binding
        }


        var downloadTag: String? = null
        fun setTag(tag: String, binding: LayoutDownloadableBinding) {
            if (downloadTag != null) {
                DownloadProgressControl.removeLayoutFromMap(downloadTag!!, binding)
            }
            downloadTag = tag
            DownloadProgressControl.addLayoutToMapAndUpdate(downloadTag!!, binding)

        }

        var categoryDownloadableBinding: LayoutDownloadableBinding? = null

        constructor(binding: LayoutDownloadableBinding) : super(binding.root) {
            categoryDownloadableBinding = binding


            categoryDownloadableBinding?.root?.handleClickEvent {
                mItemClickCallBack?.goToListeratureDetailsFragment(adapterPosition, false)
            }

            categoryDownloadableBinding?.root?.let {
                it.resizeView(
                    ViewDimension.HalfScreenWidth,
                    mDetailsCallBack?.getScreenWith(),
                    it.context
                )
            }
        }


        var footerBinding: LayoutFooterBinding? = null

        constructor(binding: LayoutFooterBinding) : super(binding.root) {
            footerBinding = binding
        }

        var jakatHeadrBinding: LayoutJaktHeaderBinding? = null

        constructor(binding: LayoutJaktHeaderBinding) : super(binding.root) {
            jakatHeadrBinding = binding

            jakatHeadrBinding?.item = ImageFromOnline("ic_zakat_img.png")
        }

        var jakatQueryBinding: LayoutJakatQueryBinding? = null

        constructor(binding: LayoutJakatQueryBinding) : super(binding.root) {
            jakatQueryBinding = binding
            jakatQueryBinding?.let { binding ->

                toggleJakatDescriptionVisibility(binding.root, binding)

                binding.btnToggleCollapse?.let {
                    toggleJakatDescriptionVisibility(it, binding)

                }
            }

        }

        fun toggleJakatDescriptionVisibility(view: View, binding: LayoutJakatQueryBinding) {
            view.handleClickEvent {
                when (binding.tvDesc.visibility) {
                    VISIBLE -> {
                        binding.btnToggleCollapse.setImageResource(R.drawable.ic_plus)
                        binding.tvDesc.visibility = GONE
                    }
                    GONE -> {
                        binding.btnToggleCollapse.setImageResource(R.drawable.ic_minus)
                        binding.tvDesc.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun getSerialNumber(number: Int): String? {
        return if (number < 10) {
            "0$number"
        } else number.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiteratureViewHolder {

        when (viewType) {
            NO_DATA -> {
                val binding: LayoutNoDataBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_no_data,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }
            DOWNLOADABLE -> {
                val binding: LayoutDownloadableBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_downloadable,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }
            FOOTER -> {
                val binding: LayoutFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_footer,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }


            JAKAT_HEADER -> {
                val binding: LayoutJaktHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_jakt_header,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }
            JAKAT_QUERY -> {
                val binding: LayoutJakatQueryBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_jakat_query,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }
            //common literature Layout binding
            else -> {
                val binding: LayoutLiteratureBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_literature,
                    parent,
                    false
                )
                return LiteratureViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: LiteratureViewHolder, position: Int) {

        holder.noDataBinding?.let {
            it.item = ImageFromOnline("bg_no_data.png")
        }

        holder.literatureBinding?.let {
            it.literature = mList?.get(position)
        }

        holder.categoryDownloadableBinding?.let {
            it.downloadable = mList?.get(position)
            holder.setTag(mList?.get(position)?.id!!, it)
        }

        holder.footerBinding?.let {
            when (mPagingViewCallBack.hasMoreData()) {
                true -> mPagingViewCallBack.loadNextPage()
                false -> {
                    it.root.layoutParams.height = 0
                    it.root.visibility = GONE
                }
            }
        }

        holder.jakatQueryBinding?.let { binding ->
            mList?.let {
                binding.query = it.get(position - 1)
                binding.number.text =
                    TimeFormtter.getNumberByLocale(getSerialNumber(position).toString())
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

    fun setLayoutActionImage(binding: LayoutLiteratureBinding, isFavList: Boolean) {
        when (isFavList) {
            true -> {
                binding.imgAction.setImageResource(R.drawable.ic_favorite_filled)
            }
            false -> {
                binding.imgAction.setImageResource(R.drawable.ic_chevron_right)
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