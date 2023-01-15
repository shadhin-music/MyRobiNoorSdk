package com.gakk.noorlibrary.ui.fragments.billboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.databinding.FragmentBillboardQuranBinding
import com.gakk.noorlibrary.model.billboard.Data
import com.gakk.noorlibrary.ui.activity.khatamquran.KhatamQuranVideoActivity
import com.gakk.noorlibrary.ui.adapter.FragmentDestinationMap
import com.gakk.noorlibrary.util.handleClickEvent

private const val ARG_MAIN_BACK = "mainCallBack"
private const val ARG_BILLBORAD_DATA = "billboradData"

class BillboardQuranFragment : Fragment() {
    private lateinit var binding: FragmentBillboardQuranBinding
    private lateinit var mCallback: MainCallback
    private lateinit var mData: Data


    companion object {
        @JvmStatic
        fun newInstance(data: Data, callback: MainCallback) =
            BillboardQuranFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BILLBORAD_DATA, data)
                    //putSerializable(ARG_MAIN_BACK, callback)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mData = it.getSerializable(ARG_BILLBORAD_DATA) as Data
        }
        mCallback = (requireActivity() as MainCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_billboard_quran, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.item = mData

        binding.imgBillboard.handleClickEvent {
            Log.e("Title","ss${mData.categoryName.trim()}")
             val title: String? = FragmentDestinationMap.getDestinationFragmentName(
                mData.categoryName.trim(),
                requireContext()
            )
            title?.let { text->

                if (mData.categoryName.trim().equals(getString(R.string.cat_khatam_quran))){
                    startActivity(Intent(requireContext(), KhatamQuranVideoActivity::class.java))
                }else {
                    mCallback.openDetailsActivityWithPageName(
                        text
                    )
                }

            }

        }

    }
}