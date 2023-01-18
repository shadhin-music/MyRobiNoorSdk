package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.*
import com.gakk.noorlibrary.extralib.cardstackview.*
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.home.Data
import com.gakk.noorlibrary.ui.activity.KafelaPlayerActivity
import com.gakk.noorlibrary.ui.fragments.CountControl
import com.gakk.noorlibrary.ui.fragments.NamazTimingFragment
import com.gakk.noorlibrary.ui.fragments.billboard.BillboardQuranFragment
import com.gakk.noorlibrary.ui.fragments.tabs.BillboardItemControl
import com.gakk.noorlibrary.ui.fragments.tabs.HomeCellItemControl
import com.gakk.noorlibrary.util.*


class HomeFragmentAdapter(
    homeList: MutableList<Data>,
    allahNameslist: List<com.gakk.noorlibrary.model.names.Data>,
    callback: MainCallback,
    billBoardCallback: BillboardItemControl,
    prayerTimeCalculator: PrayerTimeCalculator,
    homeCellItemControl: HomeCellItemControl,
    cardStackListener: CardStackListener,
    countControl: CountControl
) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private val homeList: List<Data>
    private val allahNameslist: List<com.gakk.noorlibrary.model.names.Data>
    private val mCallBack: MainCallback
    private val mBillBoardCallback: BillboardItemControl
    private val fragmentList = ArrayList<Fragment>()
    var prayerData: List<com.gakk.noorlibrary.model.tracker.Data>? = null
    var nextWaqt: String = ""
    private val mPrayerTimeCalculator: PrayerTimeCalculator
    private val mHomeCellItemControl: HomeCellItemControl
    private var personalTrackerPos = -1
    private val mstackListener: CardStackListener
    private var allahNameLayoutPos = -1
    private var latyoutAllahNameBinding: LayoutItemAllahNameBinding? = null
    private var latyoutTasbihHomeBinding: LayoutItemTahbihHomeBinding? = null
    var mcardStackLayoutManager: CardStackLayoutManager? = null
    private lateinit var cardAdapter: CardStackAdapter
    var sound = true
    private val mcountControl: CountControl
    private lateinit var tasbihAdapter: TasbihAdapter


    init {
        this.homeList = homeList.sortedBy { it.order }
        this.allahNameslist = allahNameslist
        mCallBack = callback
        mBillBoardCallback = billBoardCallback
        mPrayerTimeCalculator = prayerTimeCalculator
        mHomeCellItemControl = homeCellItemControl
        mstackListener = cardStackListener
        mcountControl = countControl
    }

    inner class HomeFragmentViewHolder : RecyclerView.ViewHolder {

        var bindingPrayerTiming: LayoutItemPrayerTimingBinding? = null

        constructor(itemView: LayoutItemPrayerTimingBinding) : super(itemView.root) {
            bindingPrayerTiming = itemView
            val adapter =
                SliderAdapter(bindingPrayerTiming?.root?.context as FragmentActivity)
            bindingPrayerTiming!!.vpSliderPrayer.adapter = adapter

            bindingPrayerTiming!!.indicatorLayout.setIndicatorCount(mBillBoardCallback.getListSize() + 1)

            bindingPrayerTiming!!.vpSliderPrayer.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    bindingPrayerTiming!!.indicatorLayout.selectCurrentPosition(position)

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

        var bindingAyat: LayoutItemTodayAyatBinding? = null

        constructor(itemView: LayoutItemTodayAyatBinding) : super(itemView.root) {
            bindingAyat = itemView
        }


        var bindingPrayer: LayoutItemPrayerBinding? = null

        constructor(itemview: LayoutItemPrayerBinding) : super(itemview.root) {
            bindingPrayer = itemview
        }


        var bindingPersonalTracker: LayoutItemPersonalTrackerBinding? = null

        constructor(itemview: LayoutItemPersonalTrackerBinding) : super(itemview.root) {
            bindingPersonalTracker = itemview
            val upcomingPrayer = mPrayerTimeCalculator.getUpCommingPrayer()
            bindingPersonalTracker?.tvPrayerToday?.setText(
                bindingPersonalTracker?.tvPrayerToday?.context?.getString(
                    R.string.txt_today
                ) + " \n" + upcomingPrayer.currentWaqtName + " " + bindingPersonalTracker?.tvPrayerToday?.context?.getString(
                    R.string.txt_pray
                )
            )
        }

        var bindingIslamicInspiraton: LayoutItemIslamicInspirationBinding? = null

        constructor(itemView: LayoutItemIslamicInspirationBinding) : super(itemView.root) {
            bindingIslamicInspiraton = itemView

        }

        var bindingHajj: LayoutItemHajjBinding? = null

        constructor(itemView: LayoutItemHajjBinding) : super(itemView.root) {
            bindingHajj = itemView
        }

        var emptyView: LayoutItemEmptyBinding? = null

        constructor(itemView: LayoutItemEmptyBinding) : super(itemView.root) {
            emptyView = itemView
        }

        var bindingRomjanAmol: LayoutItemRomjanAmolBinding? = null

        constructor(itemView: LayoutItemRomjanAmolBinding) : super(itemView.root) {
            bindingRomjanAmol = itemView
        }


        var bindingScholarVideo: LayoutItemScholarVideoBinding? = null

        constructor(itemView: LayoutItemScholarVideoBinding) : super(itemView.root) {
            bindingScholarVideo = itemView
        }


        var bindingIslamPiller: LayoutItemIslamPillerBinding? = null

        constructor(itemView: LayoutItemIslamPillerBinding) : super(itemView.root) {
            bindingIslamPiller = itemView
        }

        var bindingLearnQuran: LayoutItemLearnQuranBinding? = null

        constructor(itemView: LayoutItemLearnQuranBinding) : super(itemView.root) {
            bindingLearnQuran = itemView
        }

        var bindingAllahNameBinding: LayoutItemAllahNameBinding? = null

        constructor(itemView: LayoutItemAllahNameBinding) : super(itemView.root) {
            bindingAllahNameBinding = itemView
            mcardStackLayoutManager = CardStackLayoutManager(
                bindingAllahNameBinding?.cardStackView?.context,
                mstackListener
            )
            mcardStackLayoutManager?.setStackFrom(StackFrom.Top)
            mcardStackLayoutManager?.setVisibleCount(3)
            mcardStackLayoutManager?.setTranslationInterval(20.0f)
            mcardStackLayoutManager?.setScaleInterval(0.95f)
            mcardStackLayoutManager?.setSwipeThreshold(0.3f)
            mcardStackLayoutManager?.setMaxDegree(70.0f)
            mcardStackLayoutManager?.setDirections(Direction.FREEDOM)
            mcardStackLayoutManager?.setCanScrollHorizontal(true)
            mcardStackLayoutManager?.setCanScrollVertical(true)
            mcardStackLayoutManager?.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            mcardStackLayoutManager?.setOverlayInterpolator(LinearInterpolator())
            bindingAllahNameBinding?.cardStackView?.layoutManager = mcardStackLayoutManager
            bindingAllahNameBinding?.cardStackView?.itemAnimator.apply {
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }


        }

        var bindingTasbihHome: LayoutItemTahbihHomeBinding? = null

        constructor(itemView: LayoutItemTahbihHomeBinding) : super(itemView.root) {
            bindingTasbihHome = itemView

            bindingTasbihHome?.progressBarCircle?.progress = 0
        }
    }


    companion object {
        private const val CELL_PRAYER_TIMING = 0
        private const val CELL_TODAY_AYAT = 1
        private const val CELL_PRAYER = 2
        private const val CELL_PERSONAL_TRACKER = 3
        private const val CELL_ISLSMIC_INSPIRATION = 4
        private const val CELL_HAJJ = 5
        private const val CELL_ROMJAN_AMOL = 6
        private const val CELL_SCHOLAR_VIDEO = 7
        private const val CELL_NAMAZ_VISUAL = 8
        private const val CELL_ISLAM_PILLER = 9
        private const val CELL_LEARN_QURAN = 10
        private const val CELL_NEAREST_MOSQUE = 11
        private const val CELL_NAMES_ALLAH = 12
        private const val CELL_TASBIH = 13
        private const val CELL_NATIVE_AD = 14
        private const val CELL_EMPTY = -1
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
        holder.bindingAyat?.let {
            it.data = list
            it.ayat = list.items?.get(0)
            val item = list.items?.get(0)

            it.item = ImageFromOnline("ic_bg_ayat_today.png")

            holder.bindingAyat?.rlRead?.handleClickEvent {

                if (isNetworkConnected(holder.bindingAyat?.rlRead?.context)) {
                    item?.textInArabic?.let { it1 -> mCallBack.openCurrentSurahById(it1) }
                } else {
                    Log.e("sss", "called")
                    mCallBack.showToastMessage(holder.bindingAyat?.rlRead?.context?.getString(R.string.txt_check_internet)!!)
                }
            }

            holder.bindingAyat?.rlShare?.handleClickEvent {

                mHomeCellItemControl.shareBitMap(
                    getBitmapFromView(
                        holder.bindingAyat?.clImg?.context!!,
                        holder.bindingAyat?.clImg!!
                    )
                )
            }
        }

        holder.bindingRomjanAmol?.let {
            it.data = list
            it.recyclerViewRomjanAmol.adapter = RomjanAmolAdapter(list.contentBaseUrl!!, mCallBack)
                .apply {
                    submitList(list.items)
                }

        }

        holder.bindingPrayer?.let {
            it.data = list
            it.recyclerViewPrayer.adapter =
                HomePrayerAdapter(
                    list.contentBaseUrl!!,
                    list.items!!,
                    mCallBack,
                    mHomeCellItemControl
                )
        }

        holder.bindingIslamicInspiraton?.let {
            it.data = list
            val inspirationItem = list.items?.get(0)
            it.inspiration = inspirationItem


            if (!list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {
                it.tvShareAyat.setText(it.tvShareAyat.context.getString(R.string.txt_learn_more))
                it.tvShareAyat.setTextColor(
                    ContextCompat.getColor(
                        it.tvShareAyat.context,
                        R.color.ash
                    )
                )
                it.imgShare.setImageResource(R.drawable.ic_read)
            }

            holder.bindingIslamicInspiraton?.rlShare?.handleClickEvent {

                if (list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {

                    mHomeCellItemControl.shareImage(list.contentBaseUrl + "/" + inspirationItem?.imageUrl)
                } else if (list.about?.trim().equals(PAGE_VIRTUAL_KAFELA)) {
                    holder.bindingIslamicInspiraton?.imgBg?.context?.startActivity(
                        Intent(
                            holder.bindingIslamicInspiraton?.imgBg?.context,
                            KafelaPlayerActivity::class.java
                        )
                    )
                } else {
                    mCallBack.openDetailsActivityWithPageName(
                        list.about?.trim()!!,
                        surahId = list.items?.get(0)?.category,
                    )
                }
            }

            holder.bindingIslamicInspiraton?.imgBg?.handleClickEvent {

                if (list.about?.trim().equals(PAGE_CAT_INSLAMIC_INSPIRATION)) {
                    mCallBack.openDetailsActivityWithPageName(
                        list.about?.trim()!!,
                        surahId = list.items?.get(0)?.category,
                    )
                } else if (list.about?.trim().equals(PAGE_VIRTUAL_KAFELA)) {
                    holder.bindingIslamicInspiraton?.imgBg?.context?.startActivity(
                        Intent(
                            holder.bindingIslamicInspiraton?.imgBg?.context,
                            KafelaPlayerActivity::class.java
                        )
                    )
                } else {
                    mCallBack.openDetailsActivityWithPageName(
                        list.about?.trim()!!,
                        surahId = list.items?.get(0)?.category,
                    )
                }

            }
        }


        holder.bindingHajj?.let {
            it.data = list
            it.contentbaseurl = list.contentBaseUrl
            it.hajj = list.items?.get(0)

            holder.bindingHajj?.cardBanner!!.handleClickEvent {
                //adViewModel.adClickCount()
                list.about.let { it1 ->
                    it1?.let { it2 ->
                        if (it2.equals(PAGE_ISLAMIC_EVENT)) {
                            mCallBack.openDetailsActivityWithPageName(
                                pageName = it2,
                                catId = list.items?.get(0)?.category,
                                subCatId = list.items?.get(0)?.subcategory,
                                isFromHomeEvent = true
                            )
                        } else {
                            mCallBack.openDetailsActivityWithPageName(
                                it2,
                            )
                        }

                    }
                }
            }
        }

        holder.bindingPersonalTracker?.let {
            personalTrackerPos = holder.adapterPosition
            it.contentbaseurl = list.contentBaseUrl
            it.data = list
            it.tracker = list.items?.get(0)

            when (prayerData?.count() ?: 0 == 0) {

                true -> {
                    it.rlNotYet.visibility = View.VISIBLE
                    it.rlPrayed.visibility = View.VISIBLE
                    holder.bindingPersonalTracker?.cardImgTracker?.visibility = View.VISIBLE
                    holder.bindingPersonalTracker?.layoutSwitch?.root?.visibility = View.GONE
                }

                false -> {
                    it.rlNotYet.visibility = View.GONE
                    it.rlPrayed.visibility = View.GONE
                    holder.bindingPersonalTracker?.cardImgTracker?.visibility = View.GONE
                    holder.bindingPersonalTracker?.layoutSwitch?.root?.visibility = View.VISIBLE

                    val salaData = prayerData?.get(0)?.salahStatus
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isChecked =
                        salaData?.fajr == true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isChecked =
                        salaData?.zuhr == true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isChecked =
                        salaData?.asar == true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isChecked =
                        salaData?.maghrib == true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isChecked =
                        salaData?.isha == true
                }

            }
            holder.bindingPersonalTracker?.rlNotYet?.handleClickEvent {
                // adViewModel.adClickCount()
                it.rlNotYet.visibility = View.GONE
                it.rlPrayed.visibility = View.GONE
                holder.bindingPersonalTracker?.cardImgTracker?.visibility = View.GONE
                holder.bindingPersonalTracker?.layoutSwitch?.root?.visibility = View.VISIBLE
            }

            holder.bindingPersonalTracker?.rlPrayed?.handleClickEvent {
                //  adViewModel.adClickCount()
                mHomeCellItemControl.btnClick()
                it.rlNotYet.visibility = View.GONE
                it.rlPrayed.visibility = View.GONE
                holder.bindingPersonalTracker?.cardImgTracker?.visibility = View.GONE
                holder.bindingPersonalTracker?.layoutSwitch?.root?.visibility = View.VISIBLE
            }

            holder.bindingPersonalTracker?.layoutSwitch?.llTracker?.handleClickEvent {
                mCallBack.openDetailsActivityWithPageName(
                    PAGE_TRACKER
                )
            }

            holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.setOnCheckedChangeListener { button, b ->
                holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.VISIBLE
                mHomeCellItemControl.switchClick(b, SWITCH_FAJR)
            }
            holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.setOnCheckedChangeListener { button, b ->
                holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.VISIBLE
                mHomeCellItemControl.switchClick(b, SWITCH_JOHR)
            }
            holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.setOnCheckedChangeListener { button, b ->
                holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.VISIBLE
                mHomeCellItemControl.switchClick(b, SWITCH_ASR)
            }
            holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.setOnCheckedChangeListener { button, b ->
                holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.VISIBLE
                mHomeCellItemControl.switchClick(b, SWITCH_MAGRIB)
            }
            holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.setOnCheckedChangeListener { button, b ->
                holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.VISIBLE
                mHomeCellItemControl.switchClick(b, SWITCH_ESHA)
            }

            holder.bindingPersonalTracker?.progressLayout?.root?.visibility = View.GONE

            when (nextWaqt) {
                holder.bindingPersonalTracker?.rlPrayed?.context?.getString(R.string.txt_fajr) -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = false
                }
                holder.bindingPersonalTracker?.rlPrayed?.context?.getString(R.string.txt_johr) -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = false
                }
                holder.bindingPersonalTracker?.rlPrayed?.context?.getString(R.string.txt_asr) -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = false
                }
                holder.bindingPersonalTracker?.rlPrayed?.context?.getString(R.string.txt_magrib) -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = false
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = false
                }
                holder.bindingPersonalTracker?.rlPrayed?.context?.getString(R.string.txt_esha) -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = false
                }

                else -> {
                    holder.bindingPersonalTracker?.layoutSwitch?.switchFajr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchJohr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchAsr?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchMagrib?.isEnabled = true
                    holder.bindingPersonalTracker?.layoutSwitch?.switchEsha?.isEnabled = true
                }
            }

        }


        holder.bindingIslamPiller?.let {
            it.data = list
            it.rvIslamPiller.adapter =
                IslamPillerAdapter(list.contentBaseUrl!!, list.items!!, mCallBack)
        }

        holder.bindingAllahNameBinding?.let {
            latyoutAllahNameBinding = it
            allahNameLayoutPos = holder.adapterPosition
            it.data = list
            cardAdapter = CardStackAdapter(allahNameslist)
            it.cardStackView.adapter = cardAdapter

            holder.bindingAllahNameBinding?.ivPlay?.handleClickEvent {
                // adViewModel.adClickCount()
                mHomeCellItemControl.playPauseBtnClick()
            }

            holder.bindingAllahNameBinding?.ivReload?.handleClickEvent {
                // adViewModel.adClickCount()
                mHomeCellItemControl.reloadBtnClick()
            }

            holder.bindingAllahNameBinding?.ivSound?.handleClickEvent {
                if (sound) {
                    sound = false
                    holder.bindingAllahNameBinding?.ivSound?.setImageResource(R.drawable.ic_volume_off)
                    mHomeCellItemControl.soundonOffClick(true)
                } else {
                    sound = true
                    holder.bindingAllahNameBinding?.ivSound?.setImageResource(R.drawable.ic_volume)
                    mHomeCellItemControl.soundonOffClick(false)
                }
            }

            holder.bindingAllahNameBinding?.rlLearnMore?.handleClickEvent {
                // adViewModel.adClickCount()
                mCallBack.openDetailsActivityWithPageName(
                    PAGE_99_NAMES_ALLAH
                )
            }

        }

        holder.bindingTasbihHome?.let {

            latyoutTasbihHomeBinding = it

            it.tvTimes.text = "/" + it.tvTimes.context.getString(R.string.text_thirty_three)
            it.tvCount.text = it.tvCount.context.getString(R.string.text_zero)

            it.item = ImageFromOnline("tasbih_bg_home.png")

            it.data = list
            val dua =
                holder.bindingTasbihHome?.rvTasbihItem?.context?.resources?.getStringArray(R.array.tasbih_duas) as Array<String>
            tasbihAdapter = TasbihAdapter(dua, mcountControl, 0, 0)
            holder.bindingTasbihHome?.rvTasbihItem?.adapter = tasbihAdapter

            it.tasbihCountIV.handleClickEvent {
                mHomeCellItemControl.tasbihButtonClick()
            }

            it.rlLearnMore.handleClickEvent {

                mCallBack.openDetailsActivityWithPageName(
                    pageName = PAGE_TASBIH,
                    selectedIndex = tasbihAdapter.getViewClickedIndex(),
                    currentPageNo = tasbihAdapter.getButtonClickedIndex(),
                    itemCount = mHomeCellItemControl.getTasbihCount(),
                    times = mHomeCellItemControl.getTasbihTimes(),
                )
            }

            it.onOffSoundIV.handleClickEvent {
                mHomeCellItemControl.tasbihSoundButtonClick()
            }

            it.resetAllBtn.handleClickEvent {
                //adViewModel.adClickCount()
                mHomeCellItemControl.tasbihResetButtonClick()
            }
        }

    }

    fun getPatchIndexByPatchId(id: String): Int {
        return 2
//        var i=-1
//        homeList?.forEachIndexed { index, data ->
//            if(data.patchId==id){
//                i=index
//                return@forEachIndexed
//            }
//        }
//        return i
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
                PATCH_TYPE_PERSONAL_TRACKER -> {
                    CELL_PERSONAL_TRACKER
                }
                PATCH_TYPE_ISLAMIC_INSPIRATION -> {
                    CELL_ISLSMIC_INSPIRATION
                }
                PATCH_TYPE_LOCAL_CONTENT -> {
                    CELL_HAJJ
                }
                PATCH_TYPE_ROMJAN_AMOL -> {
                    CELL_ROMJAN_AMOL
                }
                PATCH_TYPE_SCHOLAR_VIDEO -> {
                    CELL_SCHOLAR_VIDEO
                }

                PATCH_TYPE_ISLAM_PILLER -> {
                    CELL_ISLAM_PILLER
                }
                PATCH_TYPE_LEARN_QURAN -> {
                    CELL_LEARN_QURAN
                }
                PATCH_TYPE_NEAREST_MOSQUE -> {
                    CELL_NEAREST_MOSQUE
                }
                PATCH_TYPE_NAMES_ALLAH -> {
                    CELL_NAMES_ALLAH
                }
                PATCH_TYPE_TASBIH -> {
                    CELL_TASBIH
                }
                PATCH_TYPE_AD -> {
                    CELL_NATIVE_AD
                }
                else -> {
                    CELL_EMPTY
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        val binding: ViewDataBinding

        AppPreference.language?.let {
            parent.context?.setApplicationLanguage(it)
        }

        when (viewType) {

            CELL_PRAYER_TIMING -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_prayer_timing,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemPrayerTimingBinding)
            }


            CELL_TODAY_AYAT -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_today_ayat,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemTodayAyatBinding)
            }

            CELL_PRAYER -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_prayer,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemPrayerBinding)
            }

            CELL_PERSONAL_TRACKER -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_personal_tracker,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemPersonalTrackerBinding)
            }

            CELL_ISLSMIC_INSPIRATION -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_islamic_inspiration,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemIslamicInspirationBinding)
            }

            CELL_NEAREST_MOSQUE -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_islamic_inspiration,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemIslamicInspirationBinding)
            }
            CELL_HAJJ -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_hajj,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemHajjBinding)
            }

            CELL_EMPTY -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_empty,
                    parent,
                    false
                )
                val viewHolder = HomeFragmentViewHolder(binding as LayoutItemEmptyBinding)
                viewHolder.emptyView?.root?.visibility = View.GONE
                return viewHolder
            }

            CELL_ROMJAN_AMOL -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_romjan_amol,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemRomjanAmolBinding)
            }

            CELL_SCHOLAR_VIDEO -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_scholar_video,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemScholarVideoBinding)
            }

            CELL_NAMAZ_VISUAL -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_hajj,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemHajjBinding)
            }

            CELL_ISLAM_PILLER -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_islam_piller,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemIslamPillerBinding)
            }

            CELL_LEARN_QURAN -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_learn_quran,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemLearnQuranBinding)
            }

            CELL_NAMES_ALLAH -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_allah_name,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemAllahNameBinding)
            }

            CELL_TASBIH -> {
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_item_tahbih_home,
                    parent,
                    false
                )
                return HomeFragmentViewHolder(binding as LayoutItemTahbihHomeBinding)
            }

            else -> throw IllegalStateException("Illegal view type")
        }

    }

    fun getItemCountCard(): Int {
        return cardAdapter.itemCount
    }

    fun paginate() {
        val old = cardAdapter.getItems()
        val new = old.plus(allahNameslist)
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    fun reload() {
        val old = cardAdapter.getItems()
        val new = allahNameslist
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
        cardAdapter.notifyDataSetChanged()
    }

    fun updateAllNameLayoutPlayPauseButton(isPlaying: Boolean) {
        when (isPlaying) {
            true -> {
                latyoutAllahNameBinding?.ivPlay?.setImageResource(R.drawable.ic_pause_round)
            }
            false -> {
                latyoutAllahNameBinding?.ivPlay?.setImageResource(R.drawable.ic_play_round)
            }
        }
    }

    fun updateTasbihCount(count: Int, userSelectCount: Int) {
        latyoutTasbihHomeBinding?.tvCount?.setText(
            TimeFormtter.getNumberByLocale(
                TimeFormtter.getNumber(count)!!
            )
        )

        latyoutTasbihHomeBinding?.progressBarCircle?.max = userSelectCount
        latyoutTasbihHomeBinding?.progressBarCircle?.progress = count
    }

    fun updateTasbihItemCount(count: Int) {

        latyoutTasbihHomeBinding?.tvTimes?.text =
            "/" + TimeFormtter.getNumberByLocale(count.toString()) + " " +
                    latyoutTasbihHomeBinding?.tvTimes?.context?.getString(R.string.txt_times)
        latyoutTasbihHomeBinding?.tvCount?.text =
            latyoutTasbihHomeBinding?.tvCount?.context?.getString(R.string.text_zero)

        latyoutTasbihHomeBinding?.progressBarCircle?.progress = 0
    }

    fun resetTasbihItemCount() {
        latyoutTasbihHomeBinding?.tvCount?.text =
            latyoutTasbihHomeBinding?.tvCount?.context?.getString(R.string.text_zero)

        latyoutTasbihHomeBinding?.progressBarCircle?.progress = 0
    }

    fun updateTasbihLayoutSoundButton(sound: Boolean) {
        when (sound) {
            true -> {
                latyoutTasbihHomeBinding?.onOffSoundIV?.setImageResource(R.drawable.ic_btn_sound_off)
            }
            false -> {
                latyoutTasbihHomeBinding?.onOffSoundIV?.setImageResource(R.drawable.ic_btn_sound)
            }
        }
    }

    fun invalidatePersonalTracker() {
        if (personalTrackerPos != -1) {
            notifyItemChanged(personalTrackerPos)
        }
    }

    fun invalidateNamesCell() {
        if (allahNameLayoutPos != -1) {
            notifyItemChanged(allahNameLayoutPos)
        }
    }
}


