package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentHajjAddressBinding
import com.gakk.noorlibrary.model.hajjpackage.PersonalInfoItem
import com.gakk.noorlibrary.model.hajjpackage.PreRegisteredUserInfo
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

class HajjAddressFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentHajjAddressBinding
    private lateinit var viewModel: PreregistrationViewModel
    private lateinit var hajjViewModel: HajjViewModel
    private lateinit var repository: RestRepository
    private lateinit var personalInfo: PersonalInfoItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hajj_address, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            personalInfo = item
        }

        binding.btnSave.handleClickEvent {

            val permanentDistrict = binding.spinnerDistrict.selectedItem.toString()
            val permanentAddress = binding.etParmanentAddress.text.toString()
            val presentAddress = binding.etPresentAddress.text.toString()
            val mobileNumber = binding.etPhone.text.toString()
            val userNumber = "880$mobileNumber"
            val email = binding.etEmailAddress.text.toString()

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
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Log.e("addHajjPreregistration", "SUCCESS${it.data?.data.toString()}")
                    binding.progressLayout.root.visibility = View.GONE
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
                    binding.progressLayout.root.visibility = View.GONE
                    mCallback?.showToastMessage(getString(R.string.api_error_msg))
                    Log.e("addHajjPreregistration", "ERROR${it.data?.error}")
                }
            }
        }
    }
}