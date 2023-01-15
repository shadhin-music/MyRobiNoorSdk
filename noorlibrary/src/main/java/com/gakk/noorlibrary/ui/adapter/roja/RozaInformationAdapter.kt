package com.gakk.noorlibrary.ui.adapter.roja

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.databinding.*
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.ui.fragments.DivisionSelectionCallback
import com.gakk.noorlibrary.util.*
import com.github.eltohamy.materialhijricalendarview.CalendarDay
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class RozaInformationAdapter(
    callBack: DivisionSelectionCallback?,
    ramadanSehriIfterTimes: MutableList<IfterAndSehriTime>,
    nextTenDaysSehriIfterTime: MutableList<IfterAndSehriTime>,
    duaList: MutableList<Literature>?,
    val fromMalaysia: Boolean
) :
    RecyclerView.Adapter<RozaInformationAdapter.RozaInformationViewHolder>() {

    val _HEADER = 0
    val _SEHRI_IFTER_HEADER = 1
    val _ROZA_INFO = 2
    val _ESSENTIAL_DUA_HEADER = 3
    val _DUA_INFO = 4

    val mCallBack = callBack
    val mRamadanSehriIfterTimes = ramadanSehriIfterTimes
    val mNextTenDaysSehriIfterTimes = nextTenDaysSehriIfterTime
    val mDisplayableSehriIfterList: MutableList<IfterAndSehriTime>
    val mListControl: DisplayableSehriIfterListControl
    val todaySehriIfterControl: TodaySehriIfterControl
    var mPeriodControl: RamadanPeriodControl
    val mDuaList = duaList
    var duaItemCount: Int


    init {


        mPeriodControl = RamadanPeriodControl()
        mListControl = DisplayableSehriIfterListControl()
        mDisplayableSehriIfterList = mListControl.getDisplayableList()
        todaySehriIfterControl = TodaySehriIfterControl()
        duaItemCount = mDuaList?.size ?: 0
    }


    inner class RozaInformationViewHolder : RecyclerView.ViewHolder {

        var primaryHeaderBinding: LayoutRozaPrimaryHeaderBinding? = null

        constructor(binding: LayoutRozaPrimaryHeaderBinding) : super(binding.root) {
            primaryHeaderBinding = binding
            primaryHeaderBinding.let {
                it?.let {
                    it.layoutDivisionContainer.handleClickEvent {
                        mCallBack?.showDivisionListAlert(it)
                    }
                }


            }

        }

        var sehriIfterHeaderBindingBinding: LayoutRozaSehriIfterHeaderBinding? = null

        constructor(binding: LayoutRozaSehriIfterHeaderBinding) : super(binding.root) {
            sehriIfterHeaderBindingBinding = binding
            sehriIfterHeaderBindingBinding?.let {
                it.tabRamadanPeriod?.selectTab(it.tabRamadanPeriod.getTabAt(mPeriodControl.mSelectedPeriod))
                it.tvDate.setText(getSehriIfterHeaderFormattedDate())
            }
        }

        fun getSehriIfterHeaderFormattedDate(): String {
            val cal: Calendar
            var year = 0
            var month = 0
            var day = 0

            try {
                cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                year = cal.get(Calendar.YEAR)
                month = cal.get(Calendar.MONTH)
                day = cal.get(Calendar.DATE)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val calHijri = Calendar.getInstance()
            calHijri.time = Date()
            calHijri.add(Calendar.DAY_OF_MONTH, -1)
            var dateTxt = ""
            dateTxt =
                (TimeFormtter.getNumberByLocale(day.toString()) + " " + BaseApplication.getAppContext()
                    ?.let {
                        TimeFormtter.getBanglaMonthName(month, it)
                    } + " " + TimeFormtter.getNumberByLocale(year.toString())
                        + "   |   " + TimeFormtter.getNumberByLocale(
                    (CalendarDay.from(Date()).getDay() - 1).toString()
                ) + " " + BaseApplication.getAppContext()!!.resources.getStringArray(R.array.custom_months)[CalendarDay.from(
                    Date()
                ).getMonth()] + " " + TimeFormtter.getNumberByLocale(
                    CalendarDay.from(Date()).getYear().toString()
                ))

            return dateTxt
        }

        var rozaInfoCellBinding: LayoutRozaInfoCellBinding? = null

        constructor(binding: LayoutRozaInfoCellBinding) : super(binding.root) {
            rozaInfoCellBinding = binding
        }

        var duaHeaderBinding: LayoutRozaDuaHeaderBinding? = null

        @SuppressLint("MissingPermission")
        constructor(binding: LayoutRozaDuaHeaderBinding) : super(binding.root) {
            duaHeaderBinding = binding
        }

        var duaBinding: LayoutRozaDuaBinding? = null

        constructor(binding: LayoutRozaDuaBinding) : super(binding.root) {
            duaBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RozaInformationViewHolder {
        when (viewType) {
            _HEADER -> {
                val binding: LayoutRozaPrimaryHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_roza_primary_header,
                    parent,
                    false
                )
                return RozaInformationViewHolder(binding)
            }
            _SEHRI_IFTER_HEADER -> {
                val binding: LayoutRozaSehriIfterHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_roza_sehri_ifter_header,
                    parent,
                    false
                )
                return RozaInformationViewHolder(binding)
            }
            _ROZA_INFO -> {
                val binding: LayoutRozaInfoCellBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_roza_info_cell,
                    parent,
                    false
                )
                return RozaInformationViewHolder(binding)
            }

            _ESSENTIAL_DUA_HEADER -> {
                val binding: LayoutRozaDuaHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_roza_dua_header,
                    parent,
                    false
                )
                return RozaInformationViewHolder(binding)
            }
            else -> {
                val binding: LayoutRozaDuaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_roza_dua,
                    parent,
                    false
                )
                return RozaInformationViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RozaInformationViewHolder, position: Int) {
        when (holder.itemViewType) {
            _HEADER -> {

                holder.primaryHeaderBinding?.let {
                    it.item = ImageFromOnline("bg_ramadan.png")

                    it.tvTitle.setText(R.string.today_sehri_iftar_robi)

                    it.layoutIfterInfo.imgSehriOrIfter.setImageResource(R.drawable.ic_islam)
                    it.layoutIfterInfo.tvIfterOrSehriTitle.setText(R.string.ifter_time_today)

                    val todaysIfterSehri =
                        todaySehriIfterControl.getSehriIfterTimeForToday(mDisplayableSehriIfterList)

                    it.root.context.resources.getText(R.string.evening)
                    it.root.context.resources.getText(R.string.txt_minute)

                    it.layoutSehriInfo.tvIfterOrSehriTime.setText(
                        "${todaysIfterSehri?.sehriTimeStr} ${
                            it.root.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"
                    )
                    it.layoutIfterInfo.tvIfterOrSehriTime.setText(
                        "${todaysIfterSehri?.ifterTimeStr} ${
                            it.root.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"
                    )

                    it?.tvInfo?.visibility = VISIBLE
                    it?.tvInfoLarge?.visibility = VISIBLE
                    it?.imageFilterView?.visibility = VISIBLE

                    when {
                        CalenderUtil.isRamadanNow() == false && CalenderUtil.isShabanNow() == false -> {
                            //hide both tvInfo & tvInfoLarge
                            it?.tvInfo?.visibility = GONE
                            it?.tvInfoLarge?.visibility = GONE
                            it?.imageFilterView?.visibility = GONE

                        }
                        CalenderUtil.isRamadanNow() == true -> {
                            it?.tvInfo?.text = it?.root?.context?.getText(R.string.holy_ramadan)
                            it?.tvInfoLarge?.text =
                                "${it?.root?.context?.getText(R.string.today)} ${
                                    TimeFormtter.getNumberByLocale(
                                        CalenderUtil.getDayOfHizriMonth().toString()
                                    )
                                } ${it?.root?.context?.getText(R.string.cat_roja)}"
                        }

                        CalenderUtil.isShabanNow() == true -> {
                            it?.tvInfo?.text =
                                it?.root?.context?.getText(R.string.time_left_till_ramadan)
                            it?.tvInfoLarge?.text =
                                "${it?.root?.context?.getText(R.string.only_more)} ${
                                    TimeFormtter.getNumberByLocale(
                                        CalenderUtil.daysLeftTillRamadan().toString()
                                    )
                                } ${it?.root?.context?.getText(R.string.day_meaning_v2)}"
                        }


                    }

                    if (fromMalaysia) {
                        //hide both tvInfo & tvInfoLarge
                        it?.tvInfo?.visibility = GONE
                        it?.tvInfoLarge?.visibility = GONE
                        it?.imageFilterView?.visibility = GONE
                    }
                    it.layoutIfterInfo.imageAlarm.setOnClickListener { it ->

                        gotoDefaultAlarm(
                            todaysIfterSehri?.ifterTimeStr!!,
                            it.context,
                            "Ifter time",
                            0
                        )
                    }

                    it.layoutSehriInfo.imageAlarm.setOnClickListener { it ->
                        gotoDefaultAlarm(
                            todaysIfterSehri?.sehriTimeStr!!,
                            it.context,
                            "Sehri time",
                            1
                        )
                    }
                }
            }

            _SEHRI_IFTER_HEADER -> {

                holder.sehriIfterHeaderBindingBinding?.tvTitle?.setText(R.string.today_sehri_iftar_time_table_robi)

                holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.addOnTabSelectedListener(
                    object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {

                            mPeriodControl.updateSelectedPeriod(holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.selectedTabPosition!!)
                            notifyItemRangeChanged(3, 10)
                            // Log.i("MSELECTEDPOS",holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.selectedTabPosition.toString())
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {

                        }

                        override fun onTabReselected(tab: TabLayout.Tab?) {

                        }

                    })

                when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                    true -> {
                        holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.visibility =
                            VISIBLE
                    }
                    false -> holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.visibility =
                        GONE
                }


            }
            _ROZA_INFO -> {
                if (position == 2) {

                    when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                        true -> holder.rozaInfoCellBinding?.tvDay?.setText(
                            BaseApplication.getAppContext().resources.getText(
                                R.string.ramadan
                            )
                        )
                        else -> holder.rozaInfoCellBinding?.tvDay?.setText(
                            BaseApplication.getAppContext().resources.getText(
                                R.string.date
                            )
                        )
                    }


                } else {

                    val pos = position - 3 + mPeriodControl.mSelectedPeriod * 10

                    if (mDisplayableSehriIfterList.size > 0) {
                        val sehriIfterTime = mDisplayableSehriIfterList.get(pos)
                        when (sehriIfterTime.isToday) {
                            true -> {
                                holder.rozaInfoCellBinding?.scrim?.visibility = VISIBLE
                            }
                            else -> {
                                holder.rozaInfoCellBinding?.scrim?.visibility = GONE
                            }
                        }
                        if (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                            holder.rozaInfoCellBinding?.tvDay?.setText(sehriIfterTime.dayOfHizriMonth)
                        } else {
                            holder.rozaInfoCellBinding?.tvDay?.setText(sehriIfterTime.dayOfGeorgianMonth)
                        }

                        holder.rozaInfoCellBinding?.tvDate?.setText(sehriIfterTime.dayOfWeek)
                        holder.rozaInfoCellBinding?.tvLastTimeOfSehri?.setText(sehriIfterTime.sehriTimeStr)
                        holder.rozaInfoCellBinding?.tvTimeOfIftari?.setText(sehriIfterTime.ifterTimeStr)
                    }

                }
            }
            _DUA_INFO -> {
                holder.duaBinding?.let { binding ->
                    mDuaList?.let {
                        var pos = if (fromMalaysia) {
                            position - mDisplayableSehriIfterList.size - 3
                        } else {
                            position - 14
                        }
                        var literature = it.get(pos)
                        binding.dua = literature


                        holder.duaBinding?.btnToggleCollapse?.handleClickEvent {
                            when (holder.duaBinding?.tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = GONE
                                    holder.duaBinding?.tvDesArabic?.visibility = GONE
                                    holder.duaBinding?.tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                        holder.duaBinding?.tvDuaTitle?.handleClickEvent {
                            when (holder.duaBinding?.tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = GONE
                                    holder.duaBinding?.tvDesArabic?.visibility = GONE
                                    holder.duaBinding?.tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }

                        holder.duaBinding?.root?.handleClickEvent {
                            when (holder.duaBinding?.tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = GONE
                                    holder.duaBinding?.tvDesArabic?.visibility = GONE
                                    holder.duaBinding?.tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                    holder.duaBinding?.btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                    holder.duaBinding?.tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            holder.duaBinding?.tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                    }


                }

            }
        }
    }

    private fun gotoDefaultAlarm(
        alarmTime: String,
        context: Context,
        message: String,
        alarmType: Int
    ) {
        val sdf =
            SimpleDateFormat(
                "HH:mm",
                Locale.ENGLISH
            ) // or "hh:mm" for 12 hour format

        val date: Date = sdf.parse(alarmTime)

        val hour = date.hours

        val min = date.minutes

        val i = Intent(AlarmClock.ACTION_SET_ALARM)

        i.putExtra(AlarmClock.EXTRA_MESSAGE, message)
        if (alarmType == 0) {
            i.putExtra(AlarmClock.EXTRA_HOUR, 12 + hour)
        } else {
            i.putExtra(AlarmClock.EXTRA_HOUR, hour)
        }

        i.putExtra(AlarmClock.EXTRA_MINUTES, min)
        context.startActivity(i)
    }

    override fun getItemCount(): Int {
        if (fromMalaysia) {
            return 3 + mDisplayableSehriIfterList.size + duaItemCount
        }
        return 14 + duaItemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (fromMalaysia) {
            return when (position) {
                0 -> _HEADER
                1 -> _SEHRI_IFTER_HEADER
                in 2..mDisplayableSehriIfterList.size + 2 -> _ROZA_INFO
                mDisplayableSehriIfterList.size + 3 -> _ESSENTIAL_DUA_HEADER
                else -> _DUA_INFO
            }
        }
        return when (position) {
            0 -> _HEADER
            1 -> _SEHRI_IFTER_HEADER
            in 2..12 -> _ROZA_INFO
            13 -> _ESSENTIAL_DUA_HEADER
            else -> _DUA_INFO
        }
    }


    inner class DisplayableSehriIfterListControl {
        fun getDisplayableList(): MutableList<IfterAndSehriTime> {
            when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                true -> return mRamadanSehriIfterTimes
                else -> return mNextTenDaysSehriIfterTimes
            }
        }
    }


    inner class TodaySehriIfterControl {

        //returns IfterSehriObject from given list for today
        fun getSehriIfterTimeForToday(list: MutableList<IfterAndSehriTime>): IfterAndSehriTime? {
            for (item in list) {
                if (item.isToday)
                    return item
            }
            return null
        }
    }

    /**
     * Handles & Updates Current Ramadan Periods
     * Periods:
     *     i)Rahmat:1st 10 days of Ramadan
     *     ii)Magfirat:2nd 10 days of Ramadan
     *     iii)Nazat:last 10 days of Ramadan
     */
    inner class RamadanPeriodControl {

        var mSelectedPeriod: Int

        init {
            when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                true -> {
                    var dateOfRamadan = CalenderUtil.getDayOfHizriMonth()
                    when {
                        //Rahmat
                        dateOfRamadan <= 10 -> mSelectedPeriod = 0
                        //Magfirat
                        dateOfRamadan > 10 && dateOfRamadan < 21 -> mSelectedPeriod = 1
                        //Nazat
                        else -> mSelectedPeriod = 2

                    }
                }

                else -> {
                    mSelectedPeriod = 0
                }
            }

        }

        fun updateSelectedPeriod(period: Int) {
            mSelectedPeriod = period
        }
    }

}