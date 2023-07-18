package com.gakk.noorlibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.BottomSheetItem
import com.gakk.noorlibrary.ui.fragments.tabs.MoreFragmentCallBack
import com.gakk.noorlibrary.util.*

internal class BottomSheetAdapter(
    bottomSheetItemList: List<BottomSheetItem>,
    callback: MainCallback,
    moreFragmentCallBack: MoreFragmentCallBack
) : RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder>() {
    var bottomSheetItemList: List<BottomSheetItem>
    private val mCallBack: MainCallback
    private val mMoreFragmentCallBack: MoreFragmentCallBack

    init {
        this.bottomSheetItemList = bottomSheetItemList
        mCallBack = callback
        mMoreFragmentCallBack = moreFragmentCallBack
    }

    inner class BottomSheetViewHolder
        (binding: View) : RecyclerView.ViewHolder(binding) {

        init {
            binding.let {
                it.resizeView(
                    ViewDimension.OneFourthScreenWidth, mCallBack.getScreenWith(), it.context
                )

                it.handleClickEvent {
                    val title = FragmentDestinationMap.getDestinationFragmentName(
                        bottomSheetItemList.get(adapterPosition).title, it.context!!
                    )
                    if (isNetworkConnected(it.context)) {
                        title?.let { it1 ->
                            mCallBack.openDetailsActivityWithPageName(
                                it1
                            )
                        }
                    } else {
                        mCallBack.showToastMessage("Please check internet connection!")
                    }

                    mMoreFragmentCallBack.dismissMoreFragment()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_menu_item, parent, false)
        return BottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val bottomSheetItem = bottomSheetItemList[position]

        val title = holder.itemView.findViewById<AppCompatTextView>(R.id.tv_menu_item_text)
        val image = holder.itemView.findViewById<AppCompatImageButton>(R.id.btn_menu_item_image)
        title.text = bottomSheetItem.title
        image.setImageResource(bottomSheetItem.resId)
    }

    override fun getItemCount(): Int {
        return bottomSheetItemList.size
    }
}

class FragmentDestinationMap {
    companion object {
        fun getDestinationFragmentName(text: String, context: Context): String? {
            return when (text) {
                context.resources.getString(R.string.cat_quran) -> PAGE_QURAN_HOME
                context.resources.getString(R.string.cat_roja) -> PAGE_ROZA
                context.resources.getString(R.string.cat_dua) -> PAGE_DUA
                context.resources.getString(R.string.cat_hadith) -> PAGE_HADIS
                context.resources.getString(R.string.cat_namaz_sikhha) -> PAGE_NAMAZ_RULES
                context.resources.getString(R.string.namaz_visual) -> PAGE_NAMAZ_VISUAL
                context.resources.getString(R.string.cat_nearest_mosque) -> PAGE_NEAREST_MOSQUE
                context.resources.getString(R.string.txt_jakat_calculator) -> PAGE_JAKAT
                context.resources.getString(R.string.cat_hajj) -> PAGE_HAJJ_HOME
                context.resources.getString(R.string.cat_donation) -> PAGE_DONATION_HOME
                context.resources.getString(R.string.cat_islamic_podcast) -> PAGE_ISLAMIC_PODCAST
                context.resources.getString(R.string.cat_eid_jamat) -> PAGE_EID_JAMAT
                context.resources.getString(R.string.cat_umrah_hajj) -> PAGE_UMRAH_HAJJ
                else -> null
            }
        }
    }
}
