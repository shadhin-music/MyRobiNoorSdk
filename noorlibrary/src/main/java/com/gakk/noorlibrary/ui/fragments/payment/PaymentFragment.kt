package com.mcc.noor.ui.fragments.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.activity.PreRegistrationBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import kotlinx.coroutines.launch

private const val ARG_NAME = "payment_UserName"
private const val ARG_AMOUNT = "payment_Amount"
private const val ARG_EMAIL = "payment_UserEmail"
private const val ARG_TRACKING = "payment_TrackingNumber"
private const val ARG_SERVICE_ID = "payment_ServiceID"
private const val ARG_PAYMENT_BACK_TITLE = "payment_Back_Titile"
private const val ARG_PAYMENT_PAGE_TITLE = "payment_page_Titile"
private const val ARG_PAYMENT_PAGE_SUB_TITLE = "payment_page_sub_Titile"
private const val ARG_TERMS_LINK = "payment_term_link"

class PaymentFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var paymentUsername:String
    private lateinit var paymentAmount:String
    private lateinit var paymentEmail:String
    private lateinit var paymentTracking:String
    private lateinit var paymentServiceID:String
    private lateinit var paymentBackTitile:String
    private lateinit var paymentPageTitile:String
    private lateinit var paymentStatusTag:String
    private lateinit var pageSubTitile:String
    private lateinit var payemntTermsLink:String
    private lateinit var viewModelSub: SubscriptionViewModel
    private lateinit var repository: RestRepository

    //view

    private lateinit var tvPreReg:AppCompatTextView
    private lateinit var payamount:AppCompatTextView
    private lateinit var cardMfsPayment:CardView
    private lateinit var appCompatCheckBox:AppCompatCheckBox
    private lateinit var cardPayment:CardView
    private lateinit var cardBankPayment:CardView
    private lateinit var tvTerm:AppCompatTextView
    private lateinit var progressLayout:ConstraintLayout


    companion object {

        @JvmStatic
        fun newInstance(
            name:String,
            email:String,
            amount:String,
            tracking:String,
            serviceID:String,
            backTitle:String,
            pageTitile:String,
            payStatusTag:String,
            pageSubTitile:String,
            termLink:String
        ) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME,name)
                    putString(ARG_EMAIL,email)
                    putString(ARG_AMOUNT,amount)
                    putString(ARG_TRACKING,tracking)
                    putString(ARG_SERVICE_ID,serviceID)
                    putString(ARG_PAYMENT_BACK_TITLE,backTitle)
                    putString(ARG_PAYMENT_PAGE_TITLE,pageTitile)
                    putString(PAYMENT_STATUS_TAG,payStatusTag)
                    putString(ARG_PAYMENT_PAGE_SUB_TITLE,pageSubTitile)
                    putString(ARG_TERMS_LINK,termLink)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack

        arguments.let {
            if (it != null) {
                paymentUsername = it.getString(ARG_NAME).toString()
                paymentEmail = it.getString(ARG_EMAIL).toString()
                paymentAmount = it.getString(ARG_AMOUNT).toString()
                paymentTracking = it.getString(ARG_TRACKING).toString()
                paymentServiceID = it.getString(ARG_SERVICE_ID).toString()
                paymentBackTitile = it.getString(ARG_PAYMENT_BACK_TITLE).toString()
                paymentPageTitile = it.getString(ARG_PAYMENT_PAGE_TITLE).toString()
                paymentStatusTag = it.getString(PAYMENT_STATUS_TAG).toString()
                pageSubTitile = it.getString(ARG_PAYMENT_PAGE_SUB_TITLE).toString()
                payemntTermsLink = it.getString(ARG_TERMS_LINK).toString()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        val view = inflater.inflate(
            R.layout.fragment_payment,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {

        tvPreReg = view.findViewById(R.id.tvPreReg)
        payamount = view.findViewById(R.id.payamount)
        cardMfsPayment = view.findViewById(R.id.cardMfsPayment)
        appCompatCheckBox = view.findViewById(R.id.appCompatCheckBox)
        cardPayment = view.findViewById(R.id.cardPayment)
        cardBankPayment = view.findViewById(R.id.cardBankPayment)
        tvTerm = view.findViewById(R.id.tvTerm)
        progressLayout = view.findViewById(R.id.progressLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(paymentPageTitile != "") {
            mCallback?.setToolBarTitle(paymentPageTitile)
        }
        else mCallback?.setToolBarTitle(resources.getString(R.string.payment_title))


        tvPreReg.text = pageSubTitile
        payamount.text = paymentAmount

        lifecycleScope.launch {

            repository = RepositoryProvider.getRepository()

            viewModelSub = ViewModelProvider(
                this@PaymentFragment,
                SubscriptionViewModel.FACTORY(repository)
            ).get(SubscriptionViewModel::class.java)

            initObserver()

        }
        cardMfsPayment.setOnClickListener {

            if (!appCompatCheckBox.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "শর্তাবলীতে সম্মতি প্রদান করুন ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                    viewModelSub.initiatePaymentSsl(
                        paymentTracking,
                        paymentServiceID,
                        paymentUsername,
                        paymentEmail

                    )

            }
        }

        cardPayment.handleClickEvent {
            cardMfsPayment.performClick()
        }

        cardBankPayment.handleClickEvent {
            cardMfsPayment.performClick()
        }

        tvTerm.handleClickEvent {
            if(payemntTermsLink !="")
            mCallback?.openUrl(payemntTermsLink)
        }

    }

    private fun  initObserver()
    {

        viewModelSub.paymentSsl.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("paymentSslHajj", "LOADING")
                    progressLayout.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    Log.e("paymentSslHajj", "SUCCESS${it.data?.errorCode}")
                    if (it.data?.errorCode.equals("200")) {
                        if (it.data?.gatewayPageURL?.isNotEmpty() == true) {
                            if (isNetworkConnected(requireContext())) {
                                context?.startActivity(
                                    Intent(
                                        context,
                                        PreRegistrationBrowserActivity::class.java
                                    ).putExtra(TRACKING_NO_TAG, paymentTracking)
                                        .putExtra(PAYMENT_URL_TAG, it.data.gatewayPageURL)
                                        .putExtra(PAYMENT_STATUS_TAG, paymentStatusTag)
                                )
                                requireActivity().finish()
                            } else {
                                mCallback?.showToastMessage("Please check internet connection!")
                            }

                        }
                    } else {
                        mCallback?.showToastMessage("Try again!")
                    }
                }

                Status.ERROR -> {
                    Log.e("UMRAH PAYMENT", "ERROR")
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if(paymentBackTitile !="")
            mCallback?.setToolBarTitle(paymentBackTitile)

    }

}