package com.gakk.noorlibrary.ui.adapter.covid


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.gms.maps.model.MarkerOptions
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.MapFragment
import com.gakk.noorlibrary.ui.fragments.covid.AmbulanceListFragment
import com.gakk.noorlibrary.util.PAGE_AMBULANCE

class AmbulancePagerAdapter(
    fragmentManager: FragmentManager,
    pageTitles: Array<String>,
    detailsCallBack: DetailsCallBack?,
    literatureList: MutableList<Literature>? = null,
    markerOptions: Array<MarkerOptions>? = null
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    var mPageTitles = pageTitles
    var mDetailsCallBack = detailsCallBack
    val mList = literatureList
    val mMarkers = markerOptions

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return AmbulanceListFragment.newInstance(mList!!)

            else -> return MapFragment.newInstance(
                categoryType = PAGE_AMBULANCE,
                markerOptions = mMarkers
            )
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "${mPageTitles[position]}     "
    }

}