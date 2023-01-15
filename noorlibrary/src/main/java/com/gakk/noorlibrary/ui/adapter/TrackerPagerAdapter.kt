package com.gakk.noorlibrary.ui.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.fragments.tracker.PrayerTrackerFragment
import com.gakk.noorlibrary.ui.fragments.tracker.RamadanTrackerFragment

class TrackerPagerAdapter(
    fragmentManager: FragmentManager,
    pageTitles: Array<String>,
    detailsCallBack: DetailsCallBack?
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

        when (position) {
            0 -> return PrayerTrackerFragment.newInstance()

            else -> return RamadanTrackerFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "     ${mPageTitles[position]}     "
    }

}