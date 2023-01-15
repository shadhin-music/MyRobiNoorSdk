package com.gakk.noorlibrary.ui.fragments.calender


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentIslamicCalenderBdBinding
import com.gakk.noorlibrary.model.calender.IslamicCalendarModel
import com.gakk.noorlibrary.model.calender.IslamicChhutiModel
import com.gakk.noorlibrary.ui.adapter.IslamicCalendarAdapter
import com.gakk.noorlibrary.ui.adapter.IslamicChhutiAdapter
import com.gakk.noorlibrary.util.*
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*


class IslamicCalenderFragmentBd : Fragment() {

    private lateinit var binding: FragmentIslamicCalenderBdBinding

    private var currentMonthIndex = 0
    private var mDataAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mResponseData: ArrayList<IslamicCalendarModel>? = null
    private var mCallback: DetailsCallBack? = null

    private var mResponseDataIslCht: ArrayList<IslamicChhutiModel>? = null
    private var mDataAdapterIslCht: RecyclerView.Adapter<*>? = null
    private var mLayoutManagerIslCht: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            IslamicCalenderFragmentBd()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_islamic_calender_bd,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(resources.getString(R.string.cat_islamic_calender))
        initFunctionality()
        initialListener()
        loadData()
    }

    private fun loadData() {
        loadIslamicChhutiData()
        loadIslamicCalendar(currentMonthIndex)
    }

    private fun loadIslamicCalendar(vararg iArr: Int) {
        val mCal = Calendar.getInstance(Locale.getDefault())
        mCal.time = Date()
        mCal.add(Calendar.DAY_OF_MONTH, -1)

        val uCal = UmmalquraCalendar()
        uCal.time = mCal.time

        var i = uCal[2]
        if (iArr.isNotEmpty()) {
            i = iArr[0]
        }

        Log.e("THE MONTH ", "uCal[2]" + uCal[2] + " i:" + i)
        val instance = CustomIslamicCalendarDataBd
        binding.calenderView.txtVwArobiDate.text = instance.getIslamicToDay(requireContext(), i)
        val instance2 = Calendar.getInstance()
        val calendarUtility = CalendarUtility()
        val weekNameBn: String =
            calendarUtility.getWeekName(Calendar.getInstance(), requireContext())
        val sb = StringBuilder()
        sb.append(TimeFormtter.getNumberByLocale(instance2[5].toString()))
        sb.append(" ")
        sb.append(calendarUtility.getMonthName(requireContext(), instance2[2]))
        sb.append(" ")
        sb.append(
            ("" + TimeFormtter.getNumberByLocale(
                calendarUtility.getCurrentYear().toString()
            )).substring(2)
        )
        val sb2 = sb.toString()
        val textView: TextView = binding.calenderView.txtVwEnglishDate
        textView.text = ("$weekNameBn, $sb2")
        val islamicMonthData: ArrayList<IslamicCalendarModel> = instance.getIslamicMonthData(i)
        mResponseData?.clear()
        mResponseData?.addAll(islamicMonthData)
        mDataAdapter?.notifyDataSetChanged()
    }

    private fun loadIslamicChhutiData() {
        var loadJSONFromAsset: String = loadJSONFromAsset("islamic_chuti.json")
        val string: String = AppPreference.language!!
        if (string.equals(LAN_ENGLISH, ignoreCase = true)) {
            loadJSONFromAsset = loadJSONFromAsset("islamic_chuti_en.json")
        }
        val jSONByIndex: ArrayList<Array<String>> = Parser.getJSONByIndex(
            loadJSONFromAsset + "", "iChhuti", arrayOf("id", "chutiTitle", "iDate", "gragrian")
        )
        if (jSONByIndex.size > 0) {
            for (i in 0 until jSONByIndex.size) {
                mResponseDataIslCht?.add(IslamicChhutiModel(strArr = jSONByIndex[i]))
            }
            mDataAdapterIslCht?.notifyDataSetChanged()
        }
    }


    private fun initialListener() {
        binding.calenderView.llNext.setOnClickListener { nextPrevious(1) }
        binding.calenderView.llLftPrevious.setOnClickListener { nextPrevious(2) }
    }

    private fun nextPrevious(i: Int) {
        if (i == 1) {
            this.currentMonthIndex++
        } else if (i == 2) {
            this.currentMonthIndex--
        }

        loadIslamicCalendar(this.currentMonthIndex)
    }

    private fun initFunctionality() {

        val gridLayoutManager2 = GridLayoutManager(requireActivity(), 7)
        this.mLayoutManager = gridLayoutManager2
        binding.calenderView.recyclerViewIslamicCal.layoutManager = gridLayoutManager2
        val arrayList2: ArrayList<IslamicCalendarModel> = ArrayList()
        this.mResponseData = arrayList2
        val islamicCalendarAdapter = IslamicCalendarAdapter(arrayList2)
        this.mDataAdapter = islamicCalendarAdapter
        binding.calenderView.recyclerViewIslamicCal.adapter = islamicCalendarAdapter

        val mCal = Calendar.getInstance(Locale.getDefault())
        mCal.time = Date()
        mCal.add(Calendar.DAY_OF_MONTH, -1)

        val uCal = UmmalquraCalendar()
        uCal.time = mCal.time

        this.currentMonthIndex = uCal[2]

        val gridLayoutManager = GridLayoutManager(requireActivity(), 1)
        this.mLayoutManagerIslCht = gridLayoutManager
        binding.calenderView.recyclerViewIslamicChhuti.layoutManager = gridLayoutManager
        val arrayList: ArrayList<IslamicChhutiModel> = ArrayList()
        this.mResponseDataIslCht = arrayList
        val islamicChhutiAdapter = IslamicChhutiAdapter(arrayList)
        this.mDataAdapterIslCht = islamicChhutiAdapter
        binding.calenderView.recyclerViewIslamicChhuti.adapter = islamicChhutiAdapter
    }

    private fun loadJSONFromAsset(str: String): String {
        return try {
            val open: InputStream = requireActivity().getAssets().open(str)
            val bArr = ByteArray(open.available())
            open.read(bArr)
            open.close()
            String(bArr, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}
