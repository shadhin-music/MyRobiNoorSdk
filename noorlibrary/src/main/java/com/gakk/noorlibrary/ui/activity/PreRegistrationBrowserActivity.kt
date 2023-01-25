package com.gakk.noorlibrary.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.ActivitySubscriptionBrowserBinding
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcc.noor.ui.fragments.payment.PaymentResource
import kotlinx.coroutines.launch


internal class PreRegistrationBrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBrowserBinding
    private lateinit var viewModelHajj: HajjViewModel
    private lateinit var repository: RestRepository

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApplicationLanguage(AppPreference.language!!)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_browser)

        val url: String?
        val trackongNo: String?
        val paymentTag: String?

        intent.let {
            trackongNo = intent.getStringExtra(TRACKING_NO_TAG)
            url = intent.getStringExtra(PAYMENT_URL_TAG)
            paymentTag = intent.getStringExtra(PAYMENT_STATUS_TAG)
        }


        if (trackongNo == null || paymentTag == null) {
            finish()
            return
        }

        binding.toolBar.root.visibility = View.GONE

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.useWideViewPort = true
        binding.webview.settings.loadWithOverviewMode = true
        binding.progressBar.visibility = View.VISIBLE

        if (url != null) {
            binding.webview.loadUrl(url)
        }

        binding.webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar.visibility = View.GONE
                Log.e("onPageFinished", url)
                if (url.contains("SSLPaySuccessCallBack")) {
                    trackongNo.let {
                        viewModelHajj.updatePaymentStatus(it, paymentTag.toString())
                    }
                } else if (url.contains("SSLPayFailCallBack")) {
                    showPaymentStatusDialog(0)
                }
            }
        })
        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            viewModelHajj = ViewModelProvider(
                this@PreRegistrationBrowserActivity,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)


            subscribeObserver()
        }

    }

    private fun subscribeObserver() {

        viewModelHajj.paymentStatus.observe(this@PreRegistrationBrowserActivity) {

            when (it) {
                is PaymentResource.Error -> {
                    showPaymentStatusDialog(0)
                    binding.progressBar.visibility = View.GONE
                }
                PaymentResource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is PaymentResource.hajj_pre_reg -> paymentSuccess()
                is PaymentResource.umrah_hajj_reg -> paymentSuccess()
            }

        }
    }

    private fun paymentSuccess() {
        showPaymentStatusDialog(1)

        binding.progressBar.visibility = View.GONE
    }

    fun showPaymentStatusDialog(status: Int) {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )

        val view = layoutInflater.inflate(R.layout.dialog_hajj_refund, null)

        val dialogView: View = view
        customDialog.setView(dialogView)

        val imgChecked = view.findViewById<AppCompatImageView>(R.id.imgChecked)
        val tvTitleThank = view.findViewById<AppCompatTextView>(R.id.tvTitleThank)
        val tvDesRefund = view.findViewById<AppCompatTextView>(R.id.tvDesRefund)
        val textViewNormal5 = view.findViewById<AppCompatTextView>(R.id.textViewNormal5)
        val rlBtn = view.findViewById<RelativeLayout>(R.id.rlBtn)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        if (status == 0) {
            imgChecked.setImageResource(R.drawable.ic_failed)
            tvTitleThank.setText("দুঃখিত মুহতারাম!")
            tvDesRefund.setText("আপনার পেমেন্ট সফল হয়নি। অনুগ্রহ করে আবার চেষ্টা করুন অথবা আমাদের সাথে যোগাযোগ করুন।")
            textViewNormal5.setText("আবার চেষ্টা করুন")
        } else {
            tvTitleThank.setText("ধন্যবাদ মুহতারাম!")
            tvDesRefund.setText("আপনার পেমেন্ট সম্পন্ন হয়েছে। খুব শীঘ্রই আমাদের প্রতিনিধি আপনার সাথে যোগাযোগ করবে, ইনশাআল্লাহ।")
        }

        rlBtn.handleClickEvent {
            alertDialog.dismiss()
            finish()
        }
    }

}