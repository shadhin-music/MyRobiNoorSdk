package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.ui.activity.SubscriptionBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

internal class SubscriptionFragment : Fragment() {


    private lateinit var progressLayout:ProgressBar
    private lateinit var ivShapeSubWeekly:ImageView
    private lateinit var btnSubscribeWeekly:TextView
    private lateinit var tvAmount:TextView

    private lateinit var tvFifteenService:TextView
    private lateinit var tvAmountFifteen:TextView
    private lateinit var tvSevenService:TextView
    private lateinit var tvAmountWeekly:TextView
    private lateinit var tvMonthlyService:TextView
    private lateinit var tvAmountMonthly:TextView
    private lateinit var tvTitleSub:TextView
    private lateinit var tvDesSub:TextView
    private lateinit var tvDailyService:TextView
    private lateinit var tvContentMonthly:TextView


    private lateinit var tvContent:TextView
    private lateinit var tvContentSeven:TextView
    private lateinit var ivShapeSubMonthly:ImageView
    private lateinit var btnSubscribeMonthly:TextView
    private lateinit var btnSubscribeFifteen:TextView
    private lateinit var btnSubscribeDaily:View
    private lateinit var ivBg:ImageView

    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: SubscriptionViewModel
    private lateinit var subscriptionId: String
    private var isCelcom: Boolean? = null

    companion object {

        @JvmStatic
        fun newInstance() =
            SubscriptionFragment()
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
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        return inflater.inflate( R.layout.fragment_subscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCelcom = false
        setupUi(view)
        initViewRobi()

        setImageFromUrlNoProgress(ivBg,ImageFromOnline("ic_bg_sub.png").fullImageUrl)

        lifecycleScope.launch {

            mCallback?.setToolBarTitle(getString(R.string.page_title_subscription))

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@SubscriptionFragment,
                SubscriptionViewModel.FACTORY(repository)
            ).get(SubscriptionViewModel::class.java)

            model.checkSubscriptionRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_DAILY)
            model.checkSubscriptionFifteenDays(
                AppPreference.userNumber!!,
                SUBSCRIPTION_ID_FIFTEENDAYS
            )

           /* model.subscriptionCheckRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_WEEKLY_ROBI)
            model.subscriptionCheckRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_MONTHLY_ROBI)*/

            model.weeklySubInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        progressLayout.visibility = View.VISIBLE
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        progressLayout.visibility = View.GONE
                        Log.e("Sub", "Weekly" + it.data)
                        when (it.data?.regStatus) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                                ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeWeekly.setText(getString(R.string.txt_unsub))
                                btnSubscribeWeekly.setTextColor(Color.WHITE)
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
                                btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subWeekly = false
                                ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeWeekly.setText(getString(R.string.txt_sub))
                                btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_small)
                            }

                        }

                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                        Log.e("Sub", "Error" + it.message)
                    }
                }
            }

            model.weeklySubInfoRobi.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        progressLayout.visibility = View.VISIBLE
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        progressLayout.visibility = View.GONE
                        Log.e("Sub", "Weekly" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                                ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeWeekly.setText(getString(R.string.txt_unsub))
                                btnSubscribeWeekly.setTextColor(Color.WHITE)
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
                                btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subWeekly = false
                                ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeWeekly.setText(getString(R.string.txt_sub))
                                btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_small)
                            }

                        }

                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                        Log.e("Sub", "Error" + it.message)
                    }
                }
            }
            model.monthlySubInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.e("Sub", "Monthly" + it.data)
                        when (it.data?.regStatus) {
                            "1AK" -> {

                                AppPreference.subMonthly = true
                                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeMonthly.setText(getString(R.string.txt_unsub))
                                btnSubscribeMonthly.setTextColor(Color.WHITE)
                                tvAmountMonthly.setTextColor(
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
                                AppPreference.subMonthly = false
                                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeMonthly.setText(getString(R.string.txt_sub))

                                btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.e("Sub", "Error" + it.message)
                    }
                }
            }

            model.monthlySubInfoRobi.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        Log.e("Sub", "Monthly" + it.data)
                        when (it.data) {
                            "1AK" -> {

                                AppPreference.subMonthly = true
                                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeMonthly.setText(getString(R.string.txt_unsub))
                                btnSubscribeMonthly.setTextColor(Color.WHITE)
                                tvAmountMonthly.setTextColor(
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
                                AppPreference.subMonthly = false
                                ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeMonthly.setText(getString(R.string.txt_sub))

                                btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.e("Sub", "Error" + it.message)
                    }
                }
            }


            // robi new subscription package

            model.subscription_robi.observe(viewLifecycleOwner)
            {
                when (it) {
                    is SubsResource.Error -> Log.e("Sub", "Error" + it.msg)
                    SubsResource.Loading -> Log.e("Sub", "loading")
                    is SubsResource.SubscriptionRobi -> {

                        when (it.subscriptionId) {

                            SUBSCRIPTION_ID_WEEKLY_ROBI -> {

                                when (it.data.data) {

                                    "1AK" -> {

                                        AppPreference.subWeeklyRobi = true
                                        ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub_disable)
                                        btnSubscribeWeekly.text = getString(R.string.txt_unsub)
                                        btnSubscribeWeekly.setTextColor(Color.WHITE)
                                        tvAmountWeekly.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.txt_color_title
                                            )
                                        )
                                        tvContentSeven.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.txt_color_title
                                            )
                                        )
                                        btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_unsub)
                                    }
                                    else -> {
                                        AppPreference.subWeeklyRobi = false
                                        ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub)
                                        btnSubscribeWeekly.text = getString(R.string.txt_sub)
                                        btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_small)
                                    }
                                }
                            }

                            SUBSCRIPTION_ID_MONTHLY_ROBI -> {

                                when (it.data.data) {

                                    "1AK" -> {

                                        AppPreference.subMonthlyRobi = true
                                        ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                                        btnSubscribeMonthly.text = getString(R.string.txt_unsub)
                                        btnSubscribeMonthly.setTextColor(Color.WHITE)
                                        tvAmountMonthly.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.txt_color_title
                                            )
                                        )
                                        tvContentMonthly.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.txt_color_title
                                            )
                                        )
                                        btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_unsub)
                                    }
                                    else -> {
                                        AppPreference.subMonthlyRobi = false
                                        ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                                        btnSubscribeMonthly.text = getString(R.string.txt_sub)

                                        btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
                                    }
                                }


                            }


                        }
                    }
                }
            }

            model.canelSubInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {

                    }
                    Status.SUCCESS -> {

                        when (it.data) {
                            "Successfull", "SUCCESSFUL" -> {
                                Toast.makeText(
                                    context,
                                    getString(R.string.unsubscribe_success),
                                    Toast.LENGTH_LONG
                                ).show()

                                model.checkSubscriptionRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_DAILY
                                )
                                model.checkSubscriptionFifteenDays(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_FIFTEENDAYS
                                )

                                model.subscriptionCheckRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_WEEKLY_ROBI
                                )
                                model.subscriptionCheckRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_MONTHLY_ROBI
                                )

                            }

                            else -> {
                                Toast.makeText(
                                    context,
                                    getString(R.string.sub_fail),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }
                    Status.ERROR -> {

                    }
                }
            }

            model.networkInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("NetworkInfo", ": LOADING")
                    }
                    Status.SUCCESS -> {
                        it.data?.let {
                            checkMSISDN(it)
                        }
                    }

                    Status.ERROR -> {
                        Log.e("NetworkInfo", ": ERROR")
                    }
                }
            }

            btnSubscribeDaily.handleClickEvent {

                if (AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    if (isCelcom == true) {
                        subscriptionId = SUBSCRIPTION_ID_WEEKLY
                        unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_weekly)
                        planTitle = getString(R.string.txt_weekly_service)
                        planDes = getString(R.string.txt_dialog_sub_des_weekly)
                    } else {
                        subscriptionId = PRODUCT_ID_DAILY
                        unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_daily)
                        planTitle = getString(R.string.txt_daily_service)
                        planDes = getString(R.string.txt_dialog_sub_des_daily)
                    }

                    when (AppPreference.subWeekly) {
                        true -> {
                            showSubDialog(
                                PLAN_NAME_WEEKLY_ROBI,
                                getString(R.string.txt_unsub),
                                unSubDialogTxt
                            )
                        }

                        else -> {
                            showSubDialog(
                                PLAN_NAME_WEEKLY_ROBI,
                                planTitle,
                                planDes
                            )
                        }
                    }
                }
            }


            btnSubscribeWeekly.handleClickEvent {


                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    subscriptionId = PRODUCT_ID_SEVEN_DAYS_ROBI
                    unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_weekly)
                    planTitle = getString(R.string.txt_weekly_service)
                    planDes = getString(R.string.txt_dialog_sub_des_weekly_robi)

                    when (AppPreference.subWeeklyRobi) {
                        true -> {
                            showSubDialog(
                                PLAN_NAME_WEEKLY_ROBI,
                                getString(R.string.txt_unsub),
                                unSubDialogTxt
                            )
                        }

                        else -> {
                            showSubDialog(
                                PLAN_NAME_WEEKLY_ROBI,
                                planTitle,
                                planDes
                            )
                        }
                    }
                }
            }



            btnSubscribeMonthly.handleClickEvent {

                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    subscriptionId = PRODUCT_ID_THIRTY_DAYS_ROBI
                    unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_monthly)
                    planTitle = getString(R.string.txt_monthly_service)
                    planDes = getString(R.string.txt_dialog_sub_des_monthly_robi)

                    Log.e("monthly product code", subscriptionId)

                    when (AppPreference.subMonthlyRobi) {
                        true -> {
                            showSubDialog(
                                PLAN_NAME_MONTHLY_ROBI,
                                getString(R.string.txt_unsub),
                                unSubDialogTxt
                            )
                        }

                        else -> {
                            showSubDialog(
                                PLAN_NAME_MONTHLY_ROBI,
                                planTitle,
                                planDes
                            )
                        }
                    }
                }
            }


            btnSubscribeFifteen.handleClickEvent {

                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    if (isCelcom == true) {
                        subscriptionId = SUBSCRIPTION_ID_MONTHLY
                        unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_monthly)
                        planTitle = getString(R.string.txt_monthly_service)
                        planDes = getString(R.string.txt_dialog_sub_des_monthly)
                    } else {
                        subscriptionId = PRODUCT_ID_FIFTEEN_DAYS
                        unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_fifteen_robi)
                        planTitle = getString(R.string.txt_fifteen_day_service)
                        planDes = getString(R.string.txt_dialog_sub_des_fifteen_robi)
                    }

                    when (AppPreference.subMonthly) {
                        true -> {
                            showSubDialog(
                                PLAN_NAME_MONTHLY,
                                getString(R.string.txt_unsub),
                                unSubDialogTxt
                            )
                        }

                        else -> {
                            showSubDialog(
                                PLAN_NAME_MONTHLY,
                                planTitle,
                                planDes
                            )
                        }
                    }
                }

            }
        }
    }

    private fun setupUi(view: View) {


        /*ivShapeSubWeekly
        btnSubscribeWeekly
        tvAmount
        tvFifteenService
        tvAmountFifteen
        tvSevenService
        tvAmountWeekly
        tvMonthlyService
        tvAmountMonthly
        tvTitleSub
        tvDesSub
        tvDailyService
        tvContentMonthly

        tvContent
        tvContentSeven
        ivShapeSubMonthly
        btnSubscribeMonthly
        btnSubscribeFifteen

        */
        progressLayout = view.findViewById(R.id.progressLayout)
        ivShapeSubWeekly    = view.findViewById(R.id.ivShapeSubWeekly)
        btnSubscribeWeekly  = view.findViewById(R.id.btnSubscribeWeekly)
        tvAmount    = view.findViewById(R.id.tvAmount)
        tvFifteenService    = view.findViewById(R.id.tvFifteenService)
        tvAmountFifteen = view.findViewById(R.id.tvAmountFifteen)
        tvSevenService  = view.findViewById(R.id.tvSevenService)
        tvAmountWeekly  = view.findViewById(R.id.tvAmountWeekly)
        tvMonthlyService    = view.findViewById(R.id.tvMonthlyService)
        tvAmountMonthly = view.findViewById(R.id.tvAmountMonthly)
        tvTitleSub  = view.findViewById(R.id.tvTitleSub)
        tvDesSub    = view.findViewById(R.id.tvDesSub)
        tvDailyService  = view.findViewById(R.id.tvDailyService)
        tvContentMonthly    = view.findViewById(R.id.tvContentMonthly)


        tvContent  = view.findViewById(R.id.tvContent)
        tvContentSeven = view.findViewById(R.id.tvContentSeven)
        ivShapeSubMonthly  = view.findViewById(R.id.ivShapeSubMonthly)
        btnSubscribeMonthly = view.findViewById(R.id.btnSubscribeMonthly)
        btnSubscribeFifteen   = view.findViewById(R.id.btnSubscribeFifteen)
        btnSubscribeDaily   = view.findViewById(R.id.btnSubscribeDaily)
        ivBg   = view.findViewById(R.id.ivBg)


    }

    private fun checkMSISDN(output: String) {

        if (output.isEmpty() && output.equals(TAG_COULD_NOT_TRACK_DATA, false)) {
            Toast.makeText(
                context,
                "Use Robi or Airtel mobile data for subscription!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val operatorName = getOperatorTypeClass(output)

            operatorName?.let {
                if (operatorName.contains("Robi") or operatorName.contains("Airtel")) {
                    context?.startActivity(
                        Intent(
                            context,
                            SubscriptionBrowserActivity::class.java
                        ).putExtra(SUBSCRIPTION_ID_TAG, subscriptionId)
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Use Robi or Airtel mobile data for subscription!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }


    private fun initViewRobi() {
        tvTitleSub.setText(getString(R.string.sub_title_robi))
        tvDesSub.setText(getString(R.string.sub_des_robi))

        tvDailyService.setText(getString(R.string.txt_daily_service))
        tvAmount.setText(getString(R.string.txt_amount_robi))

        tvFifteenService.text = getString(R.string.txt_fifteen_day_service)
        tvAmountFifteen.text = getString(R.string.txt_amount_fifteen_robi)

        tvSevenService.text = getString(R.string.txt_seven_day_service)
        tvAmountWeekly.text = getString(R.string.txt_amount_seven_robi)

        tvMonthlyService.text = getString(R.string.txt_thirty_day_service)
        tvAmountMonthly.text = getString(R.string.txt_amount_thirty_robi)
    }

    override fun onResume() {
        super.onResume()

        if (this::model.isInitialized) {
            model.checkSubscriptionRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_DAILY)
            model.checkSubscriptionFifteenDays(
                AppPreference.userNumber!!,
                SUBSCRIPTION_ID_FIFTEENDAYS
            )
            model.subscriptionCheckRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_WEEKLY_ROBI)
            model.subscriptionCheckRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_MONTHLY_ROBI)

        } else {
            Log.e("chkSub", "not initialized")
        }

    }

    fun showSubDialog(planName: String, title: String, descrption: String) {
        val customDialog =
            MaterialAlertDialogBuilder(
                requireActivity(),
                R.style.MaterialAlertDialog_rounded
            )
        val binding: View = LayoutInflater.from(requireActivity()).inflate(
            R.layout.dialog_subscription,
            null,
            false
        )


        val dialogView: View = binding
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (planName.equals(PLAN_NAME_WEEKLY)) {
            when (AppPreference.subWeekly) {
                true -> {
                    binding.findViewById<TextView>(R.id.btnSub).setText(getString(R.string.txt_unsub))
                    binding.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    binding.findViewById<TextView>(R.id.btnSub).setText(getString(R.string.txt_sub))
                    binding.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_premium)
                }
            }
        } else {
            when (AppPreference.subMonthly) {
                true -> {
                    binding.findViewById<TextView>(R.id.btnSub).setText(getString(R.string.txt_unsub))
                    binding.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    binding.findViewById<TextView>(R.id.btnSub).setText(getString(R.string.txt_sub))
                    binding.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_premium)
                }
            }
        }


        binding.findViewById<TextView>(R.id.tvTitle).setText(title)
        binding.findViewById<TextView>(R.id.tvDes).setText(descrption)



        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        binding.findViewById<View>(R.id.imgClose).handleClickEvent {
            alertDialog.dismiss()
        }

        binding.findViewById<View>(R.id.btnSub).handleClickEvent {

            if (planName.equals(PLAN_NAME_WEEKLY)) {
                if (AppPreference.subMonthly) {

                    val unsubText: String
                    if (isCelcom == true) {
                        unsubText = getString(R.string.unsubscribe_monthly)
                    } else {
                        unsubText = getString(R.string.unsubscribe_fifteen_robi)
                    }

                    Toast.makeText(
                        context,
                        unsubText,
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    when (AppPreference.subWeekly) {
                        true -> {
                            if (isCelcom == true) {
                                model.cancelSubscription(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_WEEKLY
                                )
                            } else {
                                model.cancelSubscriptionRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_DAILY
                                )
                            }

                            alertDialog.dismiss()
                        }
                        else -> {
                            alertDialog.dismiss()

                            if (isCelcom == true) {
                                context?.startActivity(
                                    Intent(
                                        context,
                                        SubscriptionBrowserActivity::class.java
                                    ).putExtra(SUBSCRIPTION_ID_TAG, subscriptionId)
                                )
                            } else {
                                if (checkOtherNumber()) {
                                    model.checkNetworkStatus()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Use airtel or robi number to upgrade!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }
                    }
                }
            } else {
                if (AppPreference.subWeekly) {
                    val unsubText: String
                    if (isCelcom == true) {
                        unsubText = getString(R.string.unsubscribe_weekly)
                    } else {
                        unsubText = getString(R.string.unsubscribe_daily)
                    }
                    Toast.makeText(
                        context,
                        unsubText,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    when (AppPreference.subMonthly) {
                        true -> {
                            if (isCelcom == true) {
                                model.cancelSubscription(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_MONTHLY
                                )
                            } else {
                                model.cancelSubscriptionRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_FIFTEENDAYS
                                )
                            }

                            alertDialog.dismiss()
                        }
                        else -> {
                            alertDialog.dismiss()
                            if (isCelcom == true) {
                                context?.startActivity(
                                    Intent(
                                        context,
                                        SubscriptionBrowserActivity::class.java
                                    ).putExtra(SUBSCRIPTION_ID_TAG, subscriptionId)
                                )
                            } else {
                                if (checkOtherNumber()) {
                                    model.checkNetworkStatus()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Use airtel or robi number to upgrade!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }
                    }
                }
            }

        }
    }
}
