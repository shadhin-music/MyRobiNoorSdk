package com.mcc.noor.ui.fragments.hajj.umrah_hajj


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.UmrahHajjViewModel
import com.google.gson.Gson
import com.mcc.noor.model.umrah_hajj.UmrahHajjPersonalPostModel
import com.gakk.noorlibrary.ui.fragments.payment.PaymentFragment
import kotlinx.coroutines.launch

private const val ARG_PACKAGE_ID = "umrahHajjPackID"
private const val ARG_PACKAGE_PRICE = "umrahHajjPackPrice"
class UmrahPersonalInfoFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var packageID:String
    private lateinit var packagePrice:String
    private lateinit var viewmodel : UmrahHajjViewModel
    private lateinit var repository: RestRepository
    private var gender = "Male"
    private var docType = "NID"
    private lateinit var docNumber: String
    private lateinit var name:String
    private lateinit var email:String
    private lateinit var passportNumber:String

    //view
    private lateinit var radio_group_nid:RadioGroup
    private lateinit var clNid:ConstraintLayout
    private lateinit var clBirthCertificate:ConstraintLayout
    private lateinit var btnNext:AppCompatButton
    private lateinit var etYourName:AppCompatEditText
    private lateinit var yourEmail:AppCompatEditText
    private lateinit var yourMob:AppCompatEditText
    private lateinit var yourPassport:AppCompatEditText
    private lateinit var radionId:RadioButton
    private lateinit var etNid:AppCompatEditText
    private lateinit var radioBirthCertificate:RadioButton
    private lateinit var etBirthCertificate:AppCompatEditText
    private lateinit var radioMale:RadioButton
    private lateinit var radioFemale:RadioButton
    private lateinit var progressLayout:ConstraintLayout


    companion object {

        @JvmStatic
        fun newInstance(packageID:String,price:String) =
            UmrahPersonalInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PACKAGE_ID,packageID)
                    putString(ARG_PACKAGE_PRICE,price)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailsCallBack = requireActivity() as DetailsCallBack

        arguments.let {
            if (it != null) {
                     packageID = it.getString(ARG_PACKAGE_ID).toString()
                     packagePrice = it.getString(ARG_PACKAGE_PRICE).toString()
            }
        }

       if(packageID.isEmpty())
       {
           mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
           mDetailsCallBack?.addFragmentToStackAndShow(UmrahHajjFragment.newInstance())

       }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch {

            repository = RepositoryProvider.getRepository()

            viewmodel = ViewModelProvider(
                this@UmrahPersonalInfoFragment,
                UmrahHajjViewModel.FACTORY(repository)
            ).get(UmrahHajjViewModel::class.java)

            viewmodel.let {
                initObserver()

            }

        }

        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        val view = inflater.inflate(
            R.layout.fragment_umrah_personal_info,
            container, false
        )

        initView(view)


        return view
    }

    private fun initView(view:View)
    {
        radio_group_nid = view.findViewById(R.id.radio_group_nid)
        clNid = view.findViewById(R.id.clNid)
        clBirthCertificate = view.findViewById(R.id.clBirthCertificate)
        btnNext = view.findViewById(R.id.btnNext)
        etYourName = view.findViewById(R.id.etYourName)
        yourEmail = view.findViewById(R.id.yourEmail)
        yourMob = view.findViewById(R.id.yourMob)
        yourPassport = view.findViewById(R.id.yourPassport)
        radionId = view.findViewById(R.id.radionId)
        etNid = view.findViewById(R.id.etNid)
        radioBirthCertificate = view.findViewById(R.id.radioBirthCertificate)
        etBirthCertificate = view.findViewById(R.id.etBirthCertificate)
        radioMale = view.findViewById(R.id.radioMale)
        radioFemale = view.findViewById(R.id.radioFemale)
        progressLayout = view.findViewById(R.id.progressLayout)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_group_nid.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radionId -> {

                    clNid.visibility = View.VISIBLE
                    clBirthCertificate.visibility = View.GONE
                }

                else -> {

                    clNid.visibility = View.GONE
                    clBirthCertificate.visibility = View.VISIBLE
                }
            }
        }

        btnNext.handleClickEvent {
            check_personal_info()
        }
    }


    private fun check_personal_info()
    {
         name = etYourName.text.toString()
         email = yourEmail.text.toString()
        val mobileNumber = yourMob.text.toString()
        val userNumber = "880$mobileNumber"
        passportNumber = yourPassport.text.toString()

        if (radionId.isChecked) {
            docType = "NID"
            docNumber = etNid.text.toString()
        } else if (radioBirthCertificate.isChecked) {
            docType = "BC"
            docNumber = etBirthCertificate.text.toString()
        }


        if (radioMale.isChecked) {
            gender = "Male"
        } else if (radioFemale.isChecked) {
            gender = "Female"
        }

        if (name.isEmpty()) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_name))
            return
        }

        if (mobileNumber.isEmpty()) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_mobile_number))
            return
        }

        if (mobileNumber.length < 10) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
            return
        }

        if (!isBangladeshiPhoneNumber(userNumber)) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
        }

        if (email.isEmpty()) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_email))
            return
        }

        if (!email.isEmailValid()) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_correct_email))
            return
        }

        if (passportNumber.isEmpty()) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_passport))
            return
        }

        if (passportNumber.length >9) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_correct_passport))
            return
        }


        if (docNumber.isEmpty()) {
            if (radionId.isChecked) {
                mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_nid))
            } else if (radioBirthCertificate.isChecked) {
                mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_bc))
            }

            return
        }


        if (radionId.isChecked && (docNumber.length < 10 || docNumber.length > 16)) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_valid_nid_number))
            return
        }

        if (radioBirthCertificate.isChecked && (docNumber.length != 17)) {
            mDetailsCallBack?.showToastMessage(getString(R.string.txt_error_valid_bc_number))
            return
        }


        val item = UmrahHajjPersonalPostModel(
            docNumber,
            docType,
            email,
            gender,
            AppPreference.userNumber!!,
            passportNumber,
            packageID,
            name

        )

        viewmodel.postUmrahPersonalInfo(item)

        Log.e("UMRAH",Gson().toJson(item))
    }

    private fun initObserver()
    {

        viewmodel.umrah_hajj_personal_info.observe(viewLifecycleOwner)
        {
            when(it)
            {

                is UmrahHajjResource.Error ->
                {
                    progressLayout.visibility = View.GONE
                    btnNext.show()
                    mDetailsCallBack?.showToastMessage(it.errorMsg)
                }
                UmrahHajjResource.Loading ->
                {
                    progressLayout.visibility = View.VISIBLE
                    btnNext.hide()
                }
                is UmrahHajjResource.UmrahHajjPersonalPostResponse ->
                {
                    progressLayout.visibility = View.GONE
                    btnNext.show()

                    when(it.data.data?.status)
                    {
                        200 ->
                        {

                            mDetailsCallBack?.addFragmentToStackAndShow(
                                PaymentFragment.newInstance(
                                    name,
                                    email,
                                    "$packagePrice টাকা",
                                    it.data.data.data?.trackingNumber.toString(),
                                    UMRAH_SERVICE_ID,
                                    resources.getString(R.string.cat_umrah_details),
                                    "",
                                    PAYMENT_UMRAH_HAJJ_REG,
                                    "বুকিং প্যাকেজ",
                                    "https://docs.google.com/document/d/1lYjdU9rYw9emzxm_yYkWwEEYeVcuZTTlIK1O2FcS_3s/edit?usp=sharing"

                                )
                            )

                        }

                        400 ->
                        {

                                viewmodel.checkUmrahPersonalInfo(passportNumber)
                                progressLayout.visibility = View.VISIBLE
                                btnNext.hide()

                        }

                        else ->
                        {
                            mDetailsCallBack?.showToastMessage("Something went wrong! Please try again")
                        }
                    }
                }

                is UmrahHajjResource.CheckUmrahPersonalInfo ->
                {
                    progressLayout.visibility = View.GONE
                    btnNext.show()

                    when(it.data.data?.status)
                    {
                        200 ->
                        {

                            if(it.data.data.data?.get(0)?.paymentStatus == "unpaid") {

                                mDetailsCallBack?.addFragmentToStackAndShow(
                                    PaymentFragment.newInstance(
                                        name,
                                        email,
                                        "$packagePrice টাকা",
                                        it.data.data.data?.get(0)?.trackingNumber.toString(),
                                        UMRAH_SERVICE_ID,
                                        resources.getString(R.string.cat_umrah_details),
                                        "",
                                        PAYMENT_UMRAH_HAJJ_REG,
                                        "বুকিং প্যাকেজ",
                                        "https://docs.google.com/document/d/1lYjdU9rYw9emzxm_yYkWwEEYeVcuZTTlIK1O2FcS_3s/edit?usp=sharing"
                                    )
                                )
                            }
                            else
                                mDetailsCallBack?.showToastMessage("Registration already completed!")




                        }

                        else -> mDetailsCallBack?.showToastMessage("Something went wrong! Please try again")
                    }

                }


                else -> Unit
            }
        }

    }

}