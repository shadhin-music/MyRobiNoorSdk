package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.activity.SubscriptionBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */


internal class SslSubscriptionFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var repository: RestRepository
    private lateinit var btnSubscribeMonthly: AppCompatButton
    private lateinit var btnSubscribeHalfYearly: AppCompatButton
    private lateinit var btnSubscribeYearly: AppCompatButton
    private lateinit var ivShapeSubMonthly: AppCompatImageView
    private lateinit var ivShapeSubYearly: AppCompatImageView
    private lateinit var tvAmount: AppCompatTextView
    private lateinit var tvContent: AppCompatTextView
    private lateinit var ivShapeHalfYearly: AppCompatImageView
    private lateinit var tvAmountHalfYearly: AppCompatTextView
    private lateinit var tvContentHalfYearly: AppCompatTextView
    private lateinit var tvAmountYearly: AppCompatTextView
    private lateinit var tvContentYearly: AppCompatTextView
    private lateinit var progressLayout: ConstraintLayout


    companion object {
        @JvmStatic
        fun newInstance() =
            SslSubscriptionFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_subscription_nagad,
            container, false
        )

        initUi(view)

        return view
    }

    private fun initUi(view: View) {
        btnSubscribeMonthly = view.findViewById(R.id.btnSubscribeMonthly)
        btnSubscribeHalfYearly = view.findViewById(R.id.btnSubscribeHalfYearly)
        btnSubscribeYearly = view.findViewById(R.id.btnSubscribeYearly)
        ivShapeSubMonthly = view.findViewById(R.id.ivShapeSubMonthly)
        ivShapeHalfYearly = view.findViewById(R.id.ivShapeHalfYearly)
        ivShapeSubYearly = view.findViewById(R.id.ivShapeSubYearly)
        tvAmount = view.findViewById(R.id.tvAmount)
        tvContent = view.findViewById(R.id.tvContent)
        tvAmountHalfYearly = view.findViewById(R.id.tvAmountHalfYearly)
        tvContentHalfYearly = view.findViewById(R.id.tvContentHalfYearly)
        tvAmountYearly = view.findViewById(R.id.tvAmountYearly)
        tvContentYearly = view.findViewById(R.id.tvContentYearly)
        progressLayout = view.findViewById(R.id.progressLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.page_title_subscription))
        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewModel = ViewModelProvider(
                this@SslSubscriptionFragment,
                SubscriptionViewModel.FACTORY(repository)
            ).get(SubscriptionViewModel::class.java)

            AppPreference.userNumber?.let {
                viewModel.checkSslSubStatusMonthly(it, SSL_SERVICE_ID_MONTHLY)
                viewModel.checkSslSubStatusHalfYearly(it, SSL_SERVICE_ID_HALF_YEARLY)
                viewModel.checkSslSubStatusYearly(it, SSL_SERVICE_ID_YEARLY)
            }

            subscribeObserver()
        }

        btnSubscribeMonthly.handleClickEvent {
            if (AppPreference.subHalfYearlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Half Yearly plan first!")
            } else if (AppPreference.subYearlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Yearly plan first!")
            } else if (AppPreference.subDaily || AppPreference.subFifteenDays) {
                mCallback?.showToastMessage("You are Already subscribed")
            } else {
                if (AppPreference.subMonthlySsl) {
                    Log.e("Monthly", "Subscribed")
                } else {
                    val serviceId: String
                    serviceId = SSL_SERVICE_ID_MONTHLY
                    paymentApiLoad(serviceId)
                }
            }
        }

        btnSubscribeHalfYearly.handleClickEvent {

            if (AppPreference.subMonthlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Monthly plan first!")
            } else if (AppPreference.subYearlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Yearly plan first!")
            } else if (AppPreference.subDaily || AppPreference.subFifteenDays) {
                mCallback?.showToastMessage("You are Already subscribed")
            } else {
                if (AppPreference.subHalfYearlySsl) {
                    Log.e("HalfYearly", "Subscribed")
                } else {
                    val serviceId: String

                    serviceId = SSL_SERVICE_ID_HALF_YEARLY

                    paymentApiLoad(serviceId)
                }
            }

        }
        btnSubscribeYearly.handleClickEvent {

            if (AppPreference.subMonthlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Monthly plan first!")
            } else if (AppPreference.subHalfYearlySsl) {
                mCallback?.showToastMessage("Please unsubscribe Half Yearly plan first!")
            } else if (AppPreference.subDaily || AppPreference.subFifteenDays) {
                mCallback?.showToastMessage("You are Already subscribed")
            } else {
                if (AppPreference.subYearlySsl) {
                    Log.e("yearly", "Subscribed")
                } else {

                    val serviceId: String
                    serviceId = SSL_SERVICE_ID_YEARLY
                    paymentApiLoad(serviceId)
                }
            }
        }
    }

    private fun paymentApiLoad(serviceId: String) {
        AppPreference.userNumber?.let {
            viewModel.initiatePaymentSsl(
                it,
                serviceId,
                SSL_CUSTOMER_NAME,
                SSL_CUSTOMER_EMAIL
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (this::viewModel.isInitialized) {
            AppPreference.userNumber?.let {
                viewModel.checkSslSubStatusMonthly(it, SSL_SERVICE_ID_MONTHLY)
                viewModel.checkSslSubStatusHalfYearly(it, SSL_SERVICE_ID_HALF_YEARLY)
                viewModel.checkSslSubStatusYearly(it, SSL_SERVICE_ID_YEARLY)
            }

        }
    }

    private fun subscribeObserver() {

        viewModel.paymentSsl.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("paymentNagad", "LOADING")
                    progressLayout.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    Log.e("paymentNagad", "SUCCESS${it.data?.errorCode}")
                    if (it.data?.errorCode.equals("200")) {
                        if (it.data?.gatewayPageURL?.isNotEmpty() == true) {
                            context?.startActivity(
                                Intent(
                                    context,
                                    SubscriptionBrowserActivity::class.java
                                ).putExtra(PAYMENT_METHOD_TAG, true)
                                    .putExtra(PAYMENT_URL_TAG, it.data.gatewayPageURL)
                            )
                        }
                    } else {
                        mCallback?.showToastMessage("Try again!")
                    }

                }

                Status.ERROR -> {
                    Log.e("paymentNagad", "ERROR ${it.message}")
                    progressLayout.visibility = View.GONE
                }
            }
        }


        viewModel.sslSubInfoMonthly.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                    Log.e("sslSubInfoMonthly", "LOADING")
                }

                Status.SUCCESS -> {
                    Log.e("sslSubInfoMonthly", "SUCCESS")

                    updateUiMonthly(it.data?.response)
                    when (it.data?.response) {
                        "1AC" -> {
                            AppPreference.subMonthlySsl = true
                        }

                        else -> {
                            AppPreference.subMonthlySsl = false
                        }
                    }
                    progressLayout.visibility = View.GONE
                }

                Status.ERROR -> {
                    Log.e("sslSubInfoMonthly", "ERROR")
                    progressLayout.visibility = View.GONE
                }
            }
        }

        viewModel.sslSubInfoHalfYearly.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("sslSubInfoHalfYearly", "LOADING")
                }

                Status.SUCCESS -> {
                    Log.e("sslSubInfoHalfYearly", "SUCCESS")

                    updateUiHalfYearly(it.data?.response)
                    when (it.data?.response) {
                        "1AC" -> {
                            AppPreference.subHalfYearlySsl = true
                        }

                        else -> {
                            AppPreference.subHalfYearlySsl = false
                        }
                    }
                }

                Status.ERROR -> {
                    Log.e("sslSubInfoHalfYearly", "ERROR")
                }
            }
        }

        viewModel.sslSubInfoYearly.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("sslSubInfoYearly", "LOADING")
                }

                Status.SUCCESS -> {
                    Log.e("sslSubInfoYearly", "SUCCESS")

                    updateUiYearly(it.data?.response)
                    when (it.data?.response) {
                        "1AC" -> {
                            AppPreference.subYearlySsl = true
                        }

                        else -> {
                            AppPreference.subYearlySsl = false
                        }
                    }
                }

                Status.ERROR -> {
                    Log.e("sslSubInfoYearly", "ERROR")
                }
            }
        }
    }

    private fun updateUiMonthly(response: String?) {

        when (response) {
            "1AC" -> {
                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                btnSubscribeMonthly.setText(getString(R.string.txt_subscribed))
                btnSubscribeMonthly.setTextColor(Color.WHITE)
                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                tvContent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_unsub)
            }

            else -> {
                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                btnSubscribeMonthly.setText(getString(R.string.txt_sub))
                btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
            }
        }
    }

    private fun updateUiHalfYearly(response: String?) {
        when (response) {
            "1AC" -> {

                ivShapeHalfYearly.setImageResource(R.drawable.ic_shape_sub_disable)
                btnSubscribeHalfYearly.setText(getString(R.string.txt_subscribed))
                btnSubscribeHalfYearly.setTextColor(Color.WHITE)
                tvAmountHalfYearly.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                tvContentHalfYearly.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                btnSubscribeHalfYearly.setBackgroundResource(R.drawable.ic_button_unsub)
            }

            else -> {

                ivShapeHalfYearly.setImageResource(R.drawable.ic_shape_sub)
                btnSubscribeHalfYearly.setText(getString(R.string.txt_sub))
                btnSubscribeHalfYearly.setBackgroundResource(R.drawable.ic_button_small)
            }
        }
    }


    private fun updateUiYearly(response: String?) {
        when (response) {
            "1AC" -> {
                ivShapeSubYearly.setImageResource(R.drawable.ic_shape_sub_disable)
                btnSubscribeYearly.setText(getString(R.string.txt_subscribed))
                btnSubscribeYearly.setTextColor(Color.WHITE)
                tvAmountYearly.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                tvContentYearly.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_color_title
                    )
                )
                btnSubscribeYearly.setBackgroundResource(R.drawable.ic_button_unsub)
            }

            else -> {
                ivShapeSubYearly.setImageResource(R.drawable.ic_shape_sub)
                btnSubscribeYearly.setText(getString(R.string.txt_sub))
                btnSubscribeYearly.setBackgroundResource(R.drawable.ic_button_small)
            }
        }
    }
}