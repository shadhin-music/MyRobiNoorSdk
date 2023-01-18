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
import com.gakk.noorlibrary.databinding.FragmentPrayerTrackerBinding
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
    private lateinit var binding: FragmentPrayerTrackerBinding
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
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_prayer_tracker,
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
            dateFormatForDisplaying.format(binding.compactcalendarView.firstDayOfCurrentMonth)
        val toMonth = Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(Date()))
        model.loadAllPrayerData(fromMonth, toMonth)

        binding.compactcalendarView.setUseThreeLetterAbbreviation(true)
        currentCalender = Calendar.getInstance(Locale.getDefault())
        currentCalender.time = Date()
        binding.compactcalendarView.invalidate()

        binding.tvEngDate.text =
            dateFormatForMonth.format(binding.compactcalendarView.firstDayOfCurrentMonth)

        Log.e(
            "datechk",
            "Date current or less" + dateFormatForDisplaying.format(binding.compactcalendarView.firstDayOfCurrentMonth)
        )

        binding.compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                when (Util.checkSelectedDate(dateFormat.format(dateClicked))) {
                    true -> {
                        enableSwitch()
                        selectedDate = dateClicked!!

                        val events = binding.compactcalendarView.getEvents(dateClicked)

                        binding.switchFajr.isChecked = events[0].checked
                        binding.switchJohr.isChecked = events[1].checked
                        binding.switchAsr.isChecked = events[2].checked
                        binding.switchMagrib.isChecked = events[3].checked
                        binding.switchEsha.isChecked = events[4].checked
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
                binding.tvEngDate.setText(dateFormatForMonth.format(firstDayOfNewMonth))
                val fromMonthNew =
                    dateFormatForDisplaying.format(firstDayOfNewMonth)
                val toMonthNew =
                    Util.getLastDayOfTheMonth(dateFormatForDisplaying.format(firstDayOfNewMonth))
                model.loadAllPrayerData(fromMonthNew, toMonthNew)
            }

        })

        binding.prevDate.handleClickEvent { binding.compactcalendarView.scrollLeft() }
        binding.nextDate.handleClickEvent { binding.compactcalendarView.scrollRight() }

        binding.switchFajr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataFajr = binding.compactcalendarView.getEvents(selectedDate)[0]

                when (dataFajr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            isChecked, binding.switchJohr.isChecked,
                            binding.switchAsr.isChecked, binding.switchMagrib.isChecked,
                            binding.switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataFajr.data as Data

                        val salahStatus = SalahStatus(
                            isChecked, binding.switchJohr.isChecked, binding.switchAsr.isChecked,
                            binding.switchMagrib.isChecked, binding.switchEsha.isChecked
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

        binding.switchJohr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataJohr = binding.compactcalendarView.getEvents(selectedDate)[1]

                binding.compactcalendarView.getEvents(selectedDate)[1].checked = isChecked
                binding.compactcalendarView.invalidate()

                when (dataJohr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked, isChecked,
                            binding.switchAsr.isChecked, binding.switchMagrib.isChecked,
                            binding.switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataJohr.data as Data

                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked, isChecked, binding.switchAsr.isChecked,
                            binding.switchMagrib.isChecked, binding.switchEsha.isChecked
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

        binding.switchAsr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataAsr = binding.compactcalendarView.getEvents(selectedDate)[2]

                binding.compactcalendarView.getEvents(selectedDate)[2].checked = isChecked
                binding.compactcalendarView.invalidate()

                when (dataAsr.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked, binding.switchJohr.isChecked,
                            isChecked, binding.switchMagrib.isChecked,
                            binding.switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataAsr.data as Data
                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked, binding.switchJohr.isChecked, isChecked,
                            binding.switchMagrib.isChecked, binding.switchEsha.isChecked
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
        binding.switchMagrib.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataMagrib = binding.compactcalendarView.getEvents(selectedDate)[3]

                binding.compactcalendarView.getEvents(selectedDate)[3].checked = isChecked
                binding.compactcalendarView.invalidate()

                when (dataMagrib.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked, binding.switchJohr.isChecked,
                            binding.switchAsr.isChecked, isChecked,
                            binding.switchEsha.isChecked
                        )

                        model.postPrayerData(
                            outputFormat.format(selectedDate),
                            AppPreference.language!!, salahStatus
                        )
                    }
                    else -> {

                        val data: Data = dataMagrib.data as Data
                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked,
                            binding.switchJohr.isChecked,
                            binding.switchAsr.isChecked,
                            isChecked,
                            binding.switchEsha.isChecked
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

        binding.switchEsha.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val dataEsha = binding.compactcalendarView.getEvents(selectedDate)[4]

                binding.compactcalendarView.getEvents(selectedDate)[4].checked = isChecked
                binding.compactcalendarView.invalidate()

                when (dataEsha.data) {
                    null -> {

                        val salahStatus = SalahStatus(
                            binding.switchFajr.isChecked,
                            binding.switchJohr.isChecked,
                            binding.switchAsr.isChecked,
                            binding.switchMagrib.isChecked,
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
                            binding.switchFajr.isChecked,
                            binding.switchJohr.isChecked,
                            binding.switchAsr.isChecked,
                            binding.switchMagrib.isChecked,
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
                    binding.switchEsha.isEnabled = false
                    binding.switchFajr.isEnabled = false
                    binding.switchJohr.isEnabled = false
                    binding.switchAsr.isEnabled = false
                    binding.switchMagrib.isEnabled = false
                }
                context?.getString(R.string.txt_johr) -> {
                    binding.switchFajr.isEnabled = true
                    binding.switchJohr.isEnabled = false
                    binding.switchAsr.isEnabled = false
                    binding.switchMagrib.isEnabled = false
                    binding.switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_asr) -> {
                    binding.switchJohr.isEnabled = true
                    binding.switchFajr.isEnabled = true
                    binding.switchAsr.isEnabled = false
                    binding.switchMagrib.isEnabled = false
                    binding.switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_magrib) -> {
                    binding.switchFajr.isEnabled = true
                    binding.switchJohr.isEnabled = true
                    binding.switchAsr.isEnabled = true
                    binding.switchMagrib.isEnabled = false
                    binding.switchEsha.isEnabled = false
                }
                context?.getString(R.string.txt_esha) -> {
                    binding.switchFajr.isEnabled = true
                    binding.switchJohr.isEnabled = true
                    binding.switchAsr.isEnabled = true
                    binding.switchMagrib.isEnabled = true
                    binding.switchEsha.isEnabled = false
                }

                else -> {
                    binding.switchFajr.isEnabled = true
                    binding.switchJohr.isEnabled = true
                    binding.switchAsr.isEnabled = true
                    binding.switchMagrib.isEnabled = true
                    binding.switchEsha.isEnabled = true
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
        binding.tvDateTodayEng.text =
            (TimeFormtter.getBanglaWeekName(weekIndex, requireContext())
                .toString() + ", " + TimeFormtter.getNumberByLocale(day.toString()) + " " + TimeFormtter.getBanglaMonthName(
                month,
                requireContext()
            ) + " " + TimeFormtter.getNumberByLocale(year.toString()))

    }

    fun disableSwitch() {
        binding.switchFajr.isEnabled = false
        binding.switchFajr.isChecked = false

        binding.switchJohr.isEnabled = false
        binding.switchJohr.isChecked = false

        binding.switchAsr.isEnabled = false
        binding.switchAsr.isChecked = false

        binding.switchMagrib.isEnabled = false
        binding.switchMagrib.isChecked = false

        binding.switchEsha.isEnabled = false
        binding.switchEsha.isChecked = false
    }

    fun enableSwitch() {
        binding.switchFajr.isEnabled = true
        binding.switchFajr.isChecked = false

        binding.switchJohr.isEnabled = true
        binding.switchJohr.isChecked = false

        binding.switchAsr.isEnabled = true
        binding.switchAsr.isChecked = false

        binding.switchMagrib.isEnabled = true
        binding.switchMagrib.isChecked = false

        binding.switchEsha.isEnabled = true
        binding.switchEsha.isChecked = false
    }

    private fun subscribeObserver() {
        model.prayerListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
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
                    binding.progressLayout.root.visibility = View.GONE
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
        binding.compactcalendarView.getEvents(date)[0].data = result
        binding.compactcalendarView.getEvents(date)[0].checked =
            result.salahStatus.fajr == true
        binding.compactcalendarView.getEvents(date)[1].data = result
        binding.compactcalendarView.getEvents(date)[1].checked =
            result.salahStatus.zuhr == true
        binding.compactcalendarView.getEvents(date)[2].data = result
        binding.compactcalendarView.getEvents(date)[2].checked =
            result.salahStatus.asar == true
        binding.compactcalendarView.getEvents(date)[3].data = result
        binding.compactcalendarView.getEvents(date)[3].checked =
            result.salahStatus.maghrib == true
        binding.compactcalendarView.getEvents(date)[4].data = result
        binding.compactcalendarView.getEvents(date)[4].checked =
            result.salahStatus.isha == true

        binding.compactcalendarView.invalidate()
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
                binding.switchFajr.isChecked = eventOne.checked
                binding.switchJohr.isChecked = eventTwo.checked
                binding.switchAsr.isChecked = eventThree.checked
                binding.switchMagrib.isChecked = eventFour.checked
                binding.switchEsha.isChecked = eventFive.checked
            }

            binding.compactcalendarView.addEvents(eventList.toArrayList())

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