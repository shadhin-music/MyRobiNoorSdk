package com.gakk.noorlibrary.ui.fragments.calender


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentIslamicCalenderBdBinding
import com.gakk.noorlibrary.model.calender.IslamicCalendarModel
import com.gakk.noorlibrary.model.calender.IslamicChhutiModel
import com.gakk.noorlibrary.ui.adapter.IslamicCalendarAdapter
import com.gakk.noorlibrary.ui.adapter.IslamicChhutiAdapter
import com.gakk.noorlibrary.util.CalendarUtility
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*


internal class IslamicCalenderFragmentBd : Fragment() {

    private lateinit var binding: FragmentIslamicCalenderBdBinding

    private var currentMonthIndex = 0
    private var mDataAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mResponseData: ArrayList<IslamicCalendarModel>? = null
    private var mCallback: DetailsCallBack? = null

    private var mResponseDataIslCht: ArrayList<IslamicChhutiModel>? = null
    private var mLayoutManagerIslCht: GridLayoutManager? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel

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

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@IslamicCalenderFragmentBd,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.islamic_chuti_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    val literatureList = it.data?.data ?: mutableListOf()
                    binding.calenderView.recyclerViewIslamicChhuti.adapter =
                        IslamicChhutiAdapter(literatureList)
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    private fun loadData() {
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
    }
}
