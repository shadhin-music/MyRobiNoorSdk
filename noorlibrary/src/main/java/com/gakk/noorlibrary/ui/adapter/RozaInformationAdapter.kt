package com.gakk.noorlibrary.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.roza.Data
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.ui.fragments.DivisionSelectionCallback
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.views.TextViewNormalArabic
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*


internal class RozaInformationAdapter(
    callBack: DivisionSelectionCallback?,
    ramadanSehriIfterTimes: MutableList<IfterAndSehriTime>,
    nextTenDaysSehriIfterTime: MutableList<IfterAndSehriTime>,
    duaList: MutableList<Literature>?,
    list2: MutableList<Data>,
    val fromMalaysia: Boolean
) : RecyclerView.Adapter<RozaInformationAdapter.RozaInformationViewHolder>() {

    val _HEADER = 0
    val _SEHRI_IFTER_HEADER = 1
    val _ROZA_INFO = 2
    val _ESSENTIAL_DUA_HEADER = 3
    val _DUA_INFO = 4

    val mCallBack = callBack
    var mRamadanSehriIfterTimesFromAPI = list2
    var mNextTenDaysSehriIfterTimesFromAPI = list2
    var mDisplayableSehriIfterListFromAPI: MutableList<Data>
    var mListControlFromAPI: DisplayableSehriIfterListControlFromAPI
    var todaySehriIfterControlFromAPI: TodaySehriIfterControlFromAPI
    val mRamadanSehriIfterTimes = ramadanSehriIfterTimes
    val mNextTenDaysSehriIfterTimes = nextTenDaysSehriIfterTime
    var mDisplayableSehriIfterList: MutableList<IfterAndSehriTime>
    var mListControl: DisplayableSehriIfterListControl
    var todaySehriIfterControl: TodaySehriIfterControl
    var mPeriodControl: RamadanPeriodControl
    var mDuaList = duaList
    var duaItemCount: Int
    var selectedDivision = "Dhaka"


    init {
        mPeriodControl = RamadanPeriodControl()
        mListControlFromAPI = DisplayableSehriIfterListControlFromAPI()
        mListControl = DisplayableSehriIfterListControl()
        mDisplayableSehriIfterListFromAPI = mListControlFromAPI.getDisplayableListFromAPI()
        mDisplayableSehriIfterList = mListControl.getDisplayableList()
        todaySehriIfterControl = TodaySehriIfterControl()
        todaySehriIfterControlFromAPI = TodaySehriIfterControlFromAPI()
        duaItemCount = mDuaList?.size ?: 0
    }

    fun initData() {
        mPeriodControl = RamadanPeriodControl()
        mListControlFromAPI = DisplayableSehriIfterListControlFromAPI()
        mListControl = DisplayableSehriIfterListControl()
        mDisplayableSehriIfterListFromAPI = mListControlFromAPI.getDisplayableListFromAPI()
        mDisplayableSehriIfterList = mListControl.getDisplayableList()
        todaySehriIfterControl = TodaySehriIfterControl()
        todaySehriIfterControlFromAPI = TodaySehriIfterControlFromAPI()
        duaItemCount = mDuaList?.size ?: 0
    }



    inner class RozaInformationViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var view = layoutView

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

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RozaInformationViewHolder, position: Int) {
        when (holder.itemViewType) {
            _HEADER -> {


                val tvIfterOrSehriTitle: AppCompatTextView = holder.view.findViewById(R.id.tvInfo)
                val tvTitle: AppCompatTextView = holder.view.findViewById(R.id.tvTitle)
                val  imageAlarm: ImageView = holder.view.findViewById(R.id.imageAlarm)
                val  imageFilterView: AppCompatImageView = holder.view.findViewById(R.id.imageFilterView)
               // Glide.with(holder.itemView.context).load(item.fullImageUrl).into(imageFilterView)
                tvTitle.setText(R.string.today_sehri_iftar_robi)
                val tvIfterOrSehriTime = holder.view.findViewById<AppCompatTextView>(R.id.tvIfterOrSehriTime)
                val tvInfo = holder.view.findViewById<AppCompatTextView>(R.id.tvInfo)
                val tvInfoLarge = holder.view.findViewById<AppCompatTextView>(R.id.tvInfoLarge)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
                val   imgSehriOrIfter: ImageView = holder.view.findViewById(R.id.imgSehriOrIfter)
               //     it.item = ImageFromOnline("bg_ramadan.png")

                 tvTitle.setText(R.string.today_sehri_iftar_robi)

                   imgSehriOrIfter.setImageResource(R.drawable.ic_islam)
                    tvIfterOrSehriTitle.setText(R.string.ifter_time_today)
                   // tvDivision.text = selectedDivision
                    var todaysIfterSehri = todaySehriIfterControl.getSehriIfterTimeForToday(mDisplayableSehriIfterList)


                    holder.itemView.context.resources.getText(R.string.evening)
                    holder.itemView.context.resources.getText(R.string.txt_minute)

                    // if(CalenderUtil.isRamadanNow()== false) {
                    tvIfterOrSehriTime.setText(
                        "${todaysIfterSehri?.sehriTimeStr} ${
                            holder.itemView.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"

                    )
                    Log.d("TIME", "Time123: " + todaysIfterSehri?.sehriTimeStr)
                    tvIfterOrSehriTime.setText(
                        "${todaysIfterSehri?.ifterTimeStr} ${
                            holder.itemView.context.resources.getText(
                                R.string.txt_minute
                            )
                        }"
                    )
                    // }
                    tvInfo?.visibility = VISIBLE
                  tvInfoLarge?.visibility = VISIBLE
                    imageFilterView?.visibility = VISIBLE
                    //This will work after ramadan 2022
//                    when {
//                        CalenderUtil.isRamadanNow() == false && CalenderUtil.isShabanNow() == false -> {
//                            //hide both tvInfo & tvInfoLarge
//                            it?.tvInfo?.visibility = GONE
//                            it?.tvInfoLarge?.visibility = GONE
//                            it?.imageFilterView?.visibility = GONE
//
//
//                        }
//                        CalenderUtil.isRamadanNow() == true -> {
//                            var todaysIfterSehriFromAPI =
//                                todaySehriIfterControlFromAPI.getSehriIfterTimeForTodayFromAPI(mDisplayableSehriIfterListFromAPI)
//                            it?.tvInfo?.text = it?.root?.context?.getText(R.string.holy_ramadan)
//                            it?.tvInfoLarge?.text =
//                                "${it?.root?.context?.getText(R.string.today)} ${
//                                    TimeFormtter.getNumberByLocale(
//                                        CalenderUtil.getDayOfHizriMonth().toString()
//                                    )
//                                } ${it?.root?.context?.getText(R.string.cat_roja)}"
//
//                            it.layoutSehriInfo.tvIfterOrSehriTime.setText(
//                                "${todaysIfterSehriFromAPI?.sehri} ${
//                                    it.root.context.resources.getText(
//                                        R.string.txt_minute
//                                    )
//                                }"
//                            )
//                            it.layoutIfterInfo.tvIfterOrSehriTime.setText(
//                                "${todaysIfterSehriFromAPI?.iftar} ${
//                                    it.root.context.resources.getText(
//                                        R.string.txt_minute
//                                    )
//                                }"
//                            )
//                        }
//
//                        CalenderUtil.isShabanNow() == true -> {
//                            it?.tvInfo?.text =
//                                it?.root?.context?.getText(R.string.time_left_till_ramadan)
//                            it?.tvInfoLarge?.text =
//                                "${it?.root?.context?.getText(R.string.only_more)} ${
//                                    TimeFormtter.getNumberByLocale(
//                                        CalenderUtil.daysLeftTillRamadanFor2022().toString()
//                                    )
//                                } ${it?.root?.context?.getText(R.string.day_meaning_v2)}"
//
//                        }
//
//
//                    }


                    if (CalenderUtil.isRamadanNow() == false && CalenderUtil.isShabanNow() == false) {
//                            //hide both tvInfo & tvInfoLarge
                     tvInfo?.visibility = GONE
                      tvInfoLarge?.visibility = GONE
                      imageFilterView?.visibility = GONE


                    }
                    val date = Calendar.getInstance().time
                    val formatter =
                        SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
                    val formatedDate = formatter.format(date)
                    if (CalenderUtil.isRamadanNow() == true) {
                        val todaysIfterSehriFromAPI =
                            todaySehriIfterControlFromAPI.getSehriIfterTimeForTodayFromAPI(
                                mDisplayableSehriIfterListFromAPI
                            )
                        tvInfo?.text =  holder.itemView.context.getText(R.string.holy_ramadan)
                       tvInfoLarge?.visibility = GONE
//                            it?.tvInfoLarge?.text =
//                                "${it?.root?.context?.getText(R.string.today)} ${
//                                    TimeFormtter.getNumberByLocale(
//                                        CalenderUtil.getDayOfHizriMonth().toString()
//                                    )
//                                } ${it?.root?.context?.getText(R.string.cat_roja)}"

                        tvIfterOrSehriTime.setText(
                            "${todaysIfterSehriFromAPI?.sehriTimeStr1} ${
                                holder.itemView.context.resources.getText(
                                    R.string.txt_minute
                                )
                            }"
                        )
                     tvIfterOrSehriTime.setText(
                            "${todaysIfterSehriFromAPI?.ifterTimeStr1} ${
                                holder.itemView.context.resources.getText(
                                    R.string.txt_minute
                                )
                            }"
                        )
                    }
                   imageAlarm.setOnClickListener { it ->
                        val todaysIfterSehriFromAPI =
                            todaySehriIfterControlFromAPI.getSehriIfterTimeForTodayFromAPI(
                                mDisplayableSehriIfterListFromAPI
                            )
                        val hm = todaysIfterSehriFromAPI?.ifterTimeStr1

                        val sdf: SimpleDateFormat =
                            SimpleDateFormat("HH:mm") // or "hh:mm" for 12 hour format

                        val date: Date = sdf.parse(hm)

                        val hour = date.hours

                        val min = date.minutes

                        val i = Intent(AlarmClock.ACTION_SET_ALARM)

                        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Ifter time")
                        i.putExtra(AlarmClock.EXTRA_HOUR, 12 + hour)
                        i.putExtra(AlarmClock.EXTRA_MINUTES, min)
                        it.context.startActivity(i)
                        Log.d("Time", "Time: " + hour)
                        Log.d("Time", "Time: " + min)
                        Log.d("Time", "Time: " + hm)
                    }
                   imageAlarm.setOnClickListener { it ->
                        val todaysIfterSehriFromAPI =
                            todaySehriIfterControlFromAPI.getSehriIfterTimeForTodayFromAPI(
                                mDisplayableSehriIfterListFromAPI
                            )
                        val hm = todaysIfterSehriFromAPI?.sehriTimeStr1

                        val sdf: SimpleDateFormat =
                            SimpleDateFormat("HH:mm") // or "hh:mm" for 12 hour format

                        val date: Date = sdf.parse(hm)

                        val hour = date.hours

                        val min = date.minutes // int

                        val i = Intent(AlarmClock.ACTION_SET_ALARM)

                        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Sehri time")
                        i.putExtra(AlarmClock.EXTRA_HOUR, hour)
                        i.putExtra(AlarmClock.EXTRA_MINUTES, min)

                        it.context.startActivity(i)
                        Log.d("Time", "Time: " + hour)
                        Log.d("Time", "Time: " + min)
                        Log.d("Time", "Time: " + hm)

                    }
                    if (CalenderUtil.isShabanNow() == true) {
                       tvInfo?.text =
                           holder.itemView.context.getText(R.string.time_left_till_ramadan)
                        tvInfoLarge?.text =
                            "${ holder.itemView.context.getText(R.string.only_more)} ${
                                TimeFormtter.getNumberByLocale(
                                    CalenderUtil.daysLeftTillRamadanFor2022().toString()
                                )
                            } ${ holder.itemView.context.getText(R.string.day_meaning_v2)}"

                    }
                    if (fromMalaysia) {
                        //hide both tvInfo & tvInfoLarge
                        tvInfo?.visibility = GONE
                        tvInfoLarge?.visibility = GONE
                     imageFilterView?.visibility = GONE
                    }


            }

            _SEHRI_IFTER_HEADER -> {
                val tvTitle:AppCompatTextView =holder.view.findViewById(R.id.tvTitle)
                val tabRamadanPeriod:TabLayout = holder.view.findViewById(R.id.tabRamadanPeriod)
                val tvDate:AppCompatTextView =holder.view.findViewById(R.id.tvDate)
                tabRamadanPeriod?.selectTab(tabRamadanPeriod.getTabAt(mPeriodControl.mSelectedPeriod))

                tvTitle?.setText(R.string.today_sehri_iftar_time_table_robi)
                      tabRamadanPeriod?.addOnTabSelectedListener(
                    object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {

                            mPeriodControl.updateSelectedPeriod(tabRamadanPeriod?.selectedTabPosition!!)
                            notifyItemRangeChanged(3, 10)
                            Log.e(
                                "MSELECTEDPOS",
                               tabRamadanPeriod?.selectedTabPosition.toString()
                            )
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {

                        }

                        override fun onTabReselected(tab: TabLayout.Tab?) {

                        }

                    })
                val date = Calendar.getInstance().time
                val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
                val formatedDate = formatter.format(date)
                //  if(formatedDate =="2022-04-03"&& !fromMalaysia){
                when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                    true -> {
                      tabRamadanPeriod?.visibility =
                            VISIBLE
                    }
                    false ->tabRamadanPeriod?.visibility =
                        GONE
                }
//                else{
//                    holder.sehriIfterHeaderBindingBinding?.tabRamadanPeriod?.visibility =
//                        GONE
//                }


            }
            _ROZA_INFO -> {
                if (position == 2) {
                    val tvDay = holder.view.findViewById<AppCompatTextView>(R.id.tvDay)

                    val tvDate = holder.view.findViewById<AppCompatTextView>(R.id.tvDate)
                    val tvLastTimeOfSehri = holder.view.findViewById<AppCompatTextView>(R.id.tvLastTimeOfSehri)
                    val tvTimeOfIftari = holder.view.findViewById<AppCompatTextView>(R.id.tvTimeOfIftari)
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
                   tvDate?.text =
                        Noor.appContext?.resources?.getText(
                            R.string.day
                        )
                  tvLastTimeOfSehri?.text =
                        Noor.appContext?.resources?.getText(
                            R.string.last_sehri_time_today
                        )
                   tvTimeOfIftari?.text =
                        Noor.appContext?.resources?.getText(
                            R.string.ifter_time_today
                        )

                } else {
                    val tvDay = holder.view.findViewById<AppCompatTextView>(R.id.tvDay)
                    val tvLastTimeOfSehri = holder.view.findViewById<AppCompatTextView>(R.id.tvLastTimeOfSehri)
                    val tvTimeOfIftari = holder.view.findViewById<AppCompatTextView>(R.id.tvTimeOfIftari)
                    val tvDate = holder.view.findViewById<AppCompatTextView>(R.id.tvDate)
                    val scrim = holder.view.findViewById<LinearLayout>(R.id.scrim)
                    var pos = position - 3 + mPeriodControl.mSelectedPeriod * 10
                    var sehriIfterTime = mDisplayableSehriIfterList.get(pos)
//                    var todaysIfterSehriFromAPI =
//                        todaySehriIfterControlFromAPI.getSehriIfterTimeForTodayFromAPI(mDisplayableSehriIfterListFromAPI)
                    var sehriIfterTimeForRamadan = mDisplayableSehriIfterListFromAPI.get(pos)
                    // var sehriIfterTimeForRamadan = mDisplayableSehriIfterListFromAPI.get()
                    //Log.e("TAG", "Position123: "+ mDisplayableSehriIfterListFromAPI.size)
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
                        ///       var sehriIfterTimeForRamadan = mDisplayableSehriIfterListFromAPI.get(pos)
                        when (sehriIfterTimeForRamadan?.isToday) {
                            true -> {
                             scrim?.visibility = VISIBLE
                            }
                            else -> {
                                scrim?.visibility = GONE
                            }
                        }

                      tvDate?.setText(sehriIfterTimeForRamadan.dayOfWeek)
                       tvLastTimeOfSehri?.setText(
                            sehriIfterTimeForRamadan?.sehriTimeStr1
                        )
                      tvTimeOfIftari?.setText(sehriIfterTimeForRamadan?.ifterTimeStr1)
                    } else {
                       tvDay?.setText(sehriIfterTime.dayOfGeorgianMonth)

                     tvDate?.setText(sehriIfterTime.dayOfWeek)
                        tvLastTimeOfSehri?.setText(sehriIfterTime.sehriTimeStr)
                    tvTimeOfIftari?.setText(sehriIfterTime.ifterTimeStr)
                    }

                    /*holder.rozaInfoCellBinding?.tvDate?.setText(sehriIfterTime.dayOfWeek)
                    holder.rozaInfoCellBinding?.tvLastTimeOfSehri?.setText(sehriIfterTime.sehriTimeStr)
                    holder.rozaInfoCellBinding?.tvTimeOfIftari?.setText(sehriIfterTime.ifterTimeStr)*/
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
                       // binding.dua = literature
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


                //}

            }


        }
    }

    override fun getItemCount(): Int {
        if (fromMalaysia) {
            return 3 + mDisplayableSehriIfterList.size + duaItemCount //mDisplayableSehriIfterListFromAPI
        }
//        if(fromMalaysia==false){
//            return 3+mDisplayableSehriIfterListFromAPI.size+duaItemCount
//        }
        if (mDisplayableSehriIfterListFromAPI == null || mDisplayableSehriIfterListFromAPI.isEmpty()) {
            return 0
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
//        if(!fromMalaysia){
//            return when (position) {
//                0 -> _HEADER
//                1 -> _SEHRI_IFTER_HEADER
//                in 2..mDisplayableSehriIfterListFromAPI.size+2 -> _ROZA_INFO
//                mDisplayableSehriIfterListFromAPI.size+3 -> _ESSENTIAL_DUA_HEADER
//                else -> _DUA_INFO
//            }
//        }
//        else(fromMalaysia){
//            return when (position) {
//                0 -> _HEADER
//                1 -> _SEHRI_IFTER_HEADER
//                in 2..mDisplayableSehriIfterListFromAPI.size+2 -> _ROZA_INFO
//                mDisplayableSehriIfterListFromAPI.size+3 -> _ESSENTIAL_DUA_HEADER
//                else -> _DUA_INFO
//            }
//        }
        return when (position) {
            0 -> _HEADER
            1 -> _SEHRI_IFTER_HEADER
            in 2..12 -> _ROZA_INFO
            13 -> _ESSENTIAL_DUA_HEADER
            else -> _DUA_INFO
        }
    }

    inner class DisplayableSehriIfterListControlFromAPI {
        fun getDisplayableListFromAPI(): MutableList<Data> {
            when (CalenderUtil.isRamadanNow() && !fromMalaysia) {
                true -> return mRamadanSehriIfterTimesFromAPI
                else -> return mNextTenDaysSehriIfterTimesFromAPI
            }
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

    inner class TodaySehriIfterControlFromAPI {

        //returns IfterSehriObject from given list for today
        fun getSehriIfterTimeForTodayFromAPI(list2: MutableList<Data>): Data? {
            for (item in list2) {
                if (item.isToday) return item
            }
            return null
        }
    }

    inner class TodaySehriIfterControl {

        //returns IfterSehriObject from given list for today
        fun getSehriIfterTimeForToday(list: MutableList<IfterAndSehriTime>): IfterAndSehriTime? {
            for (item in list) {
                if (item.isToday) return item
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