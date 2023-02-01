package com.gakk.noorlibrary.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.fragments.LiteratureCategoryListFragment
import com.gakk.noorlibrary.ui.fragments.LiteratureListFragment
import com.gakk.noorlibrary.ui.fragments.zakat.ZakatListFragment
import com.gakk.noorlibrary.util.LiteratureType


internal class LiteratureListFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    pageTitles: Array<String>,
    detailsCallBack: DetailsCallBack? = null,
    catId: String,
    literatureType: LiteratureType
) : FragmentPagerAdapter(
    fragmentManager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    val mPageTitles = pageTitles
    val mDetailsCallBack = detailsCallBack
    val mCatId = catId
    val mLiteratureType = literatureType

    override fun getCount() = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                when (mLiteratureType) {
                    LiteratureType.Hadis -> LiteratureListFragment.newInstance(
                        false,
                        catId = mCatId,
                        subCatId = "undefined"
                    )
                    LiteratureType.Jakat -> LiteratureListFragment.newInstance(
                        false,
                        catId = mCatId,
                        subCatId = "undefined",
                        showCalculateBtn = true
                    )
                    else -> LiteratureCategoryListFragment.newInstance(
                        literatureType = mLiteratureType,
                        catId = mCatId
                    )
                }

            }
            else -> {
                when (mLiteratureType) {
                    LiteratureType.Jakat -> {
                        ZakatListFragment.newInstance()

                    }
                    else -> {
                        LiteratureListFragment.newInstance(
                            true,
                            catId = mCatId,
                            subCatId = "undefined"
                        )
                    }
                }

            }
        }
    }

    override fun getPageTitle(position: Int) = "     ${mPageTitles[position]}     "
}


