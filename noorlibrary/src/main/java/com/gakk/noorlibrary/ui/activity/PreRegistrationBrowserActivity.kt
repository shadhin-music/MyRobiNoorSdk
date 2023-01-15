package com.gakk.noorlibrary.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.ActivitySubscriptionBrowserBinding
import com.gakk.noorlibrary.databinding.DialogHajjRefundBinding
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch


class PreRegistrationBrowserActivity : AppCompatActivity() {

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

        intent.let {
            trackongNo = intent.getStringExtra(TRACKING_NO_TAG)
            url = intent.getStringExtra(PAYMENT_URL_TAG)
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
                    trackongNo?.let { viewModelHajj.updatePaymentStatus(it) }
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
            when (it.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    showPaymentStatusDialog(1)

                    binding.progressBar.visibility = View.GONE
                }

                Status.ERROR -> {
                    showPaymentStatusDialog(0)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    fun showPaymentStatusDialog(status: Int) {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )
        val bindingDialog: DialogHajjRefundBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_hajj_refund,
            null,
            false
        )

        val dialogView: View = bindingDialog.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        if (status == 0) {
            bindingDialog.imgChecked.setImageResource(R.drawable.ic_failed)
            bindingDialog.tvTitleThank.setText("দুঃখিত মুহতারাম!")
            bindingDialog.tvDesRefund.setText("আপনার পেমেন্ট সফল হয়নি। অনুগ্রহ করে আবার চেষ্টা করুন অথবা আমাদের সাথে যোগাযোগ করুন।")
            bindingDialog.textViewNormal5.setText("আবার চেষ্টা করুন")
        } else {
            bindingDialog.tvTitleThank.setText("ধন্যবাদ মুহতারাম!")
            bindingDialog.tvDesRefund.setText("আপনার পেমেন্ট সম্পন্ন হয়েছে। খুব শীঘ্রই আমাদের প্রতিনিধি আপনার সাথে যোগাযোগ করবে, ইনশাআল্লাহ।")
        }

        bindingDialog.rlBtn.handleClickEvent {
            alertDialog.dismiss()
            finish()
        }
    }

}