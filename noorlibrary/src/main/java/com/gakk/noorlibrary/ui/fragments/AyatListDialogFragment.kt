package com.gakk.noorlibrary.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.SurahDetailsCallBack
import com.gakk.noorlibrary.ui.adapter.SurahDetailsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val ARG_SURAH_ID = "surahId"
private const val ARG_DETAILS_CALL_BACK = "detailsCallBack"

internal class AyatListDialogFragment : BottomSheetDialogFragment(), SurahDetailsCallBack {

    private lateinit var dialog: BottomSheetDialog

    private var mSurahId: String? = null
    private var mDetailsCallBack: DetailsCallBack? = null

    private lateinit var adapter: SurahDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSurahId = it.getString(ARG_SURAH_ID)
            mDetailsCallBack = it.getSerializable(ARG_DETAILS_CALL_BACK) as DetailsCallBack
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( R.layout.fragment_ayat_list_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            //binding.root.background = //ColorDrawable(Color.TRANSPARENT)
//            adapter=SurahDetailsAdapter(mDetailsCallBack,this,true)
//            binding.rvSurahDetails.adapter=adapter
//            val bottomSheetDialog = it as BottomSheetDialog
//            val parentLayout =
//                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            parentLayout?.setBackgroundResource(android.R.color.transparent)
//            //parentLayout?.setBackgroundColor(resources.getColor(R.color.bg))
//            parentLayout?.let { it ->
//                val behaviour = BottomSheetBehavior.from(it)
//                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
//                val windowHeight = mDetailsCallBack?.getWindowHeight()!!
//                behaviour.peekHeight = windowHeight
//            }
        }


        return dialog
    }



    companion object {

        @JvmStatic
        fun newInstance(surahId: String, detailsCallBack: DetailsCallBack) =
            AyatListDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SURAH_ID, surahId)
                    //putSerializable(ARG_DETAILS_CALL_BACK, detailsCallBack)
                }
            }
    }

    override fun updateSelection(id: String) {
        val selectionControl=adapter.getSurahListAdapterProvider().getAdapter().getViewHolderSelectionControl()
        selectionControl.setSelectedId(id)
        selectionControl.toggleSelectionVisibilityForAll()
    }
}