package com.gakk.noorlibrary.ui.fragments.roja

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
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.ui.adapter.roja.RozaInformationAdapter
import com.gakk.noorlibrary.ui.fragments.DivisionSelectionCallback
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


class RozaInformationFragment : Fragment(), DivisionSelectionCallback {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentRozaInformationBinding
    private lateinit var adapter: RozaInformationAdapter
    private var ramadanIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()
    private var nextTenDaysIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()
    private lateinit var model: LiteratureViewModel
    private lateinit var homeViewmodel: HomeViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var repository: RestRepository
    private var fromMalaysia: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

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

        Log.e("times", "called" + nextTenDaysIfterSehriTimes.size)

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


            loadData()

            model.literatureListData.observe(viewLifecycleOwner, {
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
                        adapter = RozaInformationAdapter(
                            this@RozaInformationFragment,
                            ramadanIfterSehriTimes,
                            nextTenDaysIfterSehriTimes,
                            list,
                            fromMalaysia
                        )
                        binding.rvRozaInfo.adapter = adapter
                    }
                }
            })
        }
        binding.noInternetLayout.btnRetry.handleClickEvent {
            loadData()
        }

        return binding.root
    }

    fun loadData() {
        model.loadTextBasedLiteratureListBySubCategory(
            R.string.roza_cat_id.getLocalisedTextFromResId(),
            "undefined",
            "1"
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RozaInformationFragment()
    }

    override fun showDivisionListAlert(binding: LayoutRozaPrimaryHeaderBinding) {
        mDetailsCallBack?.showDialogWithActionAndParam(
            dialogType = DialogType.RozaDivisionListDialog,
            binding = binding
        )
    }
}