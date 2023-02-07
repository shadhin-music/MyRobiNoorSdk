package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.util.*


internal class LiteratureCategoryAdapter(
    detailsCallBack: DetailsCallBack,
    categoryList: MutableList<Data>,
    pagingViewCallBack: PagingViewCallBack,
    catId: String
) :
    RecyclerView.Adapter<LiteratureCategoryAdapter.LiteratureCategoryViewHolder>() {

    val FOOTER = 0
    val CATEGORY_DUA = 1
    val CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION = 2
    val CATEGORY_NAMAZ_RULES_COMMON = 3
    val CATEGORY_OTHER = 4


    val mCatId = catId
    val mCategoryList = categoryList
    val mDetailsCallBack = detailsCallBack
    val mPagingViewCallBack = pagingViewCallBack

    val mViewControl = ViewHolderControl()


    inner class LiteratureCategoryViewHolder
        (layoutId: Int, layoutView: View) : RecyclerView.ViewHolder(layoutView) {

        val view:View = layoutView
        val layoutTag = layoutId

        init {

            when(layoutId)
            {
                CATEGORY_DUA ->
                {
                    view.let {
                        it.resizeView(
                            ViewDimension.HalfScreenWidth,
                            mDetailsCallBack.getScreenWith(),
                            it.context
                        )

                        it.handleClickEvent {
                            val category = mCategoryList.get(adapterPosition)
                            val fragment = FragmentProvider.getFragmentByName(
                                name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                detailsActivityCallBack = mDetailsCallBack,
                                catId = category.category,
                                subCatId = category.id,
                                isFav = false,
                                /*pageTitle = category.name*/
                            )
                            //val fragment=literatureLIstFragmentProvider.getLiteratureListFragment(mLcId!!,"0",mDetailsCallBack)
                            mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                        }
                    }
                }

                CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION ->
                {
                    view.let {

                        val layoutVisualForMen = view.findViewById<CardView>(R.id.layoutVisualForMen)
                        val layoutVisualForWomen = view.findViewById<CardView>(R.id.layoutVisualForWomen)
                        val imgThumbnailWomen = layoutVisualForWomen.findViewById<ImageView>(R.id.imgThumbnail)
                        val imgThumbnailMen = layoutVisualForMen.findViewById<ImageView>(R.id.imgThumbnail)
                        val tvSubTitle = layoutVisualForWomen.findViewById<AppCompatTextView>(R.id.tvSubTitle)


                        if (mCatId == R.string.namaz_rules_cat_id.getLocalisedTextFromResId()) {
                            layoutVisualForMen.visibility = View.VISIBLE
                            layoutVisualForWomen.visibility = View.VISIBLE
                        }

                        setImageFromUrlNoProgress(imgThumbnailMen, ImageFromOnline("Drawable/ic_man_praying.webp").fullImageUrl)
                        setImageFromUrlNoProgress(imgThumbnailWomen, ImageFromOnline("Drawable/ic_women_praying.webp").fullImageUrl)
                        tvSubTitle.setText(R.string.for_women)

                        layoutVisualForMen.handleClickEvent {
                            val fragment = FragmentProvider.getFragmentByName(
                                PAGE_NAMAZ_VISUAL, detailsActivityCallBack = mDetailsCallBack,
                                catName = CAT_MEN
                            )
                            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                        }

                        layoutVisualForWomen.handleClickEvent {
                            val fragment = FragmentProvider.getFragmentByName(
                                PAGE_NAMAZ_VISUAL, detailsActivityCallBack = mDetailsCallBack,
                                catName = CAT_WOMEN
                            )
                            mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                        }
                    }
                }

                CATEGORY_NAMAZ_RULES_COMMON ->
                {
                    view.let {

                        it.handleClickEvent {
                            val category = mCategoryList.get(adapterPosition - 1)
                            val fragment = FragmentProvider.getFragmentByName(
                                name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                detailsActivityCallBack = mDetailsCallBack,
                                catId = category.category,
                                subCatId = category.id,
                                isFav = false,
                                /*pageTitle = category.name*/
                            )
                            //val fragment=literatureLIstFragmentProvider.getLiteratureListFragment(mLcId!!,"0",mDetailsCallBack)
                            mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                        }
                    }
                }


            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LiteratureCategoryViewHolder {

        lateinit var view:View


        when (viewType) {
            CATEGORY_DUA -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_literature,parent,false)

                return LiteratureCategoryViewHolder(CATEGORY_DUA,view)
            }

            CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_namaz_rules_male_female_instruction,parent,false)

                return LiteratureCategoryViewHolder(CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION,view)
            }

            CATEGORY_NAMAZ_RULES_COMMON -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_namaz_rules_common,parent,false)

                return LiteratureCategoryViewHolder(CATEGORY_NAMAZ_RULES_COMMON,view)
            }


            else -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,parent,false)

                return LiteratureCategoryViewHolder(CATEGORY_OTHER,view)
            }

        }

    }

    override fun onBindViewHolder(holder: LiteratureCategoryViewHolder, position: Int) {

        when(holder.layoutTag) {
            CATEGORY_NAMAZ_RULES_COMMON -> {
                val category = mCategoryList[position - 1]

                val imgContent = holder.view.findViewById<CircleImageView>(R.id.imgContent)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                val tvTitle = holder.view.findViewById<AppCompatTextView>(R.id.tvTitle)

                tvTitle.text = category.name

                setImageFromUrl(imgContent, category.fullImageUrl, progressBar, PLACE_HOLDER_1_1)


            }

            CATEGORY_DUA -> {

                val category = mCategoryList.get(position)

                val imgContent = holder.view.findViewById<CircleImageView>(R.id.imgContent)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                val tvTitle = holder.view.findViewById<AppCompatTextView>(R.id.tvTitle)
                val tvCount = holder.view.findViewById<AppCompatTextView>(R.id.tvCount)

                setImageFromUrl(imgContent,category.fullImageUrl,progressBar,PLACE_HOLDER_1_1)
                tvTitle.text = category.name
                tvCount.text = category.duaCountFormatted
            }

            CATEGORY_OTHER ->
            {
                when (mPagingViewCallBack.hasMoreData()) {
                    true -> mPagingViewCallBack.loadNextPage()
                    false -> {
                        holder.view.visibility = GONE
                        holder.view.layoutParams.height = 0
                    }
                }
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return mViewControl.getItemViewType(mCatId, position, mCategoryList)
    }

    override fun getItemCount(): Int {
        return mViewControl.getItemCount(mCatId)
    }

    fun addItemToList(list: MutableList<Data>?) {
        list?.let {
            var startPos = mCategoryList!!.size
            mCategoryList?.addAll(list)
            notifyItemRangeChanged(startPos, list.size)
        }
    }

    fun hideFooter() {
        mCategoryList?.let {
            when (mCatId) {
                R.string.namaz_rules_cat_id.getLocalisedTextFromResId() -> {
                    notifyItemChanged(it.size + 1)
                    Log.e("HIDE_FOOTER", " nRule pos ${it.size + 1}")
                }
                else -> {
                    notifyItemChanged(it.size)
                    Log.e("HIDE_FOOTER", "other pos ${it.size}")
                }
            }

        }
    }


    inner class ViewHolderControl {

        fun getItemCount(catId: String): Int {
            when (catId) {
                R.string.namaz_rules_cat_id.getLocalisedTextFromResId() -> {
                    return mCategoryList.size + 2//one extra view for Male/Female Namaz Rules view
                }

                //DUA_CAT_ID,WALL_PAPER_CAT_ID,ANIMATION_CAT_ID
                else -> {
                    return mCategoryList.size + 1
                }
            }
        }

        fun getItemViewType(catId: String, position: Int, list: MutableList<Data>?): Int {
            if (list == null)
                return NO_DATA
            when (catId) {
                R.string.namaz_rules_cat_id.getLocalisedTextFromResId() -> {
                    if (position == 0) {
                        return CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION
                    }
                    if (position < mCategoryList.size + 1) {
                        return CATEGORY_NAMAZ_RULES_COMMON
                    }

                    return FOOTER
                }
                R.string.wallpaper_cat_id.getLocalisedTextFromResId(), R.string.animation_cat_id.getLocalisedTextFromResId() -> {
                    when (position < mCategoryList.size) {
                        true -> return DOWNLOADABLE
                        false -> return FOOTER
                    }
                }

                //DUA_CAT_ID
                else -> {
                    when (position < mCategoryList.size) {
                        true -> return CATEGORY_DUA
                        false -> return FOOTER
                    }
                }
            }
        }
    }

}

