package com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.gakk.noorlibrary.model.hajjtracker.HajjSharingListResponse
import com.gakk.noorlibrary.ui.adapter.hajjtracker.HajjSharingListAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.runOnUiThread
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch


internal class ShareDialogFragment : Fragment(), ccp.OnCcpClickListener {

    private lateinit var bottomSheetDisplayCallback: BottomSheetDisplay
    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel
    private var sharingAdapter: HajjSharingListAdapter? = null
    private var country_code: String = "880"
    private val ccp_core = ccp(this@ShareDialogFragment)
    private lateinit var imgBackShare: AppCompatImageView
    private lateinit var ccpBtn: LinearLayout
    private lateinit var btnShareLocation: AppCompatButton
    private lateinit var etNumber: AppCompatEditText
    private lateinit var rvShareerList: RecyclerView
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
            R.layout.dialog_share_location_hajj,
            container, false
        )
        imgBackShare = view.findViewById(R.id.imgBackShare)
        ccpBtn = view.findViewById(R.id.ccpBtn)
        btnShareLocation = view.findViewById(R.id.btnShareLocation)
        etNumber = view.findViewById(R.id.etNumber)
        rvShareerList = view.findViewById(R.id.rvShareerList)
        ccp_result = view.findViewById(R.id.ccp_result)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val display: DisplayMetrics? = context?.resources?.displayMetrics
        var width: Int = display?.widthPixels ?: 0
        var height: Int = display?.heightPixels ?: 0
        width -= (width * 10) / 100
        height -= (height * 10) / 100


        imgBackShare.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(2)
        }


        context?.let { ccp_core.setup_ccp(it, ccpBtn) }


        btnShareLocation.handleClickEvent {
            val shareerNumber = etNumber.text.toString()
            model.locationShareRequest("${country_code.replace("+", "")}$shareerNumber")

            bottomSheetDisplayCallback.startLocationShareService()
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@ShareDialogFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.shareLocation.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("shareLocation", "SUCCESS")
                }

                Status.LOADING -> {
                    Log.e("shareLocation", "LOADING")
                }

                Status.ERROR -> {
                    Log.e("shareLocation", "ERROR")
                }
            }
        }
    }

    fun setListData(list: List<HajjSharingListResponse.Data>) {

        runOnUiThread {
            //if (sharingAdapter == null) {
            Log.e("fragment", "adapter null")
            sharingAdapter = HajjSharingListAdapter(list, bottomSheetDisplayCallback)
            rvShareerList.adapter = sharingAdapter
            /*  } else {
                  Log.e("fragment", "adapter not null")
                  sharingAdapter?.notifyDataSetChanged()
              }*/
        }
    }


    override fun onItemClick(postion: Int, ccp_list: ArrayList<CCPmodel>) {
        val result = ccp_list[postion]
        this.country_code = result.dialCode.toString()
        ccp_result.text = String.format("%1$2s %2$2s", result.countryCode, result.dialCode)
    }
}