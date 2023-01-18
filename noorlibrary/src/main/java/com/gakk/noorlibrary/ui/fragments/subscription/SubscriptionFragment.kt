package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogSubscriptionBinding
import com.gakk.noorlibrary.databinding.FragmentSubscriptionBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.ui.activity.SubscriptionBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

internal class SubscriptionFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionBinding
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_subscription, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCelcom = false
        initViewRobi()

        binding.item = ImageFromOnline("ic_bg_sub.png")

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


            if (isCelcom == true) {
                model.checkSubscription(AppPreference.userNumber!!, SUBSCRIPTION_ID_WEEKLY)
                model.checkSubscriptionMonthly(AppPreference.userNumber!!, SUBSCRIPTION_ID_MONTHLY)
            } else {
                model.checkSubscriptionRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_DAILY)
                model.checkSubscriptionFifteenDays(
                    AppPreference.userNumber!!,
                    SUBSCRIPTION_ID_FIFTEENDAYS
                )
            }


            model.weeklySubInfo.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = View.GONE
                        Log.e("Sub", "Weekly" + it.data)
                        when (it.data?.regStatus) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                                binding.ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub_disable)
                                binding.btnSubscribeWeekly.setText(getString(R.string.txt_unsub))
                                binding.btnSubscribeWeekly.setTextColor(Color.WHITE)
                                binding.tvAmount.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.tvContent.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subWeekly = false
                                binding.ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub)
                                binding.btnSubscribeWeekly.setText(getString(R.string.txt_sub))
                                binding.btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_small)
                            }

                        }

                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                        Log.e("Sub", "Error" + it.message)
                    }
                }
            }

            model.weeklySubInfoRobi.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE
                        Log.e("Sub", "loading")
                    }
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = View.GONE
                        Log.e("Sub", "Weekly" + it.data)
                        when (it.data) {
                            "1AK" -> {
                                AppPreference.subWeekly = true
                                binding.ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub_disable)
                                binding.btnSubscribeWeekly.setText(getString(R.string.txt_unsub))
                                binding.btnSubscribeWeekly.setTextColor(Color.WHITE)
                                binding.tvAmount.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.tvContent.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subWeekly = false
                                binding.ivShapeSubWeekly.setImageResource(R.drawable.ic_shape_sub)
                                binding.btnSubscribeWeekly.setText(getString(R.string.txt_sub))
                                binding.btnSubscribeWeekly.setBackgroundResource(R.drawable.ic_button_small)
                            }

                        }

                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
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
                                binding.ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                                binding.btnSubscribeMonthly.setText(getString(R.string.txt_unsub))
                                binding.btnSubscribeMonthly.setTextColor(Color.WHITE)
                                binding.tvAmountMonthly.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.tvContent.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subMonthly = false
                                binding.ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                                binding.btnSubscribeMonthly.setText(getString(R.string.txt_sub))

                                binding.btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
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
                                binding.ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub_disable)
                                binding.btnSubscribeMonthly.setText(getString(R.string.txt_unsub))
                                binding.btnSubscribeMonthly.setTextColor(Color.WHITE)
                                binding.tvAmountMonthly.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.tvContent.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.txt_color_title
                                    )
                                )
                                binding.btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subMonthly = false
                                binding.ivShapeSubMonthly.setImageResource(R.drawable.ic_shape_sub)
                                binding.btnSubscribeMonthly.setText(getString(R.string.txt_sub))

                                binding.btnSubscribeMonthly.setBackgroundResource(R.drawable.ic_button_small)
                            }
                        }

                    }
                    Status.ERROR -> {
                        Log.e("Sub", "Error" + it.message)
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
                                if (isCelcom == true) {
                                    model.checkSubscription(
                                        AppPreference.userNumber!!,
                                        SUBSCRIPTION_ID_WEEKLY
                                    )
                                    model.checkSubscriptionMonthly(
                                        AppPreference.userNumber!!,
                                        SUBSCRIPTION_ID_MONTHLY
                                    )
                                } else {

                                    model.checkSubscriptionRobi(
                                        AppPreference.userNumber!!,
                                        SUBSCRIPTION_ID_DAILY
                                    )
                                    model.checkSubscriptionFifteenDays(
                                        AppPreference.userNumber!!,
                                        SUBSCRIPTION_ID_FIFTEENDAYS
                                    )
                                }

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

            binding.btnSubscribeWeekly.handleClickEvent {

                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
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
                                PLAN_NAME_WEEKLY,
                                getString(R.string.txt_unsub),
                                unSubDialogTxt
                            )
                        }

                        else -> {
                            showSubDialog(
                                PLAN_NAME_WEEKLY,
                                planTitle,
                                planDes
                            )
                        }
                    }
                }
            }
            binding.btnSubscribeMonthly.handleClickEvent {

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

    private fun initViewCelcom() {
        binding.tvTitleSub.setText(getString(R.string.sub_title))
        binding.tvDesSub.setText(getString(R.string.sub_des))

        binding.tvDailyService.setText(getString(R.string.txt_weekly_service))
        binding.tvAmount.setText(getString(R.string.txt_amount))

        binding.tvFifteenService.setText(getString(R.string.txt_monthly_service))
        binding.tvAmountMonthly.setText(getString(R.string.txt_amount_fifteen))
    }

    private fun initViewRobi() {
        binding.tvTitleSub.setText(getString(R.string.sub_title_robi))
        binding.tvDesSub.setText(getString(R.string.sub_des_robi))

        binding.tvDailyService.setText(getString(R.string.txt_daily_service))
        binding.tvAmount.setText(getString(R.string.txt_amount_robi))

        binding.tvFifteenService.setText(getString(R.string.txt_fifteen_day_service))
        binding.tvAmountMonthly.setText(getString(R.string.txt_amount_fifteen_robi))
    }

    override fun onResume() {
        super.onResume()

        if (this::model.isInitialized) {
            if (isCelcom == true) {
                model.checkSubscription(AppPreference.userNumber!!, SUBSCRIPTION_ID_WEEKLY)
                model.checkSubscriptionMonthly(AppPreference.userNumber!!, SUBSCRIPTION_ID_MONTHLY)
            } else {
                model.checkSubscriptionRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_DAILY)
                model.checkSubscriptionFifteenDays(
                    AppPreference.userNumber!!,
                    SUBSCRIPTION_ID_FIFTEENDAYS
                )
            }
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
        val binding: DialogSubscriptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.dialog_subscription,
            null,
            false
        )


        val dialogView: View = binding.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (planName.equals(PLAN_NAME_WEEKLY)) {
            when (AppPreference.subWeekly) {
                true -> {
                    binding.btnSub.setText(getString(R.string.txt_unsub))
                    binding.icon.setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    binding.btnSub.setText(getString(R.string.txt_sub))
                    binding.icon.setImageResource(R.drawable.ic_premium)
                }
            }
        } else {
            when (AppPreference.subMonthly) {
                true -> {
                    binding.btnSub.setText(getString(R.string.txt_unsub))
                    binding.icon.setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    binding.btnSub.setText(getString(R.string.txt_sub))
                    binding.icon.setImageResource(R.drawable.ic_premium)
                }
            }
        }


        binding.tvTitle.setText(title)
        binding.tvDes.setText(descrption)



        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        binding.imgClose.handleClickEvent {
            alertDialog.dismiss()
        }

        binding.btnSub.handleClickEvent {

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
