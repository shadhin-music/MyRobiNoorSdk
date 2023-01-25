package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.DONATION_SERVICE_ID
import com.gakk.noorlibrary.util.DONATION_TERM
import com.gakk.noorlibrary.util.SSL_CUSTOMER_EMAIL
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.SubscriptionViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */


private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class DonationFormFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null
    private var mDonatebleAmount: String = "1000"
    private var isFiexedDonationOption: Boolean = true
    private var address: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var name: String? = null
    private lateinit var viewModelSub: SubscriptionViewModel
    private lateinit var etNumber: AppCompatEditText
    private lateinit var etAmount: AppCompatEditText
    private lateinit var appCompatCheckBox: AppCompatCheckBox
    private lateinit var btnNext: AppCompatButton
    private lateinit var tvTerm: AppCompatTextView
    private lateinit var rbCustomAmount: AppCompatRadioButton
    private lateinit var radioFiveHundred: AppCompatRadioButton
    private lateinit var radioThousand: AppCompatRadioButton
    private lateinit var radioFiveThousand: AppCompatRadioButton
    private lateinit var radioTenThousand: AppCompatRadioButton
    private lateinit var etAddress: AppCompatEditText
    private lateinit var etEmail: AppCompatEditText
    private lateinit var etYourName: AppCompatEditText
    private lateinit var textViewNormal10: AppCompatTextView
    private lateinit var tvdetailsOrg: AppCompatTextView
    private lateinit var appCompatImageView10: AppCompatImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            mLiterature = it?.getSerializable(ARG_LITERATURE_DETAILS) as Literature?
        }
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance(
            itemLiterature: Literature
        ) =
            DonationFormFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_DETAILS, itemLiterature)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_donation_form,
            container, false
        )
        etNumber = view.findViewById(R.id.etNumber)
        etAmount = view.findViewById(R.id.etAmount)
        appCompatCheckBox = view.findViewById(R.id.appCompatCheckBox)
        btnNext = view.findViewById(R.id.btnNext)
        tvTerm = view.findViewById(R.id.tvTerm)
        rbCustomAmount = view.findViewById(R.id.rbCustomAmount)
        radioFiveHundred = view.findViewById(R.id.radioFiveHundred)
        radioThousand = view.findViewById(R.id.radioThousand)
        radioFiveThousand = view.findViewById(R.id.radioFiveThousand)
        radioTenThousand = view.findViewById(R.id.radioTenThousand)
        etAddress = view.findViewById(R.id.etAddress)
        etEmail = view.findViewById(R.id.etEmail)
        etYourName = view.findViewById(R.id.etYourName)
        textViewNormal10 = view.findViewById(R.id.textViewNormal10)
        tvdetailsOrg = view.findViewById(R.id.tvdetailsOrg)
        appCompatImageView10 = view.findViewById(R.id.appCompatImageView10)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val literature = mLiterature
        textViewNormal10.text = literature?.subcategoryName
        tvdetailsOrg.text = literature?.textInArabic

        Noor.appContext?.let {
            Glide.with(it)
                .load(literature?.fullImageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                })
                .error(R.drawable.place_holder_2_3_ratio)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(appCompatImageView10)
        }

        enableFixedAmount(500)
        etNumber.setText(AppPreference.userNumber?.replace("88", ""))
        radioButtonClick()

        etAmount.handleClickEvent {
            disableFixedAmount()
        }

        appCompatCheckBox.handleClickEvent {
            checkPersonAddress()
        }

        btnNext.handleClickEvent {
            validateAndCallPayment()
        }

        tvTerm.handleClickEvent {
            mDetailsCallBack?.openUrl(DONATION_TERM)
        }
    }

    private fun enableFixedAmount(amount: Int) {
        mDonatebleAmount = amount.toString() + ""
        isFiexedDonationOption = true

        etAmount.isCursorVisible = false
        rbCustomAmount.isChecked = false

        when (amount) {
            500 -> {
                radioFiveHundred.isChecked = true
                radioThousand.isChecked = false
                radioFiveThousand.isChecked = false
                radioTenThousand.isChecked = false
            }
            1000 -> {
                radioFiveHundred.isChecked = false
                radioThousand.isChecked = true
                radioFiveThousand.isChecked = false
                radioTenThousand.isChecked = false
            }
            5000 -> {
                radioFiveHundred.isChecked = false
                radioThousand.isChecked = false
                radioFiveThousand.isChecked = true
                radioTenThousand.isChecked = false
            }
            else -> {
                radioFiveHundred.isChecked = false
                radioThousand.isChecked = false
                radioFiveThousand.isChecked = false
                radioTenThousand.isChecked = true
            }
        }
    }

    private fun disableFixedAmount() {

        isFiexedDonationOption = false
        etAmount.isCursorVisible = true
        rbCustomAmount.isChecked = true

        radioFiveHundred.isChecked = false
        radioThousand.isChecked = false
        radioFiveThousand.isChecked = false
        radioTenThousand.isChecked = false
    }

    private fun radioButtonClick() {
        radioFiveHundred.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(500)
            }
        }

        radioThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(1000)
            }
        }

        radioFiveThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(5000)
            }
        }

        radioTenThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(10000)
            }
        }

        rbCustomAmount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                disableFixedAmount()
            }
        }
    }

    private fun validateAndCallPayment() {
        if (checkPersonAddress()) {
            payZakat()
        }
    }

    private fun payZakat() {
        val customerName = "" + name
        val customerPhone = "" + phoneNumber
        val customerEmail = "" + email

        var donationAmount: String? = null
        donationAmount = "" + mDonatebleAmount

        if (!isFiexedDonationOption) {
            donationAmount = etAmount.text.toString()
            mDonatebleAmount = donationAmount

            if (donationAmount.isNullOrEmpty()) {
                etAmount.error = "এমাউন্ট প্রদান করুন"
                return
            }
        }


        if (!appCompatCheckBox.isChecked) {
            Toast.makeText(requireContext(), "শর্তাবলীতে সম্মতি প্রদান করুন ", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // TODO("mobile number configure from sdk")

        viewModelSub.initiatePaymentSslRange(
            "01",
            DONATION_SERVICE_ID,
            customerName,
            SSL_CUSTOMER_EMAIL
        )

    }

    private fun checkPersonAddress(): Boolean {
        address = etAddress.text.toString()

        if (!checkPersonEmail()) {
            return false
        }

        if (address.isNullOrEmpty()) {
            etAddress.error = getString(R.string.txt_error_address)
            return false
        }

        return true
    }

    private fun checkPersonEmail(): Boolean {
        email = etEmail.text.toString()

        if (!checkPersonPhone()) {
            return false
        }

        if (email.isNullOrEmpty()) {
            etEmail.error = getString(R.string.txt_error_email)
            return false
        }

        if (!emailValidation(email)) {
            etEmail.error = getString(R.string.txt_error_correct_email)
            return false
        }

        return true
    }

    private fun checkPersonPhone(): Boolean {
        phoneNumber = etNumber.text.toString()

        if (!chackPersonName()) {
            return false
        }

        if (phoneNumber.isNullOrEmpty()) {
            etNumber.error = getString(R.string.txt_error_number)
            etNumber.setSelection(0)
            return false
        }

        if (phoneNumber!!.length < 11) {
            etNumber.error = getString(R.string.txt_error_correct_number)
            etNumber.setSelection(0)
            return false
        }
        return true
    }

    private fun chackPersonName(): Boolean {
        name = etYourName.text.toString()

        if (name.isNullOrEmpty()) {
            etYourName.error = getString(R.string.txt_error_name)
            etYourName.setSelection(0)
            return false
        }

        return true
    }

    fun emailValidation(emailAddress: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher

        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(emailAddress!!)
        return matcher.matches()
    }

}