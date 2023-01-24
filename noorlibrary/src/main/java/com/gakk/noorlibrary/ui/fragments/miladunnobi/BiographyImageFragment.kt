package com.gakk.noorlibrary.ui.fragments.miladunnobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.FragmentBiographySliderBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.PLACE_HOLDER_1_1
import com.gakk.noorlibrary.util.setImageFromUrl

private const val ARG_PAGER_DATA = "pagerData"

internal class BiographyImageFragment : Fragment() {
    private lateinit var mData: Literature

    //view
    private lateinit var progressBar: ProgressBar
    private lateinit var imgBillboard: AppCompatImageView


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

        val view = inflater.inflate(
            R.layout.fragment_biography_slider,
            container, false
        )

        initView(view)

        setImageFromUrl(imgBillboard,mData.fullImageUrl,progressBar,PLACE_HOLDER_1_1)

        return view
    }

    private fun initView(view:View)
    {
        progressBar = view.findViewById(R.id.progressBar)
        imgBillboard = view.findViewById(R.id.imgBillboard)
    }

}