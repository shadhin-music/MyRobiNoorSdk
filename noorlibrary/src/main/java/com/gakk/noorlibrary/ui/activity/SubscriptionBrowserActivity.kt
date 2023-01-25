package com.gakk.noorlibrary.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference

import com.gakk.noorlibrary.util.*


internal class SubscriptionBrowserActivity : AppCompatActivity() {

  //  private lateinit var binding: ActivitySubscriptionBrowserBinding
    private lateinit var webview:WebView
    private lateinit var progressBar:View
    private lateinit var toolBar:TextView
    private lateinit var btnBack:View
    private var isRobi: Boolean? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_subscription_browser)
        setupUi()

        btnBack.handleClickEvent { finish() }

        val url: String?
        val subscriptionId: String?
        val isPaymentNagad: Boolean

        intent.let {
            subscriptionId = intent.getStringExtra(SUBSCRIPTION_ID_TAG)
            isPaymentNagad = intent.getBooleanExtra(PAYMENT_METHOD_TAG, false)
        }

        toolBar.setText(R.string.page_title_subscription)

        if (isPaymentNagad) {
            url = intent.getStringExtra(PAYMENT_URL_TAG)
        } else {
            url =
                SUB_BASE_URL_ROBI + AppPreference.userNumber + "&SDPproductID=" + subscriptionId + SUB_END_PART_ROBI
        }


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
                if (url.contains("isDoubleConfrim")) {
                    val chargeStatus: String? = getQueryMap(url)?.get("isDoubleConfrim")
                    val confrimResult: String? = getQueryMap(url)?.get("cnfmResult")
                    val resultCode: String? = getQueryMap(url)?.get("resultCode")
                    if (chargeStatus != null) {
                        if (chargeStatus == "1" && confrimResult == "1" && resultCode == "0") {
                            Toast.makeText(
                                this@SubscriptionBrowserActivity,
                                "Successfully Subscribed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            if (isRobi == true) {
                            }

                            Toast.makeText(
                                this@SubscriptionBrowserActivity,
                                "Subscription Failed! Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }

            }
        })


    }

    private fun setupUi() {
        webview = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progressBar)
        toolBar = findViewById(R.id.toolBar)
        btnBack = findViewById(R.id.btnBack)
    }

    fun getQueryMap(query: String): Map<String, String>? {
        val map: MutableMap<String, String> = HashMap()
        val queryString = query.split("\\?".toRegex()).toTypedArray()
        if (queryString.size > 1) {
            val params = queryString[1].split("&".toRegex()).toTypedArray()
            for (param in params) {
                val name = param.split("=".toRegex()).toTypedArray()[0]
                val value = param.split("=".toRegex()).toTypedArray()[1]
                map[name] = value
            }
        }
        return map
    }
}