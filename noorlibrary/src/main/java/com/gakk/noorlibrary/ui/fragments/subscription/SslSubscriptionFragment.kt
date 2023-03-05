package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.ui.activity.SubscriptionBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */


private const val ARG_PAGE_TAG = "subscriotion_page"

internal class SslSubscriptionFragment : Fragment(), BillingClientWrapper.OnPurchaseListener {

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
    private lateinit var layoutMonthlyNagad:ConstraintLayout
    private lateinit var layoutHalfYearly:ConstraintLayout
    private lateinit var layoutYearly:ConstraintLayout


    // get bundle extra

    private  var PAGE_TAG :String = ""

    // gPAY layout
    private lateinit var gpayLayout:ConstraintLayout
    private lateinit var btnSubscribeYearly_gpay:AppCompatButton
    private lateinit var btnSubscribeMonthly_gpay:AppCompatButton
    private lateinit var ivShapeSubMonthly_gpay:AppCompatImageView
    private lateinit var ivShapeSubWeekly_gpay:AppCompatImageView
    private lateinit var tvContent_gpay:AppCompatTextView
    private lateinit var tvContentFifteen_gpay:AppCompatTextView
    private lateinit var tvAmount_gpay:AppCompatTextView
    private lateinit var tvAmountMonthly_gpay:AppCompatTextView
    private lateinit var tvDailyService_gpay:AppCompatTextView
    private lateinit var tvFifteenService_gpay:AppCompatTextView
    lateinit var billingClientWrapper: BillingClientWrapper



    companion object {
        @JvmStatic
        fun newInstance(page_tag:String="") =
            SslSubscriptionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_TAG,page_tag)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClientWrapper = BillingClientWrapper(requireContext())

        mCallback = requireActivity() as DetailsCallBack

        arguments.let {
            if (it != null) {
                PAGE_TAG = it.getString(ARG_PAGE_TAG).toString()
            }
        }
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

        // main pack layout
        layoutMonthlyNagad = view.findViewById(R.id.layoutMonthlyNagad)
        layoutHalfYearly = view.findViewById(R.id.layoutHalfYearly)
        layoutYearly = view.findViewById(R.id.layoutYearly)

        //gpay layout
        gpayLayout = view.findViewById(R.id.gpayLayout)
        btnSubscribeYearly_gpay = gpayLayout.findViewById(R.id.btnSubscribeYearly)
        btnSubscribeMonthly_gpay = gpayLayout.findViewById(R.id.btnSubscribeMonthly)
        ivShapeSubMonthly_gpay = gpayLayout.findViewById(R.id.ivShapeSubMonthly)
        ivShapeSubWeekly_gpay = gpayLayout.findViewById(R.id.ivShapeSubWeekly)
        tvContent_gpay = gpayLayout.findViewById(R.id.tvContent)
        tvContentFifteen_gpay = gpayLayout.findViewById(R.id.tvContentFifteen)
        tvAmount_gpay = gpayLayout.findViewById(R.id.tvAmount)
        tvAmountMonthly_gpay = gpayLayout.findViewById(R.id.tvAmountMonthly)
        tvDailyService_gpay = gpayLayout.findViewById(R.id.tvDailyService)
        tvFifteenService_gpay = gpayLayout.findViewById(R.id.tvFifteenService)


    }

    private fun hideMainPack()
    {
        layoutMonthlyNagad.hide()
        layoutHalfYearly.hide()
        layoutYearly.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.page_title_subscription))


        // check page tag

        when(PAGE_TAG)
        {
            PAGE_SUBSCRIPTION_GPAY ->
            {

               initGpay()

            }
            else ->
            {
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
        }
    }

    private fun initGpay()
    {
        billingClientWrapper.onPurchaseListener = this@SslSubscriptionFragment
        queryPurchaseHistory()
        hideMainPack()

        setImageFromUrlNoProgress(
            ivShapeSubWeekly_gpay,
            ImageFromOnline("Drawable/ic_shape_sub.webp").fullImageUrl
        )

        setImageFromUrlNoProgress(
            ivShapeSubMonthly_gpay,
            ImageFromOnline("Drawable/ic_shape_sub.webp").fullImageUrl
        )

        gpayLayout.show()

        tvDailyService_gpay.text = getString(R.string.txt_yearly_service)
        tvAmount_gpay.text = getString(R.string.txt_amount_yearly_g_pay)

        tvFifteenService_gpay.text = getString(R.string.txt_monthly_service)
        tvAmountMonthly_gpay.text = getString(R.string.txt_amount_monthly_g_pay)
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
                setImageFromUrlNoProgress(
                    ivShapeSubMonthly,
                    ImageFromOnline("Drawable/ic_shape_sub.webp").fullImageUrl
                )
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
                setImageFromUrlNoProgress(
                    ivShapeHalfYearly,
                    ImageFromOnline("Drawable/ic_shape_sub.webp").fullImageUrl
                )
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
                setImageFromUrlNoProgress(
                    ivShapeSubYearly,
                    ImageFromOnline("Drawable/ic_shape_sub.webp").fullImageUrl
                )
                btnSubscribeYearly.setText(getString(R.string.txt_sub))
                btnSubscribeYearly.setBackgroundResource(R.drawable.ic_button_small)
            }
        }
    }


    fun queryPurchaseHistory() {
        billingClientWrapper.queryActivePurchasesForType(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchasesList ->
            val responseCode = billingResult.responseCode
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.size == 0) {
                    displayProducts()
                    AppPreference.subYearlyGpay = false
                    AppPreference.subMonthlyGpay = false

                } else {
                    for (purchase: Purchase in purchasesList) {

                        val jsonObject =
                            JSONTokener(purchase.originalJson).nextValue() as JSONObject

                        val id = jsonObject.getString("productId")
                        if (id == "noor_yearly_pack") {

                            ivShapeSubWeekly_gpay.setImageResource(R.drawable.ic_shape_sub_disable)
                            btnSubscribeYearly_gpay.text = getString(R.string.txt_unsub)
                            btnSubscribeYearly_gpay.setTextColor(Color.WHITE)
                            tvAmount_gpay.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.txt_color_title
                                )
                            )
                            tvContent_gpay.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.txt_color_title
                                )
                            )
                            btnSubscribeMonthly_gpay.setOnClickListener {
                                if (AppPreference.subMonthlyRobi ||
                                    AppPreference.subWeeklyRobi ||
                                    AppPreference.subDaily ||
                                    AppPreference.subFifteenDays ||
                                    AppPreference.subMonthlySsl ||
                                    AppPreference.subYearlySsl ||
                                    AppPreference.subHalfYearlySsl ||
                                    AppPreference.subYearlyGpay ||
                                    AppPreference.subMonthlyGpay) {
                                    mCallback?.showToastMessage("You are Already subscribed")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You are already subscribed to a plan please unsubscribe it first from google play",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            btnSubscribeYearly_gpay.setOnClickListener {
                                if (
                                    AppPreference.subMonthlyRobi ||
                                    AppPreference.subWeeklyRobi ||
                                    AppPreference.subDaily ||
                                    AppPreference.subFifteenDays ||
                                    AppPreference.subMonthlySsl ||
                                    AppPreference.subYearlySsl ||
                                    AppPreference.subHalfYearlySsl ||
                                    AppPreference.subYearlyGpay ||
                                    AppPreference.subMonthlyGpay

                                ) {
                                    mCallback?.showToastMessage("You are Already subscribed")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "To unsubscribe please go to google play store (manage subscription)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            btnSubscribeYearly_gpay.setBackgroundResource(R.drawable.ic_button_unsub)
                        }
                        if (id == "noor_monthly_pack") {
//                                showSubDialog("noor yearly pack","To unsubcribe please go to google play")
//
                            ivShapeSubMonthly_gpay.setImageResource(R.drawable.ic_shape_sub_disable)
                            btnSubscribeMonthly_gpay.text = getString(R.string.txt_unsub)
                            btnSubscribeMonthly_gpay.setTextColor(Color.WHITE)
                            tvAmountMonthly_gpay.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.txt_color_title
                                )
                            )
                            tvContentFifteen_gpay.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.txt_color_title
                                )
                            )
                            btnSubscribeYearly_gpay.setOnClickListener {
                                if (
                                    AppPreference.subMonthlyRobi ||
                                    AppPreference.subWeeklyRobi ||
                                    AppPreference.subDaily ||
                                    AppPreference.subFifteenDays ||
                                    AppPreference.subMonthlySsl ||
                                    AppPreference.subYearlySsl ||
                                    AppPreference.subHalfYearlySsl ||
                                    AppPreference.subYearlyGpay ||
                                    AppPreference.subMonthlyGpay
                                ) {
                                    mCallback?.showToastMessage("You are Already subscribed")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You are already subscribed to a plan please unsubscribe it first from google play",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            btnSubscribeMonthly_gpay.setOnClickListener {
                                if (
                                    AppPreference.subMonthlyRobi ||
                                    AppPreference.subWeeklyRobi ||
                                    AppPreference.subDaily ||
                                    AppPreference.subFifteenDays ||
                                    AppPreference.subMonthlySsl ||
                                    AppPreference.subYearlySsl ||
                                    AppPreference.subHalfYearlySsl ||
                                    AppPreference.subYearlyGpay ||
                                    AppPreference.subMonthlyGpay
                                ) {
                                    mCallback?.showToastMessage("You are Already subscribed")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "To unsubscribe please go to google play store (manage subscription)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            btnSubscribeMonthly_gpay.setBackgroundResource(R.drawable.ic_button_unsub)
                        }
                        Log.d("Tag", "clicked: " + purchase.originalJson + "\n")
                    }
                }
            } else {
                Log.d("Tag", "ERROR: " + responseCode)
                //showResponseCode(responseCode)
            }
        }
    }

    private val purchaseButtonsMap: Map<String, Button> by lazy(LazyThreadSafetyMode.NONE) {
        mapOf(
            "noor_yearly_pack" to btnSubscribeYearly_gpay,
            "noor_monthly_pack" to btnSubscribeMonthly_gpay,
        )
    }

    private fun displayProducts() {

        billingClientWrapper.queryProducts(object : BillingClientWrapper.OnQueryProductsListener {

            override fun onSuccess(products: List<SkuDetails>) {

                Log.e("GPAY",products.toString())
                products.forEach { product ->
                    Log.d("TAG", "Product map: " + product.sku)
                    purchaseButtonsMap[product.sku]?.apply {
                        setOnClickListener {
                            billingClientWrapper.purchase(requireActivity(), product)
                            Log.d("Tag", "clicked4321: " + product.title)

                        }
                    }

                }
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                Log.d("TAG", "Product map: " + error)
            }
        })

    }

    override fun onPurchaseSuccess(purchase: Purchase?) {
        val jsonObject = JSONTokener(purchase?.originalJson).nextValue() as JSONObject
        val id = jsonObject.getString("productId")
        Log.d("Tag", "clicked123: " + id)
        AppPreference.subYearlyGpay = id == "noor_yearly_pack"

        AppPreference.subMonthlyGpay = id == "noor_monthly_pack"

        queryPurchaseHistory()
    }

    override fun onPurchaseFailure(error: BillingClientWrapper.Error) {
        Toast.makeText(requireContext(), "Your purchase failed!", Toast.LENGTH_LONG).show()
    }
}