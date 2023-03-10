package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.home.Data
import com.gakk.noorlibrary.ui.fragments.NamazTimingFragment
import com.gakk.noorlibrary.ui.fragments.billboard.BillboardQuranFragment
import com.gakk.noorlibrary.ui.fragments.tabs.BillboardItemControl
import com.gakk.noorlibrary.ui.fragments.tabs.HomeCellItemControl
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.views.IndicatorLayout


@Keep
class HomeFragmentAdapter(
    homeList: MutableList<Data>,
    callback: MainCallback,
    billBoardCallback: BillboardItemControl,
    prayerTimeCalculator: PrayerTimeCalculator,
    homeCellItemControl: HomeCellItemControl
) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {


    private val homeList: List<Data>
    private val mCallBack: MainCallback
    private val mBillBoardCallback: BillboardItemControl
    private val fragmentList = ArrayList<Fragment>()
    var prayerData: List<com.gakk.noorlibrary.model.tracker.Data>? = null
    var nextWaqt: String = ""
    private val mPrayerTimeCalculator: PrayerTimeCalculator
    private val mHomeCellItemControl: HomeCellItemControl
    private var personalTrackerPos = -1

    init {
        this.homeList = homeList.sortedBy { it.order }
        mCallBack = callback
        mBillBoardCallback = billBoardCallback
        mPrayerTimeCalculator = prayerTimeCalculator
        mHomeCellItemControl = homeCellItemControl


    }


    companion object {
        private const val CELL_PRAYER_TIMING = 0
        private const val CELL_TODAY_AYAT = 1
        private const val CELL_PRAYER = 2
        private const val CELL_HAJJ = 5
        private const val CELL_ROMJAN_AMOL = 6
        private const val CELL_NAMAZ_VISUAL = 8
        private const val CELL_ISLAM_PILLER = 9
        private const val CELL_NEAREST_MOSQUE = 11
        private const val CELL_EMPTY = -1
    }

    inner class HomeFragmentViewHolder
        (layoutId: Int, layoutView: View) : RecyclerView.ViewHolder(layoutView) {

        val view: View = layoutView
        val layoutTag = layoutId

        init {


            when (layoutId) {
                CELL_PRAYER_TIMING -> {

                    //view

                    val vpSliderPrayer = layoutView.findViewById<ViewPager2>(R.id.vpSliderPrayer)
                    val indicatorLayout =
                        layoutView.findViewById<IndicatorLayout>(R.id.indicatorLayout)

                    val adapter =
                        SliderAdapter(layoutView.context as FragmentActivity)

                    vpSliderPrayer.adapter = adapter
                    indicatorLayout.setIndicatorCount(mBillBoardCallback.getListSize() + 1)

                    vpSliderPrayer.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                            indicatorLayout.selectCurrentPosition(position)

                        }
                    })



                    fragmentList.add(NamazTimingFragment())
                    for (i in 0..mBillBoardCallback.getListSize() - 1) {

                        fragmentList.add(
                            BillboardQuranFragment.newInstance(
                                mBillBoardCallback.getitem(i)
                            )
                        )
                    }

                    adapter.setFragmentList(fragmentList)

                }
            }
        }
    }


    override fun getItemCount(): Int {

        return homeList.size + 1

    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        val pos = when (position > 0) {
            true -> {
                position - 1
            }
            else -> {
                position
            }
        }
        val list = homeList.get(pos)

        when (holder.layoutTag) {
            CELL_TODAY_AYAT -> {
                //view
                val imgBg = holder.view.findViewById<AppCompatImageView>(R.id.imgBg)
                val ayattext = holder.view.findViewById<AppCompatTextView>(R.id.ayattext)
                val imgAyat = holder.view.findViewById<AppCompatImageView>(R.id.imgAyat)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                val tvAyat = holder.view.findViewById<AppCompatTextView>(R.id.tvAyat)

                val rlRead = holder.view.findViewById<RelativeLayout>(R.id.rlRead)
                val rlShare = holder.view.findViewById<RelativeLayout>(R.id.rlShare)
                val clImg = holder.view.findViewById<ConstraintLayout>(R.id.clImg)

                val data = list
                val ayat = list.items?.get(0)
                val item = list.items?.get(0)

                val dataitem = ImageFromOnline("ic_bg_ayat_today.png")

                setImageFromUrl(imgBg, dataitem.fullImageUrl, progressBar)
                setPatchImageFromUrl(imgAyat, data.fullImageUrl)
                tvAyat.text = data.patchName

                ayattext.text = ayat?.text

                rlRead?.handleClickEvent {

                    if (isNetworkConnected(rlRead.context)) {
                        item?.textInArabic?.let { it1 -> mCallBack.openCurrentSurahById(it1) }
                    } else {
                        Log.e("sss", "called")
                        mCallBack.showToastMessage(rlRead.context?.getString(R.string.txt_check_internet)!!)
                    }
                }

                rlShare?.handleClickEvent {

                    mHomeCellItemControl.shareBitMap(
                        getBitmapFromView(
                            clImg?.context!!,
                            clImg
                        )
                    )
                }
            }

            CELL_ROMJAN_AMOL -> {
                val data = list

                val imgRomjanAmol = holder.view.findViewById<AppCompatImageView>(R.id.imgRomjanAmol)
                val tvRomjanAmol = holder.view.findViewById<AppCompatTextView>(R.id.tvRomjanAmol)
                val recyclerViewRomjanAmol =
                    holder.view.findViewById<RecyclerView>(R.id.recyclerViewRomjanAmol)

                setPatchImageFromUrl(imgRomjanAmol, data.fullImageUrl)
                tvRomjanAmol.text = data.patchName

                recyclerViewRomjanAmol.adapter = RomjanAmolAdapter(list.contentBaseUrl!!, mCallBack)
                    .apply {
                        submitList(list.items)
                    }
            }

            CELL_PRAYER -> {
                val imgAyat = holder.view.findViewById<AppCompatImageView>(R.id.imgAyat)
                val tvAyat = holder.view.findViewById<AppCompatTextView>(R.id.tvAyat)
                val recyclerViewPrayer =
                    holder.view.findViewById<RecyclerView>(R.id.recyclerViewPrayer)

                val data = list
                setPatchImageFromUrl(imgAyat, data.fullImageUrl)
                tvAyat.text = data.patchName
                recyclerViewPrayer.adapter =
                    HomePrayerAdapter(
                        data.contentBaseUrl!!,
                        data.items!!,
                        mCallBack,
                        mHomeCellItemControl
                    )
            }

            CELL_HAJJ -> {
                val data = list
                val contentbaseurl = list.contentBaseUrl
                val hajj = list.items?.get(0)

                val cardBanner = holder.view.findViewById<CardView>(R.id.cardBanner)
                val imgInspiration =
                    holder.view.findViewById<AppCompatImageView>(R.id.imgInspiration)
                val tvTitleInspiration =
                    holder.view.findViewById<AppCompatTextView>(R.id.tvTitleInspiration)
                val mainbg = holder.view.findViewById<AppCompatImageView>(R.id.bg)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)

                setPatchImageFromUrl(imgInspiration, data.fullImageUrl)
                tvTitleInspiration.text = data.patchName


                setImageFromUrl(
                    mainbg,
                    contentbaseurl + "/" + hajj?.imageUrl,
                    progressBar,
                    PLACE_HOLDER_16_9
                )

                cardBanner!!.handleClickEvent {
                    list.about.let { it1 ->
                        it1?.let { it2 ->
                                mCallBack.openDetailsActivityWithPageName(
                                    it2,
                                )
                        }
                    }
                }
            }


            CELL_NEAREST_MOSQUE -> {


                val data = list
                val inspirationItem = list.items?.get(0)
                val inspiration = inspirationItem

                val tvShareAyat = holder.view.findViewById<AppCompatTextView>(R.id.tvShareAyat)
                val imgShare = holder.view.findViewById<AppCompatImageView>(R.id.imgShare)
                val rlShare = holder.view.findViewById<RelativeLayout>(R.id.rlShare)
                val imgBg = holder.view.findViewById<AppCompatImageView>(R.id.imgBg)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)

                setImageFromUrl(
                    imgBg,
                    data.contentBaseUrl + "/" + inspiration?.imageUrl,
                    progressBar,
                    PLACE_HOLDER_16_9
                )

                if (!list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {
                    tvShareAyat.setText(tvShareAyat.context.getString(R.string.txt_learn_more))
                    tvShareAyat.setTextColor(
                        ContextCompat.getColor(
                            tvShareAyat.context,
                            R.color.ash
                        )
                    )
                    imgShare.setImageResource(R.drawable.ic_read)
                }

                rlShare?.handleClickEvent {

                    if (list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {

                        mHomeCellItemControl.shareImage(list.contentBaseUrl + "/" + inspirationItem?.imageUrl)
                    } else {
                        mCallBack.openDetailsActivityWithPageName(
                            list.about?.trim()!!,
                            surahId = list.items?.get(0)?.category,
                        )
                    }
                }

                imgBg?.handleClickEvent {

                    if (list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {
                        mCallBack.openDetailsActivityWithPageName(
                            list.about?.trim()!!,
                            surahId = list.items?.get(0)?.category,
                        )
                    } else {
                        mCallBack.openDetailsActivityWithPageName(
                            list.about?.trim()!!,
                            surahId = list.items?.get(0)?.category,
                        )
                    }

                }

            }

            CELL_ISLAM_PILLER -> {
                val imgIslamPiller =
                    holder.view.findViewById<AppCompatImageView>(R.id.imgIslamPiller)
                val tvIslamPiller = holder.view.findViewById<AppCompatTextView>(R.id.tvIslamPiller)
                val rvIslamPiller = holder.view.findViewById<RecyclerView>(R.id.rvIslamPiller)

                val data = list
                rvIslamPiller.adapter =
                    IslamPillerAdapter(list.contentBaseUrl!!, list.items!!, mCallBack)

                setPatchImageFromUrl(imgIslamPiller, data.fullImageUrl)
                tvIslamPiller.text = data.patchName

            }

        }

    }


    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return CELL_PRAYER_TIMING
        } else {
            return when (homeList.get(position - 1).patchViewType) {

                PATCH_TYPE_AYAT -> {
                    CELL_TODAY_AYAT
                }
                PATCH_TYPE_PRAYER_FOR_DUA -> {
                    CELL_PRAYER
                }

                PATCH_TYPE_LOCAL_CONTENT -> {
                    CELL_HAJJ
                }
                PATCH_TYPE_ROMJAN_AMOL -> {
                    CELL_ROMJAN_AMOL
                }

                PATCH_TYPE_ISLAM_PILLER -> {
                    CELL_ISLAM_PILLER
                }

                PATCH_TYPE_NEAREST_MOSQUE -> {
                    CELL_NEAREST_MOSQUE
                }

                else -> {
                    CELL_EMPTY
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        lateinit var view: View

        when (viewType) {

            CELL_PRAYER_TIMING -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_prayer_timing, parent, false)

                return HomeFragmentViewHolder(CELL_PRAYER_TIMING, view)
            }


            CELL_TODAY_AYAT -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_today_ayat, parent, false)

                return HomeFragmentViewHolder(CELL_TODAY_AYAT, view)
            }

            CELL_PRAYER -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_prayer, parent, false)

                return HomeFragmentViewHolder(CELL_PRAYER, view)
            }


            CELL_NEAREST_MOSQUE -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_islamic_inspiration, parent, false)

                return HomeFragmentViewHolder(CELL_NEAREST_MOSQUE, view)
            }
            CELL_HAJJ -> {


                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_hajj, parent, false)

                return HomeFragmentViewHolder(CELL_HAJJ, view)
            }

            CELL_EMPTY -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_empty, parent, false)

                val viewHolder = HomeFragmentViewHolder(CELL_EMPTY, view)

                val emptyView = viewHolder.view.findViewById<ConstraintLayout>(R.id.emptyView)
                emptyView.visibility = View.GONE
                return viewHolder
            }

            CELL_ROMJAN_AMOL -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_romjan_amol, parent, false)

                return HomeFragmentViewHolder(CELL_ROMJAN_AMOL, view)
            }

            CELL_NAMAZ_VISUAL -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_hajj, parent, false)

                return HomeFragmentViewHolder(CELL_NAMAZ_VISUAL, view)
            }

            CELL_ISLAM_PILLER -> {

                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_islam_piller, parent, false)


                return HomeFragmentViewHolder(CELL_ISLAM_PILLER, view)
            }

            else -> throw IllegalStateException("Illegal view type")
        }

    }

    fun invalidatePersonalTracker() {
        if (personalTrackerPos != -1) {
            notifyItemChanged(personalTrackerPos)
        }
    }
}


