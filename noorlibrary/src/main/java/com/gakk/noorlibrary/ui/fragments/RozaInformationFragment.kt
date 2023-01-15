package com.gakk.noorlibrary.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.NOTFICATION
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentRozaInformationBinding
import com.gakk.noorlibrary.databinding.LayoutRozaPrimaryHeaderBinding
import com.gakk.noorlibrary.model.roza.Data
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.ui.adapter.RozaInformationAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.RamadanTimingViewModel
import kotlinx.coroutines.launch


class RozaInformationFragment : Fragment(), DivisionSelectionCallback {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentRozaInformationBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationHelper = LocationHelper(requireContext())

        mDetailsCallBack?.setToolBarTitle(context?.resources!!.getString(R.string.cat_roja))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeTwo)
        mDetailsCallBack?.setOrUpdateActionButtonTag(NOTFICATION, ActionButtonType.TypeTwo)
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_roza_information, container, false)

        AppPreference.ramadanSehriIfterTimes?.let {
            ramadanIfterSehriTimes = it
        }
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


            loadData("Dhaka", "0")

            model.literatureListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        binding.noInternetLayout.root.visibility = GONE
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.noInternetLayout.root.visibility = VISIBLE
                        binding.progressLayout.root.visibility = GONE
                    }
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = GONE
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
                        if (binding.rvRozaInfo.adapter == null) {
                            adapter = RozaInformationAdapter(
                                this@RozaInformationFragment,
                                ramadanIfterSehriTimes,
                                nextTenDaysIfterSehriTimes,
                                list,
                                list2,
                                fromMalaysia, context
                            )
                            Log.e("TAG", "Message: " + list)
                            binding.rvRozaInfo.adapter = adapter
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
                        binding.noInternetLayout.root.visibility = GONE
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.noInternetLayout.root.visibility = VISIBLE
                        binding.progressLayout.root.visibility = GONE
                    }
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = GONE
                        var list2 = it.data?.data?.toMutableList()

                        //adapter.notifyDataSetChanged()

                        if (binding.rvRozaInfo.adapter == null) {
                            adapter = RozaInformationAdapter(
                                this@RozaInformationFragment,
                                ramadanIfterSehriTimes,
                                nextTenDaysIfterSehriTimes,
                                null,
                                list2!!,
                                fromMalaysia,
                                context

                            )
                            Log.e("TAG", "Message: " + list2)
                            binding.rvRozaInfo.adapter = adapter
                        } else {
                            adapter.mRamadanSehriIfterTimesFromAPI = list2!!
                            adapter.mNextTenDaysSehriIfterTimesFromAPI = list2!!
                            adapter.initData()
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }

        binding.noInternetLayout.btnRetry.handleClickEvent {
            loadData("Dhaka", "0")
        }

        return binding.root
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
        fun newInstance(detailsCallBack: DetailsCallBack) =
            RozaInformationFragment().apply {
               /* arguments = Bundle().apply {
                    putSerializable(ARG_DETAILS_CALL_BACK, detailsCallBack)
                }*/
            }
    }

    override fun showDivisionListAlert(binding: LayoutRozaPrimaryHeaderBinding) {
        mDetailsCallBack?.showDialogWithActionAndParam(
            dialogType = DialogType.RozaDivisionListDialog,
            binding = binding,
            divisionCallbackFunc = { name, index ->
                Log.i("TAG", "showDivisionListAlert: $name $index")
                // model.loadRamadanTimingData("Dhaka")
//                if(index==-1){
//                    binding.tvDivision.text = "Dhaka"
//                    adapter.selectedDivision = "Dhaka"
//                }
                if (index == 0) {
                    loadData("Dhaka", index.toString())
                    binding.tvDivision.text = "Dhaka"
                    adapter.selectedDivision = "Dhaka"
                    Log.i("TAG", "showDivisionListAlert: $name $index")
                }
                if (index == 1) {
                    loadData("Barisal", index.toString())
                    binding.tvDivision.text = "Barisal"
                    adapter.selectedDivision = "Barisal"
                }
                if (index == 2) {
                    loadData("Chattogram", index.toString())
                    binding.tvDivision.text = "Chittagong"
                    adapter.selectedDivision = "Chittagong"
                }

                if (index == 3) {
                    loadData("Sylhet", index.toString())
                    binding.tvDivision.text = "Sylhet"
                    adapter.selectedDivision = "Sylhet"
                }
                if (index == 4) {
                    loadData("Rangpur", index.toString())
                    binding.tvDivision.text = "Rangpur"
                    adapter.selectedDivision = "Rangpur"
                }
                if (index == 5) {
                    loadData("Rajshahi", index.toString())
                    binding.tvDivision.text = "Rajshahi"
                    adapter.selectedDivision = "Rajshahi"
                }
                if (index == 6) {
                    loadData("Khulna", index.toString())
                    binding.tvDivision.text = "Khulna"
                    adapter.selectedDivision = "Khulna"
                }
                if (index == 7) {
                    loadData("Mymensingh", index.toString())
                    binding.tvDivision.text = "Mymensingh"
                    adapter.selectedDivision = "Mymensingh"
                }
//                else{
//                    index ==0
//                    binding.tvDivision.
//                }
                adapter.notifyDataSetChanged()
                Log.e("TAG", "Message: called")
            }
        )


    }


}

interface DivisionSelectionCallback {
    fun showDivisionListAlert(binding: LayoutRozaPrimaryHeaderBinding)
}