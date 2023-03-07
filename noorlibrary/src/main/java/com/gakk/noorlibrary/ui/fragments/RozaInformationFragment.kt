package com.gakk.noorlibrary.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.roza.Data
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.roza.IftarAndSehriTimeProvider
import com.gakk.noorlibrary.roza.IfterAndSehriTimePopulationControl
import com.gakk.noorlibrary.ui.adapter.RozaInformationAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.RamadanTimingViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


internal class RozaInformationFragment : Fragment(), DivisionSelectionCallback {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var adapter: RozaInformationAdapter
    private var list2: MutableList<Data> = ArrayList()
    private var ramadanIfterSehriTimes2: MutableList<IftarAndSheriTimeforBD> = ArrayList()
    private var nextTenDaysIfterSehriTimes2: MutableList<IftarAndSheriTimeforBD> = ArrayList()
    private var ramadanIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()
    private var nextTenDaysIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()

    private lateinit var model: LiteratureViewModel
    private lateinit var homeViewmodel: HomeViewModel
    private lateinit var ramadanTimingViewModel: RamadanTimingViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var repository: RestRepository
    private var fromMalaysia: Boolean = false
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var rvRozaInfo: RecyclerView
    private lateinit var btnRetry: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    private fun populateIfterAndSehriTimeForThisRamadan()
    {
        var firstRamadanMs = CalenderUtil.getFirstRmdnGrgMs()
        for (i in 0..29) {
            var ifterAndSehriTime =
                IftarAndSehriTimeProvider.getIfterAndSehriTimeFromGivenDateByGivenOffset(
                    firstRamadanMs,
                    i,
                    context
                )
            ramadanIfterSehriTimes.add(ifterAndSehriTime)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationHelper = LocationHelper(requireContext())

        mDetailsCallBack?.setToolBarTitle(context?.resources!!.getString(R.string.cat_roja))

        val view = inflater.inflate(
            R.layout.fragment_roza_information,
            container, false
        )

        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        progressLayout = view.findViewById(R.id.progressLayout)
        rvRozaInfo = view.findViewById(R.id.rvRozaInfo)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)



        /*AppPreference.ramadanSehriIfterTimes?.let {
            ramadanIfterSehriTimes = it
        }*/

        AppPreference.nextTenDaysSehriIfterTimes?.let {
            nextTenDaysIfterSehriTimes = it
        }
        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@RozaInformationFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            homeViewmodel = ViewModelProvider(
                this@RozaInformationFragment,
                HomeViewModel.FACTORY(repository)
            ).get(HomeViewModel::class.java)

            runBlocking {
                val job = launch(Dispatchers.Default) {
                    populateIfterAndSehriTimeForThisRamadan()
                }
                job.join()

               // Log.e("RR DATA",Gson().toJson(ramadanIfterSehriTimes))
                loadData("Dhaka", "0")
                //Log.e("RR DATA","ok")
            }



            model.literatureListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        noInternetLayout.visibility = GONE
                        progressLayout.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        noInternetLayout.visibility = VISIBLE
                        progressLayout.visibility = GONE
                    }
                    Status.SUCCESS -> {
                       // progressLayout.visibility = GONE
                        var list = it.data?.data ?: mutableListOf()
                        /* adapter = RozaInformationAdapter(
                             this@RozaInformationFragment,
                             ramadanIfterSehriTimes,
                             nextTenDaysIfterSehriTimes,
                             list,
                             list2,
                             fromMalaysia
                         )
                         Log.e("TAG","Message: "+ list)
                         binding.rvRozaInfo.adapter = adapter*/
                        Log.e("Init", " model.literatureListData.observe")
                        //Log.e("RAMADAN DATA","HELLO"+Gson().toJson(ramadanIfterSehriTimes))
                        if (rvRozaInfo.adapter == null) {
                            /*adapter = RozaInformationAdapter(
                                this@RozaInformationFragment,
                                ramadanIfterSehriTimes,
                                nextTenDaysIfterSehriTimes,
                                list,
                                list2,
                                fromMalaysia
                            )
                            Log.e("TAG", "Message: " + list)
                            rvRozaInfo.adapter = adapter*/
                            //todo PLEASE FIX
                            crashFixHack(list, list2)
                        } else {
                            adapter.mDuaList = list
                            adapter.duaItemCount = list.size
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            })

            model.ramadanListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        noInternetLayout.visibility = GONE
                        progressLayout.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        noInternetLayout.visibility = VISIBLE
                        progressLayout.visibility = GONE
                    }
                    Status.SUCCESS -> {
                        var list2 = it.data?.data?.toMutableList()

                        if (rvRozaInfo.adapter == null) {
                            Log.e("Init", "ramadanListData.observe")
                           /* adapter = RozaInformationAdapter(
                                this@RozaInformationFragment,
                                ramadanIfterSehriTimes,
                                nextTenDaysIfterSehriTimes,
                                null,
                                list2!!,
                                fromMalaysia

                            )
                            Log.e("TAG", "Message: " + list2)
                            rvRozaInfo.adapter = adapter*/
                            //todo PLEASE FIX
                            crashFixHack(null, list2!!)

                        } else {
                            adapter.mRamadanSehriIfterTimesFromAPI = list2!!
                            adapter.mNextTenDaysSehriIfterTimesFromAPI = list2!!

                            rvRozaInfo.post {
                                adapter.initDataNew()
                                adapter.notifyDataSetChanged()

                            }
                            //adapter.notifyDataSetChanged()
                        }
                        progressLayout.visibility = GONE
                    }
                }
            })
        }

        btnRetry.handleClickEvent {
            loadData("Dhaka", "0")
        }

        return view
    }
    //todo please change (HACK BUG FIX)
    var duaList: MutableList<Literature> = arrayListOf()
    var noIdeaWhatList2Is: MutableList<Data> = arrayListOf()
    private fun crashFixHack(list_1: MutableList<Literature>?, list_2: MutableList<Data>?){
        list_1?.let {
            if(it.isNotEmpty()) {
                duaList = it
            }
        }
        list_2?.let {
            if(it.isNotEmpty()) {
                noIdeaWhatList2Is = it
            }
        }
        Log.e("crashFixHack", "duaList:"+duaList.size+",  noIdeaWhatList2Is:"+noIdeaWhatList2Is.size)
        if (duaList.isNotEmpty() && noIdeaWhatList2Is.isNotEmpty()){
            progressLayout.visibility = GONE
            if(nextTenDaysIfterSehriTimes.isNullOrEmpty()){
                val firstRamadanMs = CalenderUtil.getFirstRmdnGrgMs()
                nextTenDaysIfterSehriTimes = arrayListOf()
                for (i in 0..29) {
                    val ifterAndSehriTime =
                        IftarAndSehriTimeProvider.getIfterAndSehriTimeFromGivenDateByGivenOffset(
                            firstRamadanMs,
                            i,
                            context
                        )
                    nextTenDaysIfterSehriTimes.add(
                        ifterAndSehriTime
                    )
                }
            }
            Log.e("Init", "ramadanIfterSehriTimes:"+ramadanIfterSehriTimes.size+" nextTenDaysIfterSehriTimes:"+nextTenDaysIfterSehriTimes.size)
            adapter = RozaInformationAdapter(
                this@RozaInformationFragment,
                ramadanIfterSehriTimes,
                nextTenDaysIfterSehriTimes,
                duaList,
                noIdeaWhatList2Is,
                fromMalaysia

            )
            rvRozaInfo.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun loadData(name: String, index: String) {

        if (fromMalaysia == false) {
            model.loadRamadanTimingData(name)
            model.loadTextBasedLiteratureListBySubCategory(
                R.string.roza_cat_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )
        } else {
            model.loadTextBasedLiteratureListBySubCategory(
                R.string.roza_cat_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RozaInformationFragment()
    }

    override fun showDivisionListAlert(view: View) {
        val tvDivision = view.findViewById<AppCompatTextView>(R.id.tvDivision)
        mDetailsCallBack?.showDialogWithActionAndParam(
            dialogType = DialogType.RozaDivisionListDialog,
            binding = view,
            divisionCallbackFunc = { name, index ->
                Log.i("TAG", "showDivisionListAlert: $name $index")

                if (index == 0) {
                    loadData("Dhaka", index.toString())
                    tvDivision.text = "Dhaka"
                    adapter.selectedDivision = "Dhaka"
                    Log.i("TAG", "showDivisionListAlert: $name $index")
                }
                if (index == 1) {
                    loadData("Barisal", index.toString())
                    tvDivision.text = "Barisal"
                    adapter.selectedDivision = "Barisal"
                }
                if (index == 2) {
                    loadData("Chattogram", index.toString())
                    tvDivision.text = "Chittagong"
                    adapter.selectedDivision = "Chittagong"
                }

                if (index == 3) {
                    loadData("Sylhet", index.toString())
                    tvDivision.text = "Sylhet"
                    adapter.selectedDivision = "Sylhet"
                }
                if (index == 4) {
                    loadData("Rangpur", index.toString())
                    tvDivision.text = "Rangpur"
                    adapter.selectedDivision = "Rangpur"
                }
                if (index == 5) {
                    loadData("Rajshahi", index.toString())
                    tvDivision.text = "Rajshahi"
                    adapter.selectedDivision = "Rajshahi"
                }
                if (index == 6) {
                    loadData("Khulna", index.toString())
                    tvDivision.text = "Khulna"
                    adapter.selectedDivision = "Khulna"
                }
                if (index == 7) {
                    loadData("Mymensingh", index.toString())
                    tvDivision.text = "Mymensingh"
                    adapter.selectedDivision = "Mymensingh"
                }

                BaseActivity.alertDialog.dismiss()
                BaseActivity.selectedDivision = index
                adapter.notifyDataSetChanged()
                Log.e("TAG", "Message: called")
            }
        )
    }




}

interface DivisionSelectionCallback {
    fun showDivisionListAlert(view: View)
}