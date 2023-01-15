package com.gakk.noorlibrary.ui.fragments.miladunnobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.FragmentBiographySliderBinding
import com.gakk.noorlibrary.model.literature.Literature

private const val ARG_PAGER_DATA = "pagerData"

class BiographyImageFragment : Fragment() {
    private lateinit var binding: FragmentBiographySliderBinding
    private lateinit var mData: Literature

    companion object {
        @JvmStatic
        fun newInstance(data: Literature) =
            BiographyImageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PAGER_DATA, data)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mData = it.getSerializable(ARG_PAGER_DATA) as Literature
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_biography_slider, container, false)

        binding.item = mData
        return binding.root
    }
}