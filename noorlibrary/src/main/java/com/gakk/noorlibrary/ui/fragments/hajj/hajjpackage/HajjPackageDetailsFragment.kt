package com.gakk.noorlibrary.ui.fragments.hajj.hajjpackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.FragmentHajjPackageDetailsBinding
import com.gakk.noorlibrary.model.literature.Literature

private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class HajjPackageDetailsFragment : Fragment() {

    private lateinit var binding: FragmentHajjPackageDetailsBinding
    private var mDetailsCallBack: DetailsCallBack? = null
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
            HajjPackageDetailsFragment().apply {
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
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_hajj_package_details,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listItem = mLiterature

        mDetailsCallBack?.setToolBarTitle(mLiterature?.title)
    }
}