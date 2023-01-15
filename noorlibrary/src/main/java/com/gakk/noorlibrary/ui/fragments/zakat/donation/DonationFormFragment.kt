package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentDonationFormBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.zakat.SslStoreModel
import com.gakk.noorlibrary.util.*
/*import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener*/
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */


private const val ARG_LITERATURE_DETAILS = "literatureDetails"

class DonationFormFragment : Fragment() {

    private lateinit var binding: FragmentDonationFormBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null
    private var mDonatebleAmount: String = "1000"
    private var isFiexedDonationOption: Boolean = true
    private var address: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var name: String? = null
    private var mStoreID: String? = null
    private var mStorePassword: String? = null

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
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_donation_form,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.literature = mLiterature

        enableFixedAmount(500)
        binding.etNumber.setText(AppPreference.userNumber?.replace("88", ""))
        radioButtonClick()

        binding.etAmount.handleClickEvent {
            disableFixedAmount()
        }

        binding.appCompatCheckBox.handleClickEvent {
            checkPersonAddress()
        }

        binding.btnNext.handleClickEvent {
            validateAndCallPayment()
        }

        binding.tvTerm.handleClickEvent {
            mDetailsCallBack?.openUrl(DONATION_TERM)
        }
    }

    private fun enableFixedAmount(amount: Int) {
        mDonatebleAmount = amount.toString() + ""
        isFiexedDonationOption = true

        binding.etAmount.isCursorVisible = false
        binding.rbCustomAmount.isChecked = false

        when (amount) {
            500 -> {
                binding.radioFiveHundred.isChecked = true
                binding.radioThousand.isChecked = false
                binding.radioFiveThousand.isChecked = false
                binding.radioTenThousand.isChecked = false
            }
            1000 -> {
                binding.radioFiveHundred.isChecked = false
                binding.radioThousand.isChecked = true
                binding.radioFiveThousand.isChecked = false
                binding.radioTenThousand.isChecked = false
            }
            5000 -> {
                binding.radioFiveHundred.isChecked = false
                binding.radioThousand.isChecked = false
                binding.radioFiveThousand.isChecked = true
                binding.radioTenThousand.isChecked = false
            }
            else -> {
                binding.radioFiveHundred.isChecked = false
                binding.radioThousand.isChecked = false
                binding.radioFiveThousand.isChecked = false
                binding.radioTenThousand.isChecked = true
            }
        }
    }

    private fun disableFixedAmount() {

        isFiexedDonationOption = false
        binding.etAmount.isCursorVisible = true
        binding.rbCustomAmount.isChecked = true

        binding.radioFiveHundred.isChecked = false
        binding.radioThousand.isChecked = false
        binding.radioFiveThousand.isChecked = false
        binding.radioTenThousand.isChecked = false
    }

    private fun radioButtonClick() {
        binding.radioFiveHundred.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(500)
            }
        }

        binding.radioThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(1000)
            }
        }

        binding.radioFiveThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(5000)
            }
        }

        binding.radioTenThousand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableFixedAmount(10000)
            }
        }

        binding.rbCustomAmount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                disableFixedAmount()
            }
        }
    }

    private fun validateAndCallPayment() {
        if (checkPersonAddress()) {
            //payZakat()
        }
    }

    /*private fun payZakat() {
        val customerName = "" + name
        val customerPhone = "" + phoneNumber
        val customerEmail = "" + email

        var donationAmount: String? = null
        donationAmount = "" + mDonatebleAmount

        if (!isFiexedDonationOption) {
            donationAmount = binding.etAmount.text.toString()
            mDonatebleAmount = donationAmount

            if (donationAmount.isNullOrEmpty()) {
                binding.etAmount.error = "এমাউন্ট প্রদান করুন"
                return
            }
        }


        if (!binding.appCompatCheckBox.isChecked) {
            Toast.makeText(requireContext(), "শর্তাবলীতে সম্মতি প্রদান করুন ", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val environment = SSLCSdkType.LIVE

        val uniqueID = UUID.randomUUID().toString()
        val storeObject = generateOrganisationData()
        mStoreID = storeObject.storeId
        mStorePassword = storeObject.storePassword

        val sslCommerzInitialization: SSLCommerzInitialization = SSLCommerzInitialization(
            mStoreID,
            mStorePassword,
            donationAmount.toDouble(),
            SSLCCurrencyType.BDT,
            uniqueID,
            PRODUCT_CATEGORY,
            environment
        )

        val customerInfoInitializer: SSLCCustomerInfoInitializer = SSLCCustomerInfoInitializer(
            customerName,
            customerEmail,
            address,
            "",
            "",
            SSL_COUNTRY_NAME,
            customerPhone
        )

        IntegrateSSLCommerz.getInstance(requireContext())
            .addSSLCommerzInitialization(sslCommerzInitialization)
            .addCustomerInfoInitializer(customerInfoInitializer)
            .buildApiCall(object : SSLCTransactionResponseListener {
                override fun transactionSuccess(p0: SSLCTransactionInfoModel?) {
                    if (p0?.riskLevel.equals("0")) {
                        Log.e("DonationFormFragment", ": Payment Successful")
                        mDetailsCallBack?.showToastMessage("আপনার পেমেন্টটি সফল হয়েছে। আপনি শীঘ্রই নিশ্চিতকরণ ই-মেইল পাবেন।")
                    }
                }

                override fun transactionFail(p0: String?) {
                    mDetailsCallBack?.showToastMessage("Transaction Failed")
                    Log.e("DonationFormFragment", "Failed" + p0)
                }

                override fun merchantValidationError(p0: String?) {
                    mDetailsCallBack?.showToastMessage("merchantValidationError")
                    Log.e("DonationFormFragment", "Failed" + p0)
                }

            })

    }*/

    private fun generateOrganisationData(): SslStoreModel {
        val itemTitle = mLiterature?.title

        when (itemTitle?.replace(" ", "")) {
            "আহসানিয়ামিশনক্যান্সারহাসপাতাল", "AhsaniaMissionCancer&GeneralHospital" -> {
                return SslStoreModel(
                    storeId = "ahsaniamissionlive",
                    storePassword = "5CCAD1ECCE08E81265"
                )
            }

            "বিদ্যানন্দ", "Bidyanondo" -> {
                return SslStoreModel(
                    storeId = "bidyanondolivelive",
                    storePassword = "5CCC0F0F740A247027"
                )
            }
            "চাইল্ডঅ্যান্ডওল্ডএইজকেয়ার", "Child&OldAgeCare" -> {
                return SslStoreModel(
                    storeId = "childoldcarelive",
                    storePassword = "5CCC0E8634E9518534"
                )
            }
            "রহমত-ই-আলমইসলামমিশনঅ্যান্ডইসলামমিশন", "RahmatEAlamIslamMissionandIslamMission" -> {
                return SslStoreModel(
                    storeId = "rahmatmissionlive",
                    storePassword = "5CCC10CDE5ED678288"
                )
            }
            "স্কলারস্পেশালস্কুল", "ScholarsSpecialSchool" -> {
                return SslStoreModel(
                    storeId = "scholarsspeciallive",
                    storePassword = "5CCC118C5284D61755"
                )
            }
            else -> {
                return SslStoreModel("", "")
            }

        }
    }

    private fun checkPersonAddress(): Boolean {
        address = binding.etAddress.text.toString()

        if (!checkPersonEmail()) {
            return false
        }

        if (address.isNullOrEmpty()) {
            binding.etAddress.error = getString(R.string.txt_error_address)
            return false
        }

        return true
    }

    private fun checkPersonEmail(): Boolean {
        email = binding.etEmail.text.toString()

        if (!checkPersonPhone()) {
            return false
        }

        if (email.isNullOrEmpty()) {
            binding.etEmail.error = getString(R.string.txt_error_email)
            return false
        }

        if (!emailValidation(email)) {
            binding.etEmail.error = getString(R.string.txt_error_correct_email)
            return false
        }

        return true
    }

    private fun checkPersonPhone(): Boolean {
        phoneNumber = binding.etNumber.text.toString()

        if (!chackPersonName()) {
            return false
        }

        if (phoneNumber.isNullOrEmpty()) {
            binding.etNumber.error = getString(R.string.txt_error_number)
            binding.etNumber.setSelection(0)
            return false
        }

        if (phoneNumber!!.length < 11) {
            binding.etNumber.error = getString(R.string.txt_error_correct_number)
            binding.etNumber.setSelection(0)
            return false
        }
        return true
    }

    private fun chackPersonName(): Boolean {
        name = binding.etYourName.text.toString()

        if (name.isNullOrEmpty()) {
            binding.etYourName.error = getString(R.string.txt_error_name)
            binding.etYourName.setSelection(0)
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