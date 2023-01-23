package com.gakk.noorlibrary.ui.fragments.tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.customcalender.CalendarDay
import com.gakk.noorlibrary.extralib.customcalender.CalendarView
import com.gakk.noorlibrary.extralib.customcalender.EventDay
import com.gakk.noorlibrary.extralib.customcalender.listeners.OnCalendarPageChangeListener
import com.gakk.noorlibrary.extralib.customcalender.listeners.OnDayClickListener
import com.gakk.noorlibrary.model.tracker.ramadan.Data
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.TrackerViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


internal class RamadanTrackerFragment : Fragment() {
    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: TrackerViewModel
    var ramadanDataList: ArrayList<Data> = arrayListOf()
    private lateinit var currentCalender: Calendar
    val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val dateFormatForDisplaying =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    private var selectedDate = Date()
    private var fromMonth: Date? = null
    private lateinit var toMonth: String
    private lateinit var calendarView: CalendarView
    private lateinit var llFastingYes: LinearLayout
    private lateinit var llFastingNo: LinearLayout
    private lateinit var tvDateArabic: AppCompatTextView
    private lateinit var tvDateTodayEng: AppCompatTextView
    private lateinit var progressLayout: ConstraintLayout

    companion object {

        @JvmStatic
        fun newInstance() =
            RamadanTrackerFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_ramadan_tracker,
            container, false
        )

        calendarView = view.findViewById(R.id.calendarView)
        llFastingYes = view.findViewById(R.id.llFastingYes)
        llFastingNo = view.findViewById(R.id.llFastingNo)
        tvDateArabic = view.findViewById(R.id.tvDateArabic)
        tvDateTodayEng = view.findViewById(R.id.tvDateTodayEng)
        progressLayout = view.findViewById(R.id.progressLayout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@RamadanTrackerFragment,
                TrackerViewModel.FACTORY(repository)
            ).get(TrackerViewModel::class.java)

            initUI()
        }
    }

    fun initUI() {
        currentCalender = Calendar.getInstance(Locale.getDefault())
        currentCalender.time = Date()

        calendarView.setCalendarDayLayout(R.layout.row_calendar_ramadan)
        updateDate(Date())
        subscribeObserver()
        fromMonth = Util.getFirstDateOfMonth(Date())

        toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))
        model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {

                val list = listOf(
                    CalendarDay(eventDay.calendar).apply {
                        labelColor = R.color.white
                        selectedLabelColor = R.color.white
                        selectedBackgroundResource = R.drawable.bg_islamic_day_mark
                        backgroundResource = R.drawable.bg_islamic_day_mark
                    }
                )

               calendarView.setCalendarDays(list)

                when (Util.checkSelectedDate(dateFormat.format(eventDay.calendar.time))) {

                    true -> {
                        selectedDate = eventDay.calendar.time
                        llFastingYes.isClickable = true
                        llFastingNo.isClickable = true
                    }

                    else -> {
                        llFastingYes.isClickable = false
                        llFastingNo.isClickable = false
                    }
                }

            }

        })

        calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {

                fromMonth = Util.getFirstDateOfMonth(calendarView.currentPageDate.time)

                toMonth =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(calendarView.currentPageDate.time))
                model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
            }
        })

        calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                fromMonth = Util.getFirstDateOfMonth(calendarView.currentPageDate.time)

                toMonth =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(calendarView.currentPageDate.time))
                model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
            }

        })


        llFastingYes.handleClickEvent {
            Log.e("Datecheck", "" + dateFormatForDisplaying.format(selectedDate))

            val dataRamadan =
                findUsingIterator(dateFormatForDisplaying.format(selectedDate), ramadanDataList)

            when (dataRamadan) {
                null -> {
                    model.postRamadanData(
                        outputFormat.format(selectedDate),
                        AppPreference.language!!,
                        true
                    )
                }

                else -> {
                    model.updateRamadanData(
                        dataRamadan.id,
                        dataRamadan.createdBy,
                        outputFormat.format(selectedDate),
                        AppPreference.language!!,
                        true
                    )
                }
            }
        }

        llFastingNo.handleClickEvent {
            val dataRamadan =
                findUsingIterator(dateFormatForDisplaying.format(selectedDate), ramadanDataList)

            when (dataRamadan) {
                null -> {
                    model.postRamadanData(
                        outputFormat.format(selectedDate),
                        AppPreference.language!!,
                        false
                    )
                }

                else -> {
                    model.updateRamadanData(
                        dataRamadan.id,
                        dataRamadan.createdBy,
                        outputFormat.format(selectedDate),
                        AppPreference.language!!,
                        false
                    )
                }
            }
        }
    }

    private fun updateDate(date: Date) {
        val cal = Calendar.getInstance()
        cal.time = date
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val weekIndex = cal.get(7) - 1

        //update islamic date

        tvDateArabic.text = (
                TimeFormtter.getNumberByLocale(
                    (com.github.eltohamy.materialhijricalendarview.CalendarDay.from(date)
                        .getDay() - 1).toString() + " "
                            + resources.getStringArray(R.array.custom_months)[com.github.eltohamy.materialhijricalendarview.CalendarDay.from(
                        date
                    )
                        .month] + " "
                            + TimeFormtter.getNumberByLocale(
                        java.lang.String.valueOf(
                            com.github.eltohamy.materialhijricalendarview.CalendarDay.from(date).year
                        )
                    )
                )
                )

        //update english date
        tvDateTodayEng.text =
            (TimeFormtter.getBanglaWeekName(weekIndex, requireContext())
                .toString() + ", " + TimeFormtter.getNumberByLocale(day.toString()) + " " + TimeFormtter.getBanglaMonthName(
                month,
                requireContext()
            ) + " " + TimeFormtter.getNumberByLocale(year.toString()))

    }

    private fun subscribeObserver() {
        model.ramadanListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                   progressLayout.visibility = View.GONE
                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            ramadanDataList = it.data.data.toArrayList()
                            addCalendarEvent(ramadanDataList)
                        }

                        else -> {
                            addCalendarEvent(ramadanDataList)
                        }
                    }

                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }

        model.addRamadanData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        })

        model.updateRamadanData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }

    fun addCalendarEvent(data: List<Data>) {
        val events: MutableList<EventDay> = arrayListOf() //error
        for (i in 0..data.size - 1) {
            val calendar = Calendar.getInstance(Locale.getDefault())

            val date = data.get(i).createdOn
            calendar.time = outputFormat.parse(date)!!
            var eventRamadan: EventDay

            when (ramadanDataList[i].ramadanStatus) {
                true -> {
                    eventRamadan = EventDay(calendar, R.drawable.ic_check_ramadan_tracker)
                }
                false -> {
                    eventRamadan = EventDay(calendar, R.drawable.test_close)
                }
            }

            events.add(eventRamadan)

        }

        calendarView.setEvents(events)

    }

    fun findUsingIterator(
        name: String?, dataList: List<Data>
    ): Data? {
        val iterator: Iterator<Data> = dataList.iterator()
        while (iterator.hasNext()) {
            val prayerData: Data = iterator.next()
            val date = inputFormat.parse(prayerData.createdOn)
            val outputText = outputFormat.format(date)
            if (outputText.equals(name)) {
                return prayerData
            }
        }
        return null
    }
}