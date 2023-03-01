package com.gakk.noorlibrary.ui.adapter.roja

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.BuildConfig
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R

import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.ui.fragments.DivisionSelectionCallback
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.views.TextViewNormalArabic
import com.github.eltohamy.materialhijricalendarview.CalendarDay
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

internal class RozaInformationAdapter(
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


    inner class RozaInformationViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
       var view = layoutView

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
            (TimeFormtter.getNumberByLocale(day.toString()) + " " + Noor.appContext
                ?.let {
                    TimeFormtter.getBanglaMonthName(month, it)
                } + " " + TimeFormtter.getNumberByLocale(year.toString())
                    + "   |   " + TimeFormtter.getNumberByLocale(
                (CalendarDay.from(Date()).getDay() - 1).toString()
            ) + " " + Noor.appContext?.resources!!.getStringArray(R.array.custom_months)[CalendarDay.from(
                Date()
            ).getMonth()] + " " + TimeFormtter.getNumberByLocale(
                CalendarDay.from(Date()).getYear().toString()
            ))

        return dateTxt
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RozaInformationViewHolder {
        when (viewType) {
            _HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_roza_primary_header,parent,false)
                return RozaInformationViewHolder(view)

            }
            _SEHRI_IFTER_HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_roza_sehri_ifter_header,parent,false)
                return RozaInformationViewHolder(view)

            }
            _ROZA_INFO -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_roza_info_cell,parent,false)
                return RozaInformationViewHolder(view)

            }

            _ESSENTIAL_DUA_HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_roza_dua_header,parent,false)
                return RozaInformationViewHolder(view)

            }
            else -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_roza_dua,parent,false)
                return RozaInformationViewHolder(view)

            }
        }

    }

    override fun onBindViewHolder(holder: RozaInformationViewHolder, position: Int) {
        when (holder.itemViewType) {
            _HEADER -> {

//                holder.primaryHeaderBinding?.let {


                val layoutIfterInfo = holder.view.findViewById<CardView>(R.id.layoutIfterInfo)
                val layoutSehriInfo = holder.view.findViewById<CardView>(R.id.layoutSehriInfo)
                val item = ImageFromOnline("bg_ramadan.png")
                val tvIfterOrSehriTitle:AppCompatTextView = layoutIfterInfo.findViewById(R.id.tvInfo)
                val tvTitle:AppCompatTextView = holder.view.findViewById(R.id.tvTitle)
                val  imageAlarm1:ImageView = layoutIfterInfo.findViewById(R.id.imageAlarm)
                val  imageAlarm2:ImageView = layoutSehriInfo.findViewById(R.id.imageAlarm)
                val  imageFilterView:AppCompatImageView = holder.view.findViewById(R.id.imageFilterView)
                     Glide.with(holder.itemView.context).load(item.fullImageUrl).into(imageFilterView)
                    tvTitle.setText(R.string.today_sehri_iftar_robi)
                val tvIfterOrSehriTime1 = layoutSehriInfo.findViewById<AppCompatTextView>(R.id.tvIfterOrSehriTime)
                val tvIfterOrSehriTime2 = layoutIfterInfo.findViewById<AppCompatTextView>(R.id.tvIfterOrSehriTime)
                val tvInfo = holder.view.findViewById<AppCompatTextView>(R.id.tvInfo)
                val tvInfoLarge = holder.view.findViewById<AppCompatTextView>(R.id.tvInfoLarge)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE


                val   imgSehriOrIfter: ImageView =layoutIfterInfo.findViewById(R.id.imgSehriOrIfter)

                  imgSehriOrIfter.setImageResource(R.drawable.ic_islam)
                          tvIfterOrSehriTitle.setText(R.string.ifter_time_today)

                    val todaysIfterSehri =
                        todaySehriIfterControl.getSehriIfterTimeForToday(mDisplayableSehriIfterList)

                    holder.view.context.resources.getText(R.string.evening)
                holder.view.context.resources.getText(R.string.txt_minute)

                    tvIfterOrSehriTime1.setText(
                        "${todaysIfterSehri?.sehriTimeStr} ${
                            holder.view.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"
                    )
                   tvIfterOrSehriTime2.setText(
                        "${todaysIfterSehri?.ifterTimeStr} ${
                            holder.view.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"
                    )

                    tvInfo?.visibility = VISIBLE
                   tvInfoLarge?.visibility = VISIBLE
                    imageFilterView?.visibility = VISIBLE

                    when {
                        CalenderUtil.isRamadanNow() == false && CalenderUtil.isShabanNow() == false -> {
                            //hide both tvInfo & tvInfoLarge
                           tvInfo?.visibility = GONE
                          tvInfoLarge?.visibility = GONE
                          imageFilterView?.visibility = GONE

                        }
                        CalenderUtil.isRamadanNow() == true -> {
                           tvInfo?.text = holder.view.context.getText(R.string.holy_ramadan)
                         tvInfoLarge?.text =
                                "${holder.view.context?.getText(R.string.today)} ${
                                    TimeFormtter.getNumberByLocale(
                                        CalenderUtil.getDayOfHizriMonth().toString()
                                    )
                                } ${holder.view.context.getText(R.string.cat_roja)}"
                        }

                        CalenderUtil.isShabanNow() == true -> {
                            tvInfo?.text =
                                holder.view.context.getText(R.string.time_left_till_ramadan)
                            tvInfoLarge?.text =
                                "${holder.view.context.getText(R.string.only_more)} ${
                                    TimeFormtter.getNumberByLocale(
                                        CalenderUtil.daysLeftTillRamadan().toString()
                                    )
                                } ${holder.view.context.getText(R.string.day_meaning_v2)}"
                        }


                    }

                    if (fromMalaysia) {
                        //hide both tvInfo & tvInfoLarge
                       tvInfo?.visibility = GONE
                        tvInfoLarge?.visibility = GONE
                        imageFilterView?.visibility = GONE
                    }
                    imageAlarm1.setOnClickListener { it ->

                        gotoDefaultAlarm(
                            todaysIfterSehri?.ifterTimeStr!!,
                            it.context,
                            "Ifter time",
                            0
                        )
                    }

                    imageAlarm2.setOnClickListener { it ->
                        gotoDefaultAlarm(
                            todaysIfterSehri?.sehriTimeStr!!,
                            it.context,
                            "Sehri time",
                            1
                        )
                    }
                }
           // }

            _SEHRI_IFTER_HEADER -> {
                val tabRamadanPeriod:TabLayout = holder.view.findViewById(R.id.tabRamadanPeriod)
                val tvDate:AppCompatTextView =holder.view.findViewById(R.id.tvDate)
                tabRamadanPeriod?.selectTab(tabRamadanPeriod.getTabAt(mPeriodControl.mSelectedPeriod))
                tvDate.setText(getSehriIfterHeaderFormattedDate())
                val tvTitle:AppCompatTextView =holder.view.findViewById(R.id.tvTitle)
                  tvTitle?.setText(R.string.today_sehri_iftar_time_table_robi)

             tabRamadanPeriod?.addOnTabSelectedListener(
                    object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {

                            mPeriodControl.updateSelectedPeriod(tabRamadanPeriod?.selectedTabPosition!!)
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
                   tabRamadanPeriod?.visibility =
                            VISIBLE
                    }
                    false ->tabRamadanPeriod?.visibility =
                        GONE
                }


            }
            _ROZA_INFO -> {
                if (position == 2) {
                    val tvDay = holder.view.findViewById<AppCompatTextView>(R.id.tvDay)

                    when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                        true -> tvDay?.setText(
                            Noor.appContext?.resources?.getText(
                                R.string.ramadan
                            )
                        )
                        else -> tvDay?.setText(
                            Noor.appContext?.resources?.getText(
                                R.string.date
                            )
                        )
                    }


                } else {
                    val tvDay = holder.view.findViewById<AppCompatTextView>(R.id.tvDay)
                    val scrim = holder.view.findViewById<LinearLayout>(R.id.scrim)
                    val tvDate = holder.view.findViewById<AppCompatTextView>(R.id.tvDate)
                    val tvLastTimeOfSehri = holder.view.findViewById<AppCompatTextView>(R.id.tvLastTimeOfSehri)
                    val tvTimeOfIftari = holder.view.findViewById<AppCompatTextView>(R.id.tvTimeOfIftari)
                    val pos = position - 3 + mPeriodControl.mSelectedPeriod * 10

                    if (mDisplayableSehriIfterList.size > 0) {
                        val sehriIfterTime = mDisplayableSehriIfterList.get(pos)
                        when (sehriIfterTime.isToday) {
                            true -> {
                             scrim?.visibility = VISIBLE
                            }
                            else -> {
                              scrim?.visibility = GONE
                            }
                        }
                        if (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                          tvDay?.setText(sehriIfterTime.dayOfHizriMonth)
                        } else {
                           tvDay?.setText(sehriIfterTime.dayOfGeorgianMonth)
                        }

                       tvDate?.setText(sehriIfterTime.dayOfWeek)
                       tvLastTimeOfSehri?.setText(sehriIfterTime.sehriTimeStr)
                       tvTimeOfIftari?.setText(sehriIfterTime.ifterTimeStr)
                    }

                }
            }
            _DUA_INFO -> {

//                holder.duaBinding?.let { binding ->
                    mDuaList?.let {
                        var pos = if (fromMalaysia) {
                            position - mDisplayableSehriIfterList.size - 3
                        } else {
                            position - 14
                        }
                        var literature = it.get(pos)
//                        binding.dua = literature
                        val  tvDuaTitle:AppCompatTextView = holder.view.findViewById(R.id.tvDuaTitle)
                        val  tvDuaDesc:AppCompatTextView = holder.view.findViewById(R.id.tvDuaDesc)
                        val  tvDesArabic: TextViewNormalArabic = holder.view.findViewById(R.id.tvDesArabic)
                        val  tvDuaMeaning:AppCompatTextView = holder.view.findViewById(R.id.tvDuaMeaning)
                        val btnToggleCollapse:ImageView = holder.view.findViewById(R.id.btnToggleCollapse)
                        tvDuaTitle.text= literature.title
                        tvDesArabic.text = literature.textInArabic
                        tvDuaDesc.text = literature.text
                        tvDuaMeaning.text = literature.pronunciation
                       btnToggleCollapse?.handleClickEvent {
                            when (tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                   btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                       tvDuaDesc?.visibility = GONE
                                       tvDesArabic?.visibility = GONE
                                       tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                   btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                    tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                           tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                    tvDuaTitle?.handleClickEvent {
                            when (tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                 btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                   tvDuaDesc?.visibility = GONE
                                  tvDesArabic?.visibility = GONE
                               tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                   btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                    tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                        tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }

                      holder.itemView.handleClickEvent {
                            when (tvDuaDesc?.visibility) {
                                VISIBLE -> {
                                    btnToggleCollapse?.setImageResource(R.drawable.ic_plus)
                                   tvDuaDesc?.visibility = GONE
                                   tvDesArabic?.visibility = GONE
                             tvDuaMeaning?.visibility = GONE
                                }
                                GONE -> {
                                   btnToggleCollapse?.setImageResource(R.drawable.ic_minus)
                                  tvDuaDesc?.visibility = VISIBLE
                                    literature.textInArabic?.let {
                                        if (it.isNotEmpty()) {
                                          tvDesArabic?.visibility = VISIBLE
                                        }
                                    }
                                    literature.pronunciation?.let {
                                        if (it.isNotEmpty()) {
                                            tvDuaMeaning?.visibility = VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                    }


               // }

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