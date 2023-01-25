package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.ui.adapter.LiteratureListFragmentPagerAdapter
import com.gakk.noorlibrary.ui.adapter.ZakatListAdapter
import com.gakk.noorlibrary.util.LiteratureType
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.views.CustomTabLayout


private const val ARG_LITERATURE_TYPE = "literatureType"

internal class LiteratureHomeFragment : Fragment() {
    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiteratureType: LiteratureType? = null
    private lateinit var pager: ViewPager
    private lateinit var tabLayout: CustomTabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLiteratureType = it.getSerializable(ARG_LITERATURE_TYPE) as LiteratureType
        }
        mDetailsCallBack = requireActivity() as? DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_literature_home,
            container, false
        )

        pager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mPageTitles = when (mLiteratureType) {
            LiteratureType.Hadis -> {
                arrayOf(
                    requireContext().resources.getString(R.string.hadis_list),
                    requireContext().resources.getString(R.string.fav_list)
                )
            }

            LiteratureType.Jakat -> {
                arrayOf(
                    requireContext().resources.getString(R.string.txt_jakat_calculator),
                    requireContext().resources.getString(R.string.txt_save_calculation)
                )
            }
            else -> {
                arrayOf(
                    requireContext().resources.getString(R.string.category_list),
                    requireContext().resources.getString(R.string.fav_list)
                )
            }
        }

        val catId = when (mLiteratureType) {
            LiteratureType.Hadis -> R.string.hadis_cat_id.getLocalisedTextFromResId()
            LiteratureType.Dua -> R.string.dua_cat_id.getLocalisedTextFromResId()
            LiteratureType.Jakat -> R.string.jakat_cat_id.getLocalisedTextFromResId()
            else -> R.string.hadis_cat_id.getLocalisedTextFromResId()
        }


        pager.adapter = LiteratureListFragmentPagerAdapter(
            fragmentManager = childFragmentManager,
            pageTitles = mPageTitles,
            detailsCallBack = mDetailsCallBack,
            catId = catId,
            literatureType = mLiteratureType!!
        )
        tabLayout.setupWithViewPager(pager)
        ZakatCalculationObserver.attatchViewPager(pager)


        updateToolbarForThisFragment()
    }


    fun updateToolbarForThisFragment() {
        val pageTitle = when (mLiteratureType) {
            LiteratureType.Hadis -> resources.getString(R.string.cat_hadith)
            LiteratureType.Dua -> resources.getString(R.string.cat_dua)
            LiteratureType.NamazRules -> resources.getString(R.string.cat_namaz_sikhha)
            LiteratureType.Jakat -> resources.getString(R.string.txt_jakat_calculator)
            else -> ""
        }

        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)

        pageTitle.let {
            mDetailsCallBack?.setToolBarTitle(pageTitle)
        }

    }

    override fun onDestroy() {
        ZakatCalculationObserver.clearResource()
        super.onDestroy()
    }


    companion object {

        @JvmStatic
        fun newInstance(literatureType: LiteratureType) =
            LiteratureHomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_TYPE, literatureType)
                }
            }
    }


}

object ZakatCalculationObserver {
    var viewPager: ViewPager? = null
    var adapter: ZakatListAdapter? = null
    fun attatchViewPager(viewPager: ViewPager) {
        this.viewPager = viewPager
    }

    fun switchTabAtIndex(index: Int) {
        viewPager?.currentItem = index
    }

    fun attatchAdapter(adapter: ZakatListAdapter?) {
        this.adapter = adapter
    }

    fun updateZakatList(list: List<ZakatDataModel>) {
        adapter?.updateZakatList(list)
        adapter?.notifyDataSetChanged()
    }

    fun clearResource() {
        viewPager = null
        adapter = null
    }
}