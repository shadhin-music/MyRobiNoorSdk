package com.gakk.noorlibrary.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.databinding.LayoutMenuItemBinding
import com.gakk.noorlibrary.model.BottomSheetItem
import com.gakk.noorlibrary.ui.activity.YoutubePlayerActivity
import com.gakk.noorlibrary.ui.activity.khatamquran.KhatamQuranVideoActivity
import com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker.HajjTrackerActivity
import com.gakk.noorlibrary.ui.fragments.tabs.MoreFragmentCallBack
import com.gakk.noorlibrary.util.*

internal class BottomSheetAdapter(
    bottomSheetItemList: List<BottomSheetItem>,
    callback: MainCallback,
    moreFragmentCallBack: MoreFragmentCallBack
) :
    RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder>() {
    var bottomSheetItemList: List<BottomSheetItem>
    private val mCallBack: MainCallback
    private val mMoreFragmentCallBack: MoreFragmentCallBack

    init {
        this.bottomSheetItemList = bottomSheetItemList
        mCallBack = callback
        mMoreFragmentCallBack = moreFragmentCallBack

    }

    inner class BottomSheetViewHolder : RecyclerView.ViewHolder {
        var menuBinding: LayoutMenuItemBinding? = null

        constructor(binding: LayoutMenuItemBinding) : super(binding.root) {
            menuBinding = binding
            menuBinding?.root?.let {
                it.resizeView(
                    ViewDimension.OneFourthScreenWidth,
                    mCallBack.getScreenWith(),
                    it.context
                )
                it.handleClickEvent {
                    val title = FragmentDestinationMap.getDestinationFragmentName(
                        bottomSheetItemList.get(adapterPosition).title,
                        menuBinding?.tvMenuItemText?.context!!
                    )
                    if (title == PAGE_LIVE_VIDEO) {
                        // open youtube player activity direct if live video menu is selected
                        it.context.startActivity(
                            Intent(it.context, YoutubePlayerActivity::class.java).apply {
                                putExtra(IS_IJTEMA_LIVE_VIDEO, false)
                            }
                        )
                    } else if (title == PAGE_KHATAM_QURAN) {
                        it.context.startActivity(
                            Intent(it.context, KhatamQuranVideoActivity::class.java)
                        )
                    } else if (title == PAGE_HAJJ_SHARE_LOCATION) {
                        it.context.startActivity(
                            Intent(it.context, HajjTrackerActivity::class.java)
                        )
                    } else {
                        if (isNetworkConnected(it.context)) {
                            title?.let { it1 ->
                                mCallBack.openDetailsActivityWithPageName(
                                    it1
                                )
                            }
                        } else {
                            mCallBack.showToastMessage("Please check internet connection!")
                        }

                    }
                    mMoreFragmentCallBack.dismissMoreFragment()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val binding: LayoutMenuItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_menu_item,
            parent,
            false
        )
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.menuBinding?.bottomSheetItem = bottomSheetItemList[position]
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
                context.resources.getString(R.string.cat_wallpaper) -> PAGE_WALL_PAPER
                context.resources.getString(R.string.cat_animation) -> PAGE_ANIMATION
                context.resources.getString(R.string.cat_ninety_nine_names_allah) -> PAGE_99_NAMES_ALLAH
                context.resources.getString(R.string.namaz_visual) -> PAGE_NAMAZ_VISUAL
                context.resources.getString(R.string.cat_islamic_video) -> PAGE_ISLAMIC_VIDEOS
                context.resources.getString(R.string.cat_compass) -> PAGE_COMPASS
                context.resources.getString(R.string.cat_nearest_mosque) -> PAGE_NEAREST_MOSQUE
                context.resources.getString(R.string.cat_nearest_retuarant) -> PAGE_NEAREST_RESTAURANT
                context.resources.getString(R.string.txt_jakat_calculator) -> PAGE_JAKAT
                context.resources.getString(R.string.cat_live_video) -> PAGE_LIVE_VIDEO
                context.resources.getString(R.string.cat_islamic_name) -> PAGE_ISLAMIC_NAME
                context.resources.getString(R.string.cat_quran_school) -> PAGE_CAT_QURAN_SCHOOL
                context.resources.getString(R.string.cat_scholar_video) -> PAGE_SCHOLARS_LIST
                context.resources.getString(R.string.cat_tasbih) -> PAGE_TASBIH
                context.resources.getString(R.string.cat_hajj) -> PAGE_HAJJ_HOME
                context.resources.getString(R.string.cat_islamic_calender) -> PAGE_ISLAMIC_CALENDER
                context.resources.getString(R.string.cat_azan) -> PAGE_AZAN
                context.resources.getString(R.string.cat_tracker) -> PAGE_TRACKER
                context.resources.getString(R.string.cat_qurbani) -> PAGE_QURBANI_HOME
                context.resources.getString(R.string.cat_instructive_video) -> PAGE_CAT_INSTRUCTIVE_VIDEO
                context.resources.getString(R.string.txt_title_inspiration) -> PAGE_CAT_INSLAMIC_INSPIRATION
                context.resources.getString(R.string.cat_islamic_song) -> PAGE_ISLAMIC_SONGS
                context.resources.getString(R.string.cat_eid_e_miladunnobi),
                context.resources.getString(R.string.cat_eid_e_miladunnobi_robi) -> PAGE_EID_E_MILADUNNOBI
                "Biography" -> PAGE_BIOGRAPHY
                context.resources.getString(R.string.cat_quran_learning) -> PAGE_QURAN_LEARNING
                "Course Complete" -> PAGE_COURSE_COMPLETE
                context.resources.getString(R.string.cat_donation) -> PAGE_DONATION_HOME
                context.resources.getString(R.string.cat_ijtema) -> PAGE_IJTEMA
                context.resources.getString(R.string.cat_quiz) -> PAGE_QUIZ_LEADERBOARD
                context.resources.getString(R.string.cat_hajj_tracker) -> PAGE_HAJJ_SHARE_LOCATION
                context.resources.getString(R.string.cat_corona_funeral_service) -> PAGE_CORONA_FUNERAL_SERVICE
                context.resources.getString(R.string.cat_islamic_event) -> PAGE_ISLAMIC_EVENT
                context.resources.getString(R.string.cat_islamic_podcast) -> PAGE_ISLAMIC_PODCAST
                context.resources.getString(R.string.cat_khatam_quran) -> PAGE_KHATAM_QURAN
                context.resources.getString(R.string.cat_eid_jamat) -> PAGE_EID_JAMAT
                context.resources.getString(R.string.cat_hajj_package) -> PAGE_HAJJ_PACKAGE
                context.resources.getString(R.string.cat_live_qa) -> PAGE_CAT_LIVE_QA
                "Online Hut" -> ONLINE_HUT_HOME
                else -> null
            }
        }
    }
}
