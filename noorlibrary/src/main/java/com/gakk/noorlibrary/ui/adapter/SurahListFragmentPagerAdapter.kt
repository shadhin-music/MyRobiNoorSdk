package com.gakk.noorlibrary.ui.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.fragments.SurahListFragment
import com.gakk.noorlibrary.util.ALL_SURAH
import com.gakk.noorlibrary.util.FAVOURITE_SURAH

internal class SurahListFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    pageTitles: Array<String>,
    detailsCallBack: DetailsCallBack,
    actionGetSurahDetailFragment: (String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment?
) : FragmentPagerAdapter(
    fragmentManager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    var mPageTitles = pageTitles
    var mDetailsCallBack = detailsCallBack
    val mActionGetSurahDetailFragment = actionGetSurahDetailFragment

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        var pageType = when (position) {
            0 -> ALL_SURAH
            else -> FAVOURITE_SURAH
        }
        return SurahListFragment.newInstance(
            pageType,
            mDetailsCallBack,
            mActionGetSurahDetailFragment
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "     ${mPageTitles[position]}     "
    }

}