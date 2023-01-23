package com.gakk.noorlibrary.ui.fragments.billboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.billboard.Data
import com.gakk.noorlibrary.ui.activity.khatamquran.KhatamQuranVideoActivity
import com.gakk.noorlibrary.ui.adapter.FragmentDestinationMap
import com.gakk.noorlibrary.util.PLACE_HOLDER_1_1
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl

private const val ARG_BILLBORAD_DATA = "billboradData"

internal class BillboardQuranFragment : Fragment() {
    private lateinit var mCallback: MainCallback
    private lateinit var mData: Data
    private lateinit var imgBillboard : AppCompatImageView
    private lateinit var progressBar : ProgressBar


    companion object {
        @JvmStatic
        fun newInstance(data: Data) =
            BillboardQuranFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BILLBORAD_DATA, data)
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

        val view = inflater.inflate(
            R.layout.fragment_billboard_quran,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        imgBillboard = view.findViewById(R.id.imgBillboard)
        progressBar = view.findViewById(R.id.progressBar)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImageFromUrl(imgBillboard,mData.fullImageUrl,progressBar,PLACE_HOLDER_1_1)

        imgBillboard.handleClickEvent {
            Log.e("Title", "ss${mData.categoryName.trim()}")
            val title: String? = FragmentDestinationMap.getDestinationFragmentName(
                mData.categoryName.trim(),
                requireContext()
            )
            title?.let { text ->

                if (mData.categoryName.trim().equals(getString(R.string.cat_khatam_quran))) {
                    startActivity(Intent(requireContext(), KhatamQuranVideoActivity::class.java))
                } else {
                    mCallback.openDetailsActivityWithPageName(
                        text
                    )
                }

            }

        }

    }
}