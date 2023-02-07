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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gakk.noorlibrary.ui.fragments.payment.PaymentResource
import kotlinx.coroutines.launch


internal class PreRegistrationBrowserActivity : AppCompatActivity() {

    private lateinit var progressBar:ProgressBar
    private lateinit var webview:WebView
    private lateinit var toolBar:ConstraintLayout


    //private lateinit var binding: ActivitySubscriptionBrowserBinding
    private lateinit var viewModelHajj: HajjViewModel
    private lateinit var repository: RestRepository
    private  lateinit var paymentTag: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView( R.layout.activity_subscription_browser)
        setupUi()

        val url: String?
        val trackongNo: String?

        intent.let {
            trackongNo = intent.getStringExtra(TRACKING_NO_TAG)
            url = intent.getStringExtra(PAYMENT_URL_TAG)
            paymentTag = intent.getStringExtra(PAYMENT_STATUS_TAG).toString()
        }


        if(trackongNo == null || paymentTag == null) {
            finish()
            return
        }

        toolBar.visibility = View.GONE

        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.useWideViewPort = true
        webview.settings.loadWithOverviewMode = true
        progressBar.visibility = View.VISIBLE

        if (url != null) {
           webview.loadUrl(url)
        }

        webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
                Log.e("onPageFinished", url)
                if (url.contains("SSLPaySuccessCallBack")) {
                    trackongNo.let {
                        when(paymentTag)
                        {
                            PAYMENT_DONATION -> showPaymentStatusDialog(1)
                            else -> viewModelHajj.updatePaymentStatus(it,paymentTag)
                        }

                    }
                } else if (url.contains("SSLPayFailCallBack")) {
                    Log.e("TEST","OK")
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
                HajjViewModel.FACTORY(this@PreRegistrationBrowserActivity.repository)
            ).get(HajjViewModel::class.java)


            subscribeObserver()
        }

    }

    private fun setupUi() {
        progressBar = findViewById(R.id.progressBar)
        webview = findViewById(R.id.webview)
        toolBar = findViewById(R.id.toolBar)
    }

    private fun subscribeObserver() {

        viewModelHajj.paymentStatus.observe(this@PreRegistrationBrowserActivity) {

            when(it)
            {
                is PaymentResource.Error ->
                {
                    showPaymentStatusDialog(0,)
                    progressBar.visibility = View.GONE
                }
                PaymentResource.Loading -> progressBar.visibility = View.VISIBLE
                is PaymentResource.hajj_pre_reg -> paymentSuccess()
                is PaymentResource.umrah_hajj_reg -> paymentSuccess()
            }

        }
    }
    private fun paymentSuccess()
    {
        showPaymentStatusDialog(1)

        progressBar.visibility = View.GONE
    }

    fun showPaymentStatusDialog(status: Int) {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )

        val view = layoutInflater.inflate(R.layout.dialog_hajj_refund, null,false)

        val dialogView: View = view
        customDialog.setView(dialogView)

        val imgChecked = view.findViewById<AppCompatImageView>(R.id.imgChecked)
        val tvTitleThank = view.findViewById<AppCompatTextView>(R.id.tvTitleThank)
        val tvDesRefund = view.findViewById<AppCompatTextView>(R.id.tvDesRefund)
        val textViewNormal5 = view.findViewById<AppCompatTextView>(R.id.textViewNormal5)
        val rlBtn = view.findViewById<RelativeLayout>(R.id.rlBtn)



        if (status == 0) {
            imgChecked.setImageResource(R.drawable.ic_failed)
            tvTitleThank.setText("দুঃখিত মুহতারাম!")
            tvDesRefund.setText("আপনার পেমেন্ট সফল হয়নি। অনুগ্রহ করে আবার চেষ্টা করুন অথবা আমাদের সাথে যোগাযোগ করুন।")
            textViewNormal5.setText("আবার চেষ্টা করুন")
        } else {
            tvTitleThank.setText("ধন্যবাদ মুহতারাম!")
            tvDesRefund.setText("আপনার পেমেন্ট সম্পন্ন হয়েছে। খুব শীঘ্রই আমাদের প্রতিনিধি আপনার সাথে যোগাযোগ করবে, ইনশাআল্লাহ।")

            when(paymentTag)
            {
                PAYMENT_DONATION ->
                {
                    tvTitleThank.text = "ধন্যবাদ মুহতারাম!"
                    tvDesRefund.text = "আপনার পেমেন্টটি সফল হয়েছে। আপনি শীঘ্রই নিশ্চিতকরণ ই-মেইল পাবেন।"

                }
            }
        }


        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()


        rlBtn.handleClickEvent {
            alertDialog.dismiss()
            finish()
        }
    }


}