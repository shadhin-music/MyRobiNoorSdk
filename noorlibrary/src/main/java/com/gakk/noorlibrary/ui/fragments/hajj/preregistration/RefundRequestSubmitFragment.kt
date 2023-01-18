package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogHajjRefundBinding
import com.gakk.noorlibrary.databinding.FragmentRefundRequestSubmitBinding
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.isBangladeshiPhoneNumber
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch

private const val ARG_REFUND_FEE = "refundFee"
private const val ARG_TOTAL_AMOUNT = "totalAmount"

internal class RefundRequestSubmitFragment : Fragment() {

    private lateinit var binding: FragmentRefundRequestSubmitBinding
    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var hajjViewModel: HajjViewModel
    private var refundFee: String? = null
    private var amount: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            refundFee = it.getString(ARG_REFUND_FEE)
            amount = it.getString(ARG_TOTAL_AMOUNT)
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(
            refundFee: String? = null,
            totalAmount: String? = null
        ) =
            RefundRequestSubmitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REFUND_FEE, refundFee)
                    putString(ARG_TOTAL_AMOUNT, totalAmount)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_refund_request_submit,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.handleClickEvent {

            val trackingNo = binding.etTrackingNumber.text.toString()
            val mobileNumber = binding.etMobileNumber.text.toString()
            val userNumber = "880$mobileNumber"

            if (trackingNo.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_tracking_no))
            } else if (mobileNumber.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
            } else if (mobileNumber.length < 10 || !isBangladeshiPhoneNumber(userNumber)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
            } else {
                hajjViewModel.requestHajjRefund(trackingNo, userNumber)
            }
        }

        binding.appCompatTextView22.setText(refundFee)
        binding.tvAmountRefund.setText(amount)

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            hajjViewModel = ViewModelProvider(
                this@RefundRequestSubmitFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        hajjViewModel.refundRequest.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("refundRequest", "loading")
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Log.e("refundRequest", "SUCCESS")
                    binding.progressLayout.root.visibility = View.GONE
                    showHajjPackageRefundDialog()
                }

                Status.ERROR -> {
                    Log.e("refundRequest", "ERROR")
                    binding.progressLayout.root.visibility = View.GONE
                    mCallback?.showToastMessage(getString(R.string.api_error_msg))
                }
            }
        }
    }

    fun showHajjPackageRefundDialog() {
        val customDialog =
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MaterialAlertDialog_rounded
            )
        val bindingDialog: DialogHajjRefundBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
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


        bindingDialog.rlBtn.handleClickEvent {
            alertDialog.dismiss()
            requireActivity().finish()
        }
    }
}