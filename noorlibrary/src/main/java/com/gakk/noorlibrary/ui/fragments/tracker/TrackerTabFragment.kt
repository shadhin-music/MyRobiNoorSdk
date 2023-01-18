package com.gakk.noorlibrary.ui.fragments.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentIslamicNameTabBinding
import com.gakk.noorlibrary.ui.adapter.TrackerPagerAdapter
import com.gakk.noorlibrary.util.setApplicationLanguage

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

internal class TrackerTabFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentIslamicNameTabBinding
    private lateinit var mPageTitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_islamic_name_tab, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mPageTitles = arrayOf(
            requireContext().resources.getString(R.string.prayer_tracker),
            requireContext().resources.getString(R.string.ramadan_tracker)
        )
        binding.pager.adapter =
            TrackerPagerAdapter(
                childFragmentManager,
                mPageTitles,
                mDetailsCallBack,
            )
        binding.tabLayout.setupWithViewPager(binding.pager)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        updateToolbarForThisFragment()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_tracker))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrackerTabFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.islamic_name))
    }
}