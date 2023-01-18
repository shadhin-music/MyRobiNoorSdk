package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.databinding.LayoutCategoryLiteratureBinding
import com.gakk.noorlibrary.databinding.LayoutCategoryNamazRulesCommonBinding
import com.gakk.noorlibrary.databinding.LayoutCategoryNamazRulesMaleFemaleInstructionBinding
import com.gakk.noorlibrary.databinding.LayoutFooterBinding
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


    val mCatId = catId
    val mCategoryList = categoryList
    val mDetailsCallBack = detailsCallBack
    val mPagingViewCallBack = pagingViewCallBack

    val mViewControl = ViewHolderControl()


    inner class LiteratureCategoryViewHolder : RecyclerView.ViewHolder {

        var duaCategoryBining: LayoutCategoryLiteratureBinding? = null

        constructor(binding: LayoutCategoryLiteratureBinding) : super(binding.root) {
            duaCategoryBining = binding
            duaCategoryBining?.root?.let {
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

        var categoryNamazRulesMaleFemaleInstructionBinding: LayoutCategoryNamazRulesMaleFemaleInstructionBinding? =
            null

        constructor(binding: LayoutCategoryNamazRulesMaleFemaleInstructionBinding) : super(binding.root) {
            categoryNamazRulesMaleFemaleInstructionBinding = binding
            categoryNamazRulesMaleFemaleInstructionBinding?.let {

                if (mCatId.equals(R.string.namaz_rules_cat_id.getLocalisedTextFromResId())) {
                    it.layoutVisualForMen.root.visibility = View.VISIBLE
                    it.layoutVisualForWomen.root.visibility = View.VISIBLE
                }


                it.layoutVisualForWomen.imgThumbnail.setImageResource(R.drawable.ic_women_praying)
                it.layoutVisualForWomen.tvSubTitle.setText(R.string.for_women)

                it.layoutVisualForMen.root.handleClickEvent {
                    val fragment = FragmentProvider.getFragmentByName(
                        PAGE_NAMAZ_VISUAL, detailsActivityCallBack = mDetailsCallBack,
                        catName = CAT_MEN
                    )
                    mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                }

                it.layoutVisualForWomen.root.handleClickEvent {
                    val fragment = FragmentProvider.getFragmentByName(
                        PAGE_NAMAZ_VISUAL, detailsActivityCallBack = mDetailsCallBack,
                        catName = CAT_WOMEN
                    )
                    mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                }
            }
        }

        var categoryNamazRulesCommonBinding: LayoutCategoryNamazRulesCommonBinding? = null

        constructor(binding: LayoutCategoryNamazRulesCommonBinding) : super(binding.root) {
            categoryNamazRulesCommonBinding = binding
            categoryNamazRulesCommonBinding?.root?.let {

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


        var footerBinding: LayoutFooterBinding? = null

        constructor(binding: LayoutFooterBinding) : super(binding.root) {
            footerBinding = binding
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LiteratureCategoryViewHolder {

        when (viewType) {
            CATEGORY_DUA -> {
                val binding: LayoutCategoryLiteratureBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_category_literature,
                    parent,
                    false
                )
                return LiteratureCategoryViewHolder(binding)
            }

            CATEGORY_NAMAZ_RULES_MALE_FEMALE_INSTRUCTION -> {
                val binding: LayoutCategoryNamazRulesMaleFemaleInstructionBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_category_namaz_rules_male_female_instruction,
                        parent,
                        false
                    )
                return LiteratureCategoryViewHolder(binding)
            }

            CATEGORY_NAMAZ_RULES_COMMON -> {
                val binding: LayoutCategoryNamazRulesCommonBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_category_namaz_rules_common,
                    parent,
                    false
                )
                return LiteratureCategoryViewHolder(binding)
            }


            else -> {
                val binding: LayoutFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_footer,
                    parent,
                    false
                )
                return LiteratureCategoryViewHolder(binding)
            }

        }

    }

    override fun onBindViewHolder(holder: LiteratureCategoryViewHolder, position: Int) {


        holder.categoryNamazRulesCommonBinding?.let {
            it.category = mCategoryList.get(position - 1)
        }
        holder.duaCategoryBining?.let {
            it.category = mCategoryList.get(position)
        }



        holder.footerBinding?.let {
            Log.e("HIDE_FOOTER", " Footer pos $position")
            when (mPagingViewCallBack.hasMoreData()) {
                true -> mPagingViewCallBack.loadNextPage()
                false -> {
                    it.root.visibility = GONE
                    it.root.layoutParams.height = 0
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
                R.string.namaz_rules_cat_id.getLocalisedTextFromResId(), R.string.event_cateogry_id.getLocalisedTextFromResId() -> {
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

