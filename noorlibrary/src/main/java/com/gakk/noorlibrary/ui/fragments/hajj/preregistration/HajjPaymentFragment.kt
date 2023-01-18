package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentHajjPaymentBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.PreRegistrationBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import kotlinx.coroutines.launch

internal class HajjPaymentFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentHajjPaymentBinding
    private lateinit var viewModel: PreregistrationViewModel
    private lateinit var viewModelSub: SubscriptionViewModel
    private lateinit var viewModelHajj: HajjViewModel
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var trackingNo: String? = null
    private var name: String? = null
    private var email: String? = null
    private var literatureList: MutableList<Literature> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hajj_payment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        viewModel.selectedUserInfo.observe(viewLifecycleOwner) {
            trackingNo = it?.trackingNo
            name = it?.name
            email = it?.email
        }

        binding.cardMfsPayment.setOnClickListener {

            if (!binding.appCompatCheckBox.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "শর্তাবলীতে সম্মতি প্রদান করুন ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModelSub.initiatePaymentSsl(
                    trackingNo!!,
                    HAJJ_PREREG_SERVICE_ID,
                    name!!,
                    SSL_CUSTOMER_EMAIL
                )
            }
        }

        binding.cardPayment.handleClickEvent {
            binding.cardMfsPayment.performClick()
        }

        binding.cardBankPayment.handleClickEvent {
            binding.cardMfsPayment.performClick()
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            viewModelSub = ViewModelProvider(
                this@HajjPaymentFragment,
                SubscriptionViewModel.FACTORY(repository)
            ).get(SubscriptionViewModel::class.java)

            viewModelHajj = ViewModelProvider(
                this@HajjPaymentFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)


            model = ViewModelProvider(
                this@HajjPaymentFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.hajj_pre_registration_category_id),
                getString(R.string.hajj_preregistration_sub_category_id),
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
                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()

                    binding.literature = literatureList.get(0)

                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }

        viewModelSub.paymentSsl.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("paymentSslHajj", "LOADING")
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    Log.e("paymentSslHajj", "SUCCESS${it.data?.errorCode}")
                    if (it.data?.errorCode.equals("200")) {
                        if (it.data?.gatewayPageURL?.isNotEmpty() == true) {
                            if (isNetworkConnected(requireContext())) {
                                context?.startActivity(
                                    Intent(
                                        context,
                                        PreRegistrationBrowserActivity::class.java
                                    ).putExtra(TRACKING_NO_TAG, trackingNo)
                                        .putExtra(PAYMENT_URL_TAG, it.data.gatewayPageURL)
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
                    Log.e("paymentSslHajj", "ERROR")
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjPaymentFragment()
    }

}

