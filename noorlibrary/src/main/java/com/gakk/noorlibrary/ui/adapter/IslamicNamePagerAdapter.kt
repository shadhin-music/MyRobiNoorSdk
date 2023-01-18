package com.gakk.noorlibrary.ui.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.fragments.islamicName.IslamicNameListFragment
import com.gakk.noorlibrary.util.FAVOURITE_NAME
import com.gakk.noorlibrary.util.NAME_LIST

internal class IslamicNamePagerAdapter(
    fragmentManager: FragmentManager,
    pageTitles: Array<String>,
    detailsCallBack: DetailsCallBack?,
    private val showFemaleNameList: Boolean
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    var mPageTitles = pageTitles
    var mDetailsCallBack = detailsCallBack

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        val pageType = when (position) {
            0 -> NAME_LIST
            else -> FAVOURITE_NAME
        }
        return IslamicNameListFragment.newInstance(
            pageType,
            mDetailsCallBack, showFemaleNameList
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "     ${mPageTitles[position]}     "
    }

}