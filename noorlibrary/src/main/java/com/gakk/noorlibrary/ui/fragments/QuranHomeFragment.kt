package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.adapter.SurahListFragmentPagerAdapter
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_SURAH_DETAILS
import com.gakk.noorlibrary.views.CustomTabLayout


internal class QuranHomeFragment : Fragment() {

    private lateinit var mDetailsCallBack: DetailsCallBack
    private lateinit var mPageTitles: Array<String>
    private lateinit var pager: ViewPager
    private lateinit var tabLayout: CustomTabLayout

    private val getSurahDetailFragment: (String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment? =
        { id, callBack, list ->
            FragmentProvider.getFragmentByName(
                name = PAGE_SURAH_DETAILS,
                detailsActivityCallBack = callBack,
                id = id,
                surahList = list
            )

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_quran_home,
            container, false
        )

        pager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPageTitles = arrayOf(
            requireContext().resources.getString(R.string.surah_list),
            requireContext().resources.getString(R.string.fav_list)
        )
        pager.adapter =
            SurahListFragmentPagerAdapter(
                childFragmentManager,
                mPageTitles,
                mDetailsCallBack,
                getSurahDetailFragment
            )
        tabLayout.setupWithViewPager(pager)
        mDetailsCallBack.toggleToolBarActionIconsVisibility(false)
        updateToolbarForThisFragment()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack.setToolBarTitle(resources.getString(R.string.cat_quran))
        mDetailsCallBack.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }

    companion object {

        @JvmStatic
        fun newInstance() = QuranHomeFragment()
    }
}