package com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.country_code_picker.CCPmodel
import com.gakk.noorlibrary.extralib.country_code_picker.ccp
import com.gakk.noorlibrary.model.hajjtracker.HajjTrackingListResponse
import com.gakk.noorlibrary.ui.adapter.hajjtracker.HajjTrackingListAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.runOnUiThread
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch


internal class TrackerDialogFragment : Fragment(), TrackerListControl, ccp.OnCcpClickListener {

    private lateinit var model: HajjViewModel
    private lateinit var repository: RestRepository
    private lateinit var bottomSheetDisplayCallback: BottomSheetDisplay
    private var trackingAdapter: HajjTrackingListAdapter? = null
    private var country_code: String = "880"
    private val ccp_core = ccp(this@TrackerDialogFragment)
    private lateinit var imgBackShare: AppCompatImageView
    private lateinit var ccpBtn: LinearLayout
    private lateinit var btnShareLocation: AppCompatButton
    private lateinit var etNumber: AppCompatEditText
    private lateinit var rvTrackerList: RecyclerView
    private lateinit var ccp_result: TextView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomSheetDisplayCallback = context as BottomSheetDisplay
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.dialog_track_location_hajj,
            container, false
        )
        imgBackShare = view.findViewById(R.id.imgBackShare)
        ccpBtn = view.findViewById(R.id.ccpBtn)
        btnShareLocation = view.findViewById(R.id.btnShareLocation)
        rvTrackerList = view.findViewById(R.id.rvTrackerList)
        ccp_result = view.findViewById(R.id.ccp_result)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBackShare.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(2)
        }

        context?.let { ccp_core.setup_ccp(it, ccpBtn) }

        btnShareLocation.handleClickEvent {
            val trackerNumber = etNumber.text.toString()
            model.locationTrackRequestFromSharer("${country_code.replace("+", "")}$trackerNumber")
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@TrackerDialogFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
            model.loadTrackingList()

        }
    }

    private fun subscribeObserver() {
        model.trackRequest.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("trackRequest", "SUCCESS")
                }

                Status.LOADING -> {
                    Log.e("trackRequest", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("trackRequest", "ERROR")
                }
            }
        }

        model.trackList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    trackingAdapter =
                        HajjTrackingListAdapter(it.data?.data, bottomSheetDisplayCallback, this)
                    rvTrackerList.adapter = trackingAdapter
                }

                Status.LOADING -> {
                    Log.e("trackList", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("trackList", "ERROR")
                }
            }
        }

        model.deleteData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("deleteData", "SUCCESS")
                    model.loadTrackingList()
                }

                Status.LOADING -> {
                    Log.e("deleteData", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("deleteData", "ERROR")
                }
            }
        }
    }

    fun setListData(list: List<HajjTrackingListResponse.Data>?) {

        runOnUiThread {
            if (trackingAdapter == null) {
                Log.e("fragment", "adapter null")
                trackingAdapter = HajjTrackingListAdapter(list, bottomSheetDisplayCallback, this)
                rvTrackerList.adapter = trackingAdapter
            } else {
                Log.e("fragment", "adapter not null")
                trackingAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun deleteHajjUser(id: String?) {
        if (id != null) {
            model.deleteDataHajj(id)
        }
    }

    override fun onItemClick(postion: Int, ccp_list: ArrayList<CCPmodel>) {
        val result = ccp_list[postion]
        this.country_code = result.dialCode.toString()
        ccp_result.text = String.format("%1$2s %2$2s", result.countryCode, result.dialCode)
    }

}

interface TrackerListControl {
    fun deleteHajjUser(id: String?)
}