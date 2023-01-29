package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.ui.activity.SubscriptionBrowserActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

internal class SubscriptionFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: SubscriptionViewModel
    private lateinit var subscriptionId: String
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var ivShapeSubDaily: AppCompatImageView
    private lateinit var btnSubscribeDaily: AppCompatButton
    private lateinit var tvAmount: AppCompatTextView
    private lateinit var tvContent: AppCompatTextView
    private lateinit var ivShapeSubFifteen: AppCompatImageView
    private lateinit var btnSubscribeFiftten: AppCompatButton
    private lateinit var tvAmountFifteen: AppCompatTextView
    private lateinit var ivBg: AppCompatImageView

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

        val view = inflater.inflate(
            R.layout.fragment_subscription,
            container, false
        )

        initUi(view)
        return view
    }

    private fun initUi(view: View) {
        progressLayout = view.findViewById(R.id.progressLayout)
        ivShapeSubDaily = view.findViewById(R.id.ivShapeSubDaily)
        btnSubscribeDaily = view.findViewById(R.id.btnSubscribeDaily)
        tvAmount = view.findViewById(R.id.tvAmount)
        tvContent = view.findViewById(R.id.tvContent)
        ivShapeSubFifteen = view.findViewById(R.id.ivShapeSubFifteen)
        btnSubscribeFiftten = view.findViewById(R.id.btnSubscribeFiftten)
        tvAmountFifteen = view.findViewById(R.id.tvAmountFifteen)
        ivBg = view.findViewById(R.id.ivBg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setImageFromUrlNoProgress(ivBg, ImageFromOnline("ic_bg_sub.png").fullImageUrl)

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
                                ivShapeSubDaily.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeDaily.setText(getString(R.string.txt_unsub))
                                btnSubscribeDaily.setTextColor(Color.WHITE)
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
                                btnSubscribeDaily.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subWeekly = false
                                ivShapeSubDaily.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeDaily.setText(getString(R.string.txt_sub))
                                btnSubscribeDaily.setBackgroundResource(R.drawable.ic_button_small)
                            }

                        }

                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
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
                                ivShapeSubFifteen.setImageResource(R.drawable.ic_shape_sub_disable)
                                btnSubscribeFiftten.setText(getString(R.string.txt_unsub))
                                btnSubscribeFiftten.setTextColor(Color.WHITE)
                                tvAmountFifteen.setTextColor(
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
                                btnSubscribeFiftten.setBackgroundResource(R.drawable.ic_button_unsub)
                            }
                            else -> {
                                AppPreference.subMonthly = false
                                ivShapeSubFifteen.setImageResource(R.drawable.ic_shape_sub)
                                btnSubscribeFiftten.setText(getString(R.string.txt_sub))

                                btnSubscribeFiftten.setBackgroundResource(R.drawable.ic_button_small)
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

                                model.checkSubscriptionRobi(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_DAILY
                                )
                                model.checkSubscriptionFifteenDays(
                                    AppPreference.userNumber!!,
                                    SUBSCRIPTION_ID_FIFTEENDAYS
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

                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    subscriptionId = PRODUCT_ID_DAILY
                    unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_daily)
                    planTitle = getString(R.string.txt_daily_service)
                    planDes = getString(R.string.txt_dialog_sub_des_daily)


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
            btnSubscribeFiftten.handleClickEvent {

                if (AppPreference.subMonthlyNagad || AppPreference.subHalfYearlyNagad || AppPreference.subYearlyNagad || AppPreference.subMonthlyGpay || AppPreference.subYearly) {
                    mCallback?.showToastMessage("You are Already subscribed")
                } else {
                    val unSubDialogTxt: String
                    val planTitle: String
                    val planDes: String

                    subscriptionId = PRODUCT_ID_FIFTEEN_DAYS
                    unSubDialogTxt = getString(R.string.txt_dialog_unsub_des_fifteen_robi)
                    planTitle = getString(R.string.txt_fifteen_day_service)
                    planDes = getString(R.string.txt_dialog_sub_des_fifteen_robi)


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

    override fun onResume() {
        super.onResume()

        if (this::model.isInitialized) {
            model.checkSubscriptionRobi(AppPreference.userNumber!!, SUBSCRIPTION_ID_DAILY)
            model.checkSubscriptionFifteenDays(
                AppPreference.userNumber!!,
                SUBSCRIPTION_ID_FIFTEENDAYS
            )
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

        val btnSub = binding.findViewById<AppCompatButton>(R.id.btnSub)
        val icon = binding.findViewById<AppCompatImageView>(R.id.icon)
        val tvTitle = binding.findViewById<AppCompatTextView>(R.id.tvTitle)
        val tvDes = binding.findViewById<AppCompatTextView>(R.id.tvDes)
        val imgClose = binding.findViewById<AppCompatImageView>(R.id.imgClose)


        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (planName.equals(PLAN_NAME_WEEKLY)) {
            when (AppPreference.subWeekly) {
                true -> {
                    btnSub.setText(getString(R.string.txt_unsub))
                    icon.setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    btnSub.setText(getString(R.string.txt_sub))
                    icon.setImageResource(R.drawable.ic_premium)
                }
            }
        } else {
            when (AppPreference.subMonthly) {
                true -> {
                    btnSub.setText(getString(R.string.txt_unsub))
                    icon.setImageResource(R.drawable.ic_close_rounded)
                }
                else -> {
                    btnSub.setText(getString(R.string.txt_sub))
                    icon.setImageResource(R.drawable.ic_premium)
                }
            }
        }


        tvTitle.setText(title)
        tvDes.setText(descrption)


        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        imgClose.handleClickEvent {
            alertDialog.dismiss()
        }

        btnSub.handleClickEvent {

            if (planName.equals(PLAN_NAME_WEEKLY)) {
                if (AppPreference.subMonthly) {

                    val unsubText: String
                    unsubText = getString(R.string.unsubscribe_fifteen_robi)

                    Toast.makeText(
                        context,
                        unsubText,
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    when (AppPreference.subWeekly) {
                        true -> {
                            model.cancelSubscriptionRobi(
                                AppPreference.userNumber!!,
                                SUBSCRIPTION_ID_DAILY
                            )

                            alertDialog.dismiss()
                        }
                        else -> {
                            alertDialog.dismiss()

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
            } else {
                if (AppPreference.subWeekly) {
                    val unsubText: String
                    unsubText = getString(R.string.unsubscribe_daily)

                    Toast.makeText(
                        context,
                        unsubText,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    when (AppPreference.subMonthly) {
                        true -> {
                            model.cancelSubscriptionRobi(
                                AppPreference.userNumber!!,
                                SUBSCRIPTION_ID_FIFTEENDAYS
                            )

                            alertDialog.dismiss()
                        }
                        else -> {
                            alertDialog.dismiss()

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
