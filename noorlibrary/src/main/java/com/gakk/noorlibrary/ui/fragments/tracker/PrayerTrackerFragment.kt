package com.gakk.noorlibrary.ui.fragments.tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.compactcalender.CompactCalendarView
import com.gakk.noorlibrary.extralib.compactcalender.domain.Event
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.model.tracker.Data
import com.gakk.noorlibrary.model.tracker.SalahStatus
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.TrackerViewModel
import com.github.eltohamy.materialhijricalendarview.CalendarDay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

internal class PrayerTrackerFragment : Fragment() {
    private lateinit var repository: RestRepository
    private lateinit var model: TrackerViewModel
    private var mCallback: DetailsCallBack? = null
    private lateinit var currentCalender: Calendar
    val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val dateFormatForMonth = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
    private val dateFormatForDisplaying =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var prayerList: ArrayList<Data> = arrayListOf()
    private var selectedDate = Date()
    private lateinit var prayerTimeCalculator: PrayerTimeCalculator
    private lateinit var upCommingPrayer: UpCommingPrayer
    private var fromMalaysia = false
    private lateinit var compactcalendarView: CompactCalendarView
    private lateinit var tvEngDate: AppCompatTextView
    private lateinit var switchFajr: SwitchCompat
    private lateinit var switchJohr: SwitchCompat
    private lateinit var switchAsr: SwitchCompat
    private lateinit var switchMagrib: SwitchCompat
    private lateinit var switchEsha: SwitchCompat
    private lateinit var prevDate: ImageView
    private lateinit var nextDate: ImageView
    private lateinit var tvDateArabic: AppCompatTextView
    private lateinit var tvDateTodayEng: AppCompatTextView
    private lateinit var progressLayout: ConstraintLayout

    companion object {

        @JvmStatic
        fun newInstance() =
            PrayerTrackerFragment()
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
            R.layout.fragment_prayer_tracker,
            container, false
        )
        compactcalendarView = view.findViewById(R.id.compactcalendar_view)
        tvEngDate = view.findViewById(R.id.tvEngDate)
        switchFajr = view.findViewById(R.id.switchFajr)
        switchJohr = view.findViewById(R.id.switchJohr)
        switchAsr = view.findViewById(R.id.switchAsr)
        switchMagrib = view.findViewById(R.id.switchMagrib)
        switchEsha = view.findViewById(R.id.switchEsha)
        prevDate = view.findViewById(R.id.prev_date)
        nextDate = view.findViewById(R.id.next_date)
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
                this@PrayerTrackerFragment,
                TrackerViewModel.FACTORY(repository)
            ).get(TrackerViewModel::class.java)

            initUI()
        }
    }

    fun initUI() {

        prayerTimeCalculator = PrayerTimeCalculator(requireContext())
        setPrayerWaqt()

        Log.e("chkwaqt", "" + upCommingPrayer.nextWaqtNameTracker)

        updateDate(Date())
        subscribeObserver()
        val fromMonth =
            dateFormatForDisplaying.format(compactcalendarView.firstDayOfCurrentMonth)
        val toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))
        model.loadAllPrayerData(fromMonth, toMonth)

        compactcalendarView.setUseThreeLetterAbbreviation(true)
        currentCalender = Calendar.getInstance(Locale.getDefault())
        currentCalender.time = Date()
        compactcalendarView.invalidate()

        tvEngDate.text =
            dateFormatForMonth.format(compactcalendarView.firstDayOfCurrentMonth)


        compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                when (Util.checkSelectedDate(dateFormat.format(dateClicked))) {
                    true -> {
                        enableSwitch()
                        selectedDate = dateClicked!!

                        val events = compactcalendarView.getEvents(dateClicked)

                        switchFajr.isChecked = events[0].checked
                        switchJohr.isChecked = events[1].checked
                        switchAsr.isChecked = events[2].checked
                        switchMagrib.isChecked = events[3].checked
                        switchEsha.isChecked = events[4].checked
                        enableSwitchCurrentWaqt(upCommingPrayer.nextWaqtNameTracker)

                    }

                    false -> {
                        disableSwitch()
                    }
                }

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                Log.e(
                    "chkscrolldate",
                    "" + Util.checkSelectedDate(dateFormat.format(firstDayOfNewMonth))
                )

                selectedDate = firstDayOfNewMonth!!
                prayerList.clear()
                tvEngDate.setText(dateFormatForMonth.format(firstDayOfNewMonth))
                val fromMonthNew =
                    dateFormatForDisplaying.format(firstDayOfNewMonth)
                val toMonthNew =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(firstDayOfNewMonth))
                model.loadAllPrayerData(fromMonthNew, toMonthNew)
            }

        })

        prevDate.handleClickEvent { compactcalendarView.scrollLeft() }
        nextDate.handleClickEvent { compactcalendarView.scrollRight() }

        switchFajr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataFajr = compactcalendarView.getEvents(selectedDate)[0]

                when (dataFajr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            isChecked, switchJohr.isChecked,
                            switchAsr.isChecked, switchMagrib.isChecked,
                            switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataFajr.data as Data

                        val salahStatus = SalahStatus(
                            isChecked, switchJohr.isChecked, switchAsr.isChecked,
                            switchMagrib.isChecked, switchEsha.isChecked
                        )
                        model.updatePrayerData(
                            data.id,
                            data.createdBy, outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                }
            }
        }

        switchJohr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataJohr = compactcalendarView.getEvents(selectedDate)[1]

                compactcalendarView.getEvents(selectedDate)[1].checked = isChecked
                compactcalendarView.invalidate()

                when (dataJohr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            switchFajr.isChecked, isChecked,
                            switchAsr.isChecked, switchMagrib.isChecked,
                            switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataJohr.data as Data

                        val salahStatus = SalahStatus(
                            switchFajr.isChecked, isChecked, switchAsr.isChecked,
                            switchMagrib.isChecked, switchEsha.isChecked
                        )

                        model.updatePrayerData(
                            data.id,
                            data.createdBy, outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                }
            }
        }

        switchAsr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataAsr = compactcalendarView.getEvents(selectedDate)[2]

                compactcalendarView.getEvents(selectedDate)[2].checked = isChecked
                compactcalendarView.invalidate()

                when (dataAsr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            switchFajr.isChecked, switchJohr.isChecked,
                            isChecked, switchMagrib.isChecked,
                            switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataAsr.data as Data
                        val salahStatus = SalahStatus(
                            switchFajr.isChecked, switchJohr.isChecked, isChecked,
                            switchMagrib.isChecked, switchEsha.isChecked
                        )

                        model.updatePrayerData(
                            data.id,
                            data.createdBy, outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                }
            }
        }
        switchMagrib.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataMagrib = compactcalendarView.getEvents(selectedDate)[3]

                compactcalendarView.getEvents(selectedDate)[3].checked = isChecked
                compactcalendarView.invalidate()

                when (dataMagrib.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            switchFajr.isChecked, switchJohr.isChecked,
                            switchAsr.isChecked, isChecked,
                            switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataMagrib.data as Data
                        val salahStatus = SalahStatus(
                            switchFajr.isChecked,
                            switchJohr.isChecked,
                            switchAsr.isChecked,
                            isChecked,
                            switchEsha.isChecked
                        )

                        model.updatePrayerData(
                            data.id,
                            data.createdBy, outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                }
            }
        }

        switchEsha.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataEsha = compactcalendarView.getEvents(selectedDate)[4]

                compactcalendarView.getEvents(selectedDate)[4].checked = isChecked
                compactcalendarView.invalidate()

                when (dataEsha.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            switchFajr.isChecked,
                            switchJohr.isChecked,
                            switchAsr.isChecked,
                            switchMagrib.isChecked,
                            isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataEsha.data as Data
                        val salahStatus = SalahStatus(
                            switchFajr.isChecked,
                            switchJohr.isChecked,
                            switchAsr.isChecked,
                            switchMagrib.isChecked,
                            isChecked
                        )

                        model.updatePrayerData(
                            data.id,
                            data.createdBy, outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                }
            }
        }
    }


    fun setPrayerWaqt() {
        if (TimeFormtter.getCountryName(requireContext()) == COUNTRY_NAME
            || TimeFormtter.getCountryName(requireContext()) == COUNTRY_NAME_BN
        ) {
            fromMalaysia = true
        }

        Log.e("fromMalaysia", "$fromMalaysia")

        upCommingPrayer = prayerTimeCalculator.getUpCommingPrayer()


        enableSwitchCurrentWaqt(upCommingPrayer.nextWaqtNameTracker)
    }

    fun enableSwitchCurrentWaqt(nextWaqt: String) {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        if (Util.checkTodayDate(dateFormat.format(selectedDate), dateFormat.format(Date()))) {
            when (nextWaqt) {
                context?.getString(R.string.txt_fajr) -> {
                    switchEsha.isEnabled = false
                    switchFajr.isEnabled = false
                    switchJohr.isEnabled = false
                    switchAsr.isEnabled = false
                    switchMagrib.isEnabled = false
                }
                context?.getString(R.string.txt_johr) -> {
                    switchFajr.isEnabled = true
                    switchJohr.isEnabled = false
                    switchAsr.isEnabled = false
                    switchMagrib.isEnabled = false
                    switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_asr) -> {
                    switchJohr.isEnabled = true
                    switchFajr.isEnabled = true
                    switchAsr.isEnabled = false
                    switchMagrib.isEnabled = false
                    switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_magrib) -> {
                    switchFajr.isEnabled = true
                    switchJohr.isEnabled = true
                    switchAsr.isEnabled = true
                    switchMagrib.isEnabled = false
                    switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_esha) -> {
                    switchFajr.isEnabled = true
                    switchJohr.isEnabled = true
                    switchAsr.isEnabled = true
                    switchMagrib.isEnabled = true
                    switchEsha.isEnabled = false
                }

                else -> {
                    switchFajr.isEnabled = true
                    switchJohr.isEnabled = true
                    switchAsr.isEnabled = true
                    switchMagrib.isEnabled = true
                    switchEsha.isEnabled = true
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
                    (CalendarDay.from(date).getDay() - 1).toString() + " "
                            + resources.getStringArray(R.array.custom_months)[CalendarDay.from(
                        date
                    )
                        .month] + " "
                            + TimeFormtter.getNumberByLocale(
                        java.lang.String.valueOf(
                            CalendarDay.from(date).year
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

    fun disableSwitch() {
        switchFajr.isEnabled = false
        switchFajr.isChecked = false

        switchJohr.isEnabled = false
        switchJohr.isChecked = false

        switchAsr.isEnabled = false
        switchAsr.isChecked = false

        switchMagrib.isEnabled = false
        switchMagrib.isChecked = false

        switchEsha.isEnabled = false
        switchEsha.isChecked = false
    }

    fun enableSwitch() {
        switchFajr.isEnabled = true
        switchFajr.isChecked = false

        switchJohr.isEnabled = true
        switchJohr.isChecked = false

        switchAsr.isEnabled = true
        switchAsr.isChecked = false

        switchMagrib.isEnabled = true
        switchMagrib.isChecked = false

        switchEsha.isEnabled = true
        switchEsha.isChecked = false
    }

    private fun subscribeObserver() {
        model.prayerListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            prayerList = it.data.data.toArrayList()
                            addCalendarEvent(
                                prayerList,
                                getMonth(outputFormat.format(selectedDate))
                            )
                        }

                        else -> {
                            addCalendarEvent(
                                prayerList,
                                getMonth(outputFormat.format(selectedDate))
                            )

                            when (Util.checkSelectedDate(dateFormat.format(selectedDate))) {
                                true -> {
                                    enableSwitch()
                                }
                                false -> {
                                    disableSwitch()
                                }
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }

        model.addPrayerData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    val result = it.data?.data
                    if (result != null) {
                        updateCalenderEvent(result)
                    }

                }
                Status.ERROR -> {
                    Log.e("AddPrayerData", "Error")
                }
            }
        })

        model.updatePrayerData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("UpdatePrayerData", "loading")
                }
                Status.SUCCESS -> {
                    Log.e("UpdatePrayerData", "Success")
                    val result = it.data?.data
                    if (result != null) {
                        updateCalenderEvent(result)
                    }

                }
                Status.ERROR -> {
                    Log.e("UpdatePrayerData", "Error")
                }
            }
        })
    }

    fun updateCalenderEvent(result: Data) {
        val date = inputFormat.parse(result.createdOn)
        compactcalendarView.getEvents(date)[0].data = result
        compactcalendarView.getEvents(date)[0].checked =
            result.salahStatus.fajr == true
        compactcalendarView.getEvents(date)[1].data = result
        compactcalendarView.getEvents(date)[1].checked =
            result.salahStatus.zuhr == true
        compactcalendarView.getEvents(date)[2].data = result
        compactcalendarView.getEvents(date)[2].checked =
            result.salahStatus.asar == true
        compactcalendarView.getEvents(date)[3].data = result
        compactcalendarView.getEvents(date)[3].checked =
            result.salahStatus.maghrib == true
        compactcalendarView.getEvents(date)[4].data = result
        compactcalendarView.getEvents(date)[4].checked =
            result.salahStatus.isha == true

        compactcalendarView.invalidate()
    }

    fun addCalendarEvent(data: List<Data>, month: Int) {
        val todayDay = currentCalender[Calendar.DATE]

        val eventList: ArrayList<Event> = arrayListOf()
        currentCalender.set(Calendar.MONTH, month)
        val maxDayValue = currentCalender.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDayValue) {
            currentCalender.set(Calendar.DAY_OF_MONTH, i)
            val timeInMillis: Long = currentCalender.timeInMillis

            val machedDateData = findUsingIterator(
                dateFormatForDisplaying.format(timeInMillis),
                data
            )

            var eventOne: Event?
            var eventTwo: Event?
            var eventThree: Event?
            var eventFour: Event?
            var eventFive: Event?
            val date = currentCalender.time.time

            eventOne = Event(
                date,
                machedDateData
            )
            eventOne.checked = machedDateData?.salahStatus?.fajr == true


            eventTwo = Event(
                date,
                machedDateData
            )
            eventTwo.checked = machedDateData?.salahStatus?.zuhr == true

            eventThree =
                Event(
                    date,
                    machedDateData
                )
            eventThree.checked = machedDateData?.salahStatus?.asar == true


            eventFour =
                Event(
                    date,
                    machedDateData
                )
            eventFour.checked = machedDateData?.salahStatus?.maghrib == true

            eventFive =
                Event(
                    date,
                    machedDateData
                )
            eventFive.checked = machedDateData?.salahStatus?.isha == true



            eventList.add(eventOne)
            eventList.add(eventTwo)
            eventList.add(eventThree)
            eventList.add(eventFour)
            eventList.add(eventFive)


            if (i == todayDay) {
                switchFajr.isChecked = eventOne.checked
                switchJohr.isChecked = eventTwo.checked
                switchAsr.isChecked = eventThree.checked
                switchMagrib.isChecked = eventFour.checked
                switchEsha.isChecked = eventFive.checked
            }

           compactcalendarView.addEvents(eventList.toArrayList())

        }
    }

    fun findUsingIterator(
        name: String?, prayerList: List<Data>
    ): Data? {
        val iterator: Iterator<Data> = prayerList.iterator()
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

    private fun getMonth(date: String): Int {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        val month = cal[Calendar.MONTH]
        return month
    }
}