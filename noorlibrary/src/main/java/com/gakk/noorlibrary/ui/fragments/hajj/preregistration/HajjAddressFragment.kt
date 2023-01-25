package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.hajjpackage.PersonalInfoItem
import com.gakk.noorlibrary.model.hajjpackage.PreRegisteredUserInfo
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.isBangladeshiPhoneNumber
import com.gakk.noorlibrary.util.isEmailValid
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

internal class HajjAddressFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var viewModel: PreregistrationViewModel
    private lateinit var hajjViewModel: HajjViewModel
    private lateinit var repository: RestRepository
    private lateinit var personalInfo: PersonalInfoItem
    private lateinit var btnSave: AppCompatButton
    private lateinit var spinner_district: AppCompatSpinner
    private lateinit var etParmanentAddress: AppCompatEditText
    private lateinit var etPresentAddress: AppCompatEditText
    private lateinit var etPhone: AppCompatEditText
    private lateinit var etEmailAddress: AppCompatEditText
    private lateinit var progressLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_address,
            container, false
        )
        btnSave = view.findViewById(R.id.btnSave)
        spinner_district = view.findViewById(R.id.spinner_district)
        etParmanentAddress = view.findViewById(R.id.etParmanentAddress)
        etPresentAddress = view.findViewById(R.id.etPresentAddress)
        etPhone = view.findViewById(R.id.etPhone)
        etEmailAddress = view.findViewById(R.id.etEmailAddress)
        progressLayout = view.findViewById(R.id.progressLayout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            personalInfo = item
        }

        btnSave.handleClickEvent {

            val permanentDistrict = spinner_district.selectedItem.toString()
            val permanentAddress = etParmanentAddress.text.toString()
            val presentAddress = etPresentAddress.text.toString()
            val mobileNumber = etPhone.text.toString()
            val userNumber = "880$mobileNumber"
            val email = etEmailAddress.text.toString()

            if (permanentDistrict.equals("নির্বাচন করুন")) {
                mCallback?.showToastMessage(getString(R.string.txt_error_district))
            } else if (permanentAddress.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_permanent_address))
            } else if (presentAddress.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_persent_address))
            } else if (mobileNumber.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_mobile_number))
            } else if (mobileNumber.length < 10) {
                mCallback?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
            } else if (!isBangladeshiPhoneNumber(userNumber)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_correct_mobile_number))
            } else if (email.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_email))
            } else if (!email.isEmailValid()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_correct_email))
            } else {
                hajjViewModel.addHajjPreRegistration(
                    personalInfo.file,
                    personalInfo.name,
                    personalInfo.dateofBirth,
                    personalInfo.gender,
                    personalInfo.docType,
                    personalInfo.docNumber,
                    permanentDistrict,
                    permanentAddress,
                    presentAddress,
                    userNumber,
                    email,
                    personalInfo.maritalStatus,
                    personalInfo.maritalRefName
                )
            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            hajjViewModel = ViewModelProvider(
                this@HajjAddressFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjAddressFragment()
    }

    private fun subscribeObserver() {
        hajjViewModel.addHajjPreregistration.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("addHajjPreregistration", "loading")
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Log.e("addHajjPreregistration", "SUCCESS${it.data?.data.toString()}")
                    progressLayout.visibility = View.GONE
                    mCallback?.showToastMessage("Successfully saved!")
                    val preRegisteredUserInfo = PreRegisteredUserInfo(
                        it.data?.data?.trackingNo,
                        it.data?.data?.name,
                        it.data?.data?.email
                    )
                    viewModel.selectedInfo(preRegisteredUserInfo)
                    viewModel.gotoNext(2)
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                    mCallback?.showToastMessage(getString(R.string.api_error_msg))
                    Log.e("addHajjPreregistration", "ERROR${it.data?.error}")
                }
            }
        }
    }
}