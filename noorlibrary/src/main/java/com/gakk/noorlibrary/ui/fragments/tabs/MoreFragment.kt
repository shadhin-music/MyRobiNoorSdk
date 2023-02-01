package com.gakk.noorlibrary.ui.fragments.tabs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.BottomSheetItem
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.adapter.BottomSheetAdapter
import com.gakk.noorlibrary.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


internal class MoreFragment : BottomSheetDialogFragment(), MoreFragmentCallBack {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var mCallback: MainCallback
    private lateinit var rvCategory: RecyclerView
    private lateinit var imgDrag: AppCompatImageView
    private lateinit var layoutSubscribeCon: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as MainCallback
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MoreFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_more,
            container, false
        )

        rvCategory = view.findViewById(R.id.rvCategory)
        imgDrag = view.findViewById(R.id.imgDrag)
        layoutSubscribeCon = view.findViewById(R.id.layoutSubscribeCon)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuList = getBottomSheetItemList()
        rvCategory.adapter = BottomSheetAdapter(menuList, mCallback, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.e("subchk", "${Util.checkSub()}")
        imgDrag.handleClickEvent {
            dialog.dismiss()
        }


        layoutSubscribeCon.handleClickEvent {

            dialog.dismiss()

            if (isNetworkConnected(requireContext())) {

                if (AppPreference.subDaily || AppPreference.subFifteenDays) {
                    Intent(context, DetailsActivity::class.java).apply {
                        this.putExtra(PAGE_NAME, PAGE_SUBSCRIPTION)
                        startActivity(this)
                    }
                }  else if (AppPreference.subMonthlySsl || AppPreference.subHalfYearlySsl || AppPreference.subYearlySsl) {
                    Intent(context, DetailsActivity::class.java).apply {
                        this.putExtra(PAGE_NAME, PAGE_SUBSCRIPTION_SSL)
                        this.putExtra(IS_FAV, false)
                        startActivity(this)
                    }
                }  else {
                    Intent(context, DetailsActivity::class.java).apply {
                        this.putExtra(PAGE_NAME, PAGE_SUBSCRIPTION_OPTION_LIST)
                        startActivity(this)
                    }
                }

            } else {
                mCallback.showToastMessage("Please check internet connection!")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                setupFullHeight(it)
            }
        }


        return dialog
    }

    override fun onDestroy() {
        mCallback?.makeMoreFragmentNull()
        super.onDestroy()
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    fun getBottomSheetItemList(): List<BottomSheetItem> {
        val bottomSheetItems: ArrayList<BottomSheetItem> = ArrayList()
        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_quran,
                getString(R.string.cat_quran)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_roja,
                getString(R.string.cat_roja)
            )
        )

        bottomSheetItems.add(BottomSheetItem(R.drawable.ic_cat_dua, getString(R.string.cat_dua)))

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_hadis,
                getString(R.string.cat_hadith)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_jakat,
                getString(R.string.txt_jakat_calculator)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_mosque,
                getString(R.string.cat_nearest_mosque)
            )
        )

        if (AppPreference.language.equals(LAN_BANGLA)) {

            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_islamic_podcast,
                    getString(R.string.cat_islamic_podcast)
                )
            )

            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_live_question_answer,
                    getString(R.string.cat_live_qa)
                )
            )
        }



        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_namaz_sikhha,
                getString(R.string.cat_namaz_sikhha)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_hajj,
                getString(R.string.cat_hajj)
            )
        )


        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_qurbani,
                getString(R.string.cat_qurbani)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_hajj_live,
                getString(R.string.cat_live_video)
            )
        )

        val txtMiladunnobi: String
        txtMiladunnobi = getString(R.string.cat_eid_e_miladunnobi_robi)


        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_miladunnobi,
                txtMiladunnobi
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_cat_donation,
                getString(R.string.cat_donation)
            )
        )




        if (AppPreference.language.equals(LAN_BANGLA)) {

            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_cat_quran_school,
                    getString(R.string.cat_quran_school)
                )
            )


            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_cat_ijtema,
                    getString(R.string.cat_ijtema)
                )
            )

            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_eid_jamater_location,
                    getString(R.string.cat_eid_jamat)
                )
            )

            bottomSheetItems.add(
                BottomSheetItem(
                    R.drawable.ic_hajj_tracker,
                    getString(R.string.cat_hajj_tracker)
                )
            )

        }

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_islamic_event,
                getString(R.string.cat_islamic_event)
            )
        )

        bottomSheetItems.add(
            BottomSheetItem(
                R.drawable.ic_umrah_hajj,
                getString(R.string.cat_umrah_hajj)
            )
        )


        return bottomSheetItems
    }

    override fun dismissMoreFragment() {
        dismiss()
    }
}

interface MoreFragmentCallBack {
    fun dismissMoreFragment()
}
