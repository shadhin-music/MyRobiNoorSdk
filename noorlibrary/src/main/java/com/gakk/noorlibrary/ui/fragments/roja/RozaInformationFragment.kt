package com.gakk.noorlibrary.ui.fragments.roja

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.ui.adapter.roja.RozaInformationAdapter
import com.gakk.noorlibrary.ui.fragments.DivisionSelectionCallback
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.HomeViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


internal class RozaInformationFragment : Fragment(), DivisionSelectionCallback {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var adapter: RozaInformationAdapter
    private var ramadanIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()
    private var nextTenDaysIfterSehriTimes: MutableList<IfterAndSehriTime> = ArrayList()
    private lateinit var model: LiteratureViewModel
    private lateinit var homeViewmodel: HomeViewModel
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

            model.literatureListData.observe(viewLifecycleOwner) {
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
                        progressLayout.visibility = GONE
                        val list = it.data?.data ?: mutableListOf()
                        adapter = RozaInformationAdapter(
                            this@RozaInformationFragment,
                            ramadanIfterSehriTimes,
                            nextTenDaysIfterSehriTimes,
                            list,
                            fromMalaysia
                        )
                        rvRozaInfo.adapter = adapter
                    }
                }
            }
        }
        btnRetry.handleClickEvent {
            loadData()
        }

        return view
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

    override fun showDivisionListAlert(view: View) {
        Log.e("showDivisionListAlert","Need to configure without binding")
    }
   /* override fun showDivisionListAlert(binding: LayoutRozaPrimaryHeaderBinding) {
        mDetailsCallBack?.showDialogWithActionAndParam(
            dialogType = DialogType.RozaDivisionListDialog,
            binding = binding
        )
    }*/
}