package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse
import com.gakk.noorlibrary.model.hajjpackage.PreRegisteredUserInfo
import com.gakk.noorlibrary.ui.adapter.PreregistrationListAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.setImageFromUrlNoProgress
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

internal class HajjPreRegistrationListFragment : Fragment(), PaymentControl {

    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var viewModel: HajjViewModel
    private lateinit var viewModelPreRegistration: PreregistrationViewModel

    //view
    private lateinit var rvPreregistration : RecyclerView
    private lateinit var progressLayout : ConstraintLayout
    private lateinit var noDataLayout: ConstraintLayout
    private lateinit var tvTitle : AppCompatTextView
    private lateinit var tvDes: AppCompatTextView
    private lateinit var imgNoInternet: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjPreRegistrationListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_pre_registration_list,
            container, false
        )

        initView(view)


        return view
    }

    private fun initView(view:View)
    {
        progressLayout = view.findViewById(R.id.progressLayout)
        rvPreregistration = view.findViewById(R.id.rvPreregistration)
        noDataLayout = view.findViewById(R.id.noDataLayout)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDes = view.findViewById(R.id.tvDes)
        imgNoInternet = view.findViewById(R.id.imgNoInternet)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()

        viewModelPreRegistration =
            ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewModel = ViewModelProvider(
                this@HajjPreRegistrationListFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()

            viewModel.loadPreRegistrationList()
        }

    }

    fun updateToolbarForThisFragment() {
        mCallback?.setToolBarTitle("আপনার প্রাক-নিবন্ধন সমূহ")
    }

    private fun subscribeObserver() {
        viewModel.preRegistrationList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val list = it.data?.data as MutableList<HajjPreRegistrationListResponse.Data>

                    if (list.size > 0) {
                        rvPreregistration.adapter =
                            PreregistrationListAdapter(list, mCallback, this)
                    } else {

                        val item = ImageFromOnline("bg_no_data.png")
                        setImageFromUrlNoProgress(imgNoInternet,item.fullImageUrl)
                        noDataLayout.visibility = View.VISIBLE
                        tvTitle.setText("প্রাক-নিবন্ধন নেই")
                        tvDes.visibility = View.VISIBLE
                        tvDes.setText("নতুন প্রাক-নিবন্ধিতদের এখানে যোগ করা হবে।")
                    }

                    progressLayout.visibility = View.GONE
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun gotoPaymentPage(trackingNo: String?, name: String?, email: String?) {
        viewModelPreRegistration.gotoNext(2)
        val preRegisteredUserInfo = PreRegisteredUserInfo(
            trackingNo,
            name,
            email
        )
        viewModelPreRegistration.selectedInfo(preRegisteredUserInfo)
        mCallback?.addFragmentToStackAndShow(
            HajjpreRegistrationDetailsFragment.newInstance()
        )
    }
}

interface PaymentControl {
    fun gotoPaymentPage(trackingNo: String?, name: String?, email: String?)
}