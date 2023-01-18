package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentOrganizationDetailsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_DONATION
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class OrganizationDetailsFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentOrganizationDetailsBinding
    private var mLiterature: Literature? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            mLiterature = it?.getSerializable(ARG_LITERATURE_DETAILS) as Literature?
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance(
            itemLiterature: Literature
        ) =
            OrganizationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_DETAILS, itemLiterature)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_organization_details,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.literature = mLiterature

        binding.btnDonate.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Donation"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        binding.layoutVisit.handleClickEvent {
            mLiterature?.refUrl?.let { mDetailsCallBack?.openUrl(it) }

        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.txt_charity_organization))
    }
}