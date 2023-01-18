package com.gakk.noorlibrary.ui.fragments.islamicName

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
import com.gakk.noorlibrary.ui.adapter.IslamicNamePagerAdapter
import com.gakk.noorlibrary.util.setApplicationLanguage

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/15/2021, Thu
 */

private val ARG_PARAM_NAME_TYPE = "nameType"

internal class IslamicNameTabFragment : Fragment() {
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentIslamicNameTabBinding
    private lateinit var mPageTitles: Array<String>
    private var isFemaleNames: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            isFemaleNames = it?.getBoolean(ARG_PARAM_NAME_TYPE) as Boolean
        }
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_islamic_name_tab, container, false)
        mPageTitles = arrayOf(
            requireContext().resources.getString(R.string.names),
            requireContext().resources.getString(R.string.fav_list)
        )
        binding.pager.adapter =
            IslamicNamePagerAdapter(
                childFragmentManager,
                mPageTitles,
                mDetailsCallBack,
                isFemaleNames
            )
        binding.tabLayout.setupWithViewPager(binding.pager)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        updateToolbarForThisFragment()

        return binding.root
    }

    fun updateToolbarForThisFragment() {
        if (isFemaleNames) {
            mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.female_islamic_names))
        } else {
            mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.male_islamic_names))
        }
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }


    companion object {

        @JvmStatic
        fun newInstance(isFemaleNames: Boolean) =
            IslamicNameTabFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM_NAME_TYPE, isFemaleNames)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.islamic_name))
    }
}