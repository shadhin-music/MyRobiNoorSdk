package com.gakk.noorlibrary.ui.fragments.covid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.FragmentCovidServiceDetailsBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.handleClickEvent

private const val ARG_FUNERAL_DETAILS = "literatureDetails"

internal class CovidServiceDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCovidServiceDetailsBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLiterature = it.getSerializable(ARG_FUNERAL_DETAILS) as Literature?
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(itemLiterature: Literature) =
            CovidServiceDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_FUNERAL_DETAILS, itemLiterature)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_covid_service_details,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.literature = mLiterature

        binding.item = ImageFromOnline("ic_bg_service_details.png")

        binding.cardPhone.handleClickEvent {
            mDetailsCallBack?.showDialer(mLiterature?.textInArabic!!)
        }

        binding.layoutCall.handleClickEvent {
            mDetailsCallBack?.showDialer(mLiterature?.textInArabic!!)
        }

        binding.cardFacebook.handleClickEvent {
            mLiterature?.refUrl.let {
                mDetailsCallBack?.openUrl(mLiterature?.refUrl!!)
            }
        }

        binding.cardWebsite.handleClickEvent {
            mDetailsCallBack?.openUrl(mLiterature?.pronunciation!!)
        }
    }
}