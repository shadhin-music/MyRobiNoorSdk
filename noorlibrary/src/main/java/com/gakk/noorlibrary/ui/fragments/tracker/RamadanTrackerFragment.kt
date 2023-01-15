package com.gakk.noorlibrary.ui.fragments.tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentRamadanTrackerBinding
import com.gakk.noorlibrary.extralib.customcalender.CalendarDay
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


class RamadanTrackerFragment : Fragment() {
    private lateinit var binding: FragmentRamadanTrackerBinding
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
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_ramadan_tracker,
                container,
                false
            )

        return binding.root
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

        binding.calendarView.setCalendarDayLayout(R.layout.row_calendar_ramadan)
        updateDate(Date())
        subscribeObserver()
        fromMonth = Util.getFirstDateOfMonth(Date())

        toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))
        model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)

        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {

                val list = listOf(
                    CalendarDay(eventDay.calendar).apply {
                        labelColor = R.color.white
                        selectedLabelColor = R.color.white
                        selectedBackgroundResource = R.drawable.bg_islamic_day_mark
                        backgroundResource = R.drawable.bg_islamic_day_mark
                    }
                )

                binding.calendarView.setCalendarDays(list)

                when (Util.checkSelectedDate(dateFormat.format(eventDay.calendar.time))) {

                    true -> {
                        selectedDate = eventDay.calendar.time
                        binding.llFastingYes.isClickable = true
                        binding.llFastingNo.isClickable = true
                    }

                    else -> {
                        binding.llFastingYes.isClickable = false
                        binding.llFastingNo.isClickable = false
                    }
                }

            }

        })

        binding.calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {

                fromMonth = Util.getFirstDateOfMonth(binding.calendarView.currentPageDate.time)

                toMonth =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(binding.calendarView.currentPageDate.time))
                model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
            }
        })

        binding.calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                fromMonth = Util.getFirstDateOfMonth(binding.calendarView.currentPageDate.time)

                toMonth =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(binding.calendarView.currentPageDate.time))
                model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
            }

        })


        binding.llFastingYes.handleClickEvent {
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

        binding.llFastingNo.handleClickEvent {
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

            binding.tvDateArabic.text = (
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
        binding.tvDateTodayEng.text =
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
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
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
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }

        model.addRamadanData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        })

        model.updateRamadanData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    model.loadAllRamadanData(dateFormatForDisplaying.format(fromMonth), toMonth)
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
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

        binding.calendarView.setEvents(events)

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