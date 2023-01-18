package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.Manifest
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentHajjPersonalInfoBinding
import com.gakk.noorlibrary.model.hajjpackage.PersonalInfoItem
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class HajjPersonalInfoFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentHajjPersonalInfoBinding
    private var year: Int = 0
    private var date: Int = 0
    private var month: Int = 0
    private var certificateType: Int = 0
    private var fileName: String? = null
    private var file: File? = null
    var bitmap: Bitmap? = null
    private var actionCamera: (() -> Unit)? = {
        mCallback?.performAction(Manifest.permission.CAMERA, performCameraAction)
    }
    private var actionGalleryPhoto: (() -> Unit)? = {
        mCallback?.performAction(Manifest.permission.READ_EXTERNAL_STORAGE, performGalaryAction)
    }
    private val takeImageResult =
        registerForActivityResult(TakePictureWithUriReturnContract()) { (isSuccess, imageUri) ->
            if (isSuccess) {
                fileName = imageUri.getName(requireContext())
                bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    imageUri
                )

                file = bitmap?.let { ImageHelper.getFileFromBitmap(it) }

                if (certificateType == 0) {
                    binding.tvUploadFileNid.setText(fileName)
                } else {
                    binding.tvUploadFileBirthCer.setText(fileName)
                }

            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                fileName = uri.getName(requireContext())
                bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    uri
                )

                file = bitmap?.let { ImageHelper.getFileFromBitmap(it) }
                if (certificateType == 0) {
                    binding.tvUploadFileNid.setText(fileName)
                } else {
                    binding.tvUploadFileBirthCer.setText(fileName)
                }
            }
        }

    private lateinit var viewModel: PreregistrationViewModel
    private var gender = "Male"
    private var docType = "NID"
    private lateinit var docNumber: String
    private lateinit var nameGuardian: String
    private lateinit var maritalStatus: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let {
            context?.setApplicationLanguage(it)
        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hajj_personal_info,
            container,
            false
        )

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]
        binding.btnNext.handleClickEvent {


            val name = binding.etYourName.text.toString()
            val dob = binding.tvDOB.text.toString()

            if (binding.radionId.isChecked) {
                docType = "NID"
                docNumber = binding.etNid.text.toString()
            } else if (binding.radioBirthCertificate.isChecked) {
                docType = "BC"
                docNumber = binding.etBirthCertificate.text.toString()
            }

            if (binding.radionIdMarried.isChecked) {
                nameGuardian = binding.etHusbandName.text.toString()
                maritalStatus = "Married"
            } else if (binding.radioUnMarried.isChecked) {
                nameGuardian = binding.etFatherName.text.toString()
                maritalStatus = "UnMarried"
            }

            if (binding.radioMale.isChecked) {
                gender = "Male"
            } else if (binding.radioFemale.isChecked) {
                gender = "Female"
            }

            if (name.isEmpty()) {
                mCallback?.showToastMessage(getString(R.string.txt_error_name))
                return@handleClickEvent
            }

            if (dob.equals("MM-DD-YYYY")) {
                mCallback?.showToastMessage(getString(R.string.txt_error_dob))
                return@handleClickEvent
            }

            if (docNumber.isEmpty()) {
                if (binding.radionId.isChecked) {
                    mCallback?.showToastMessage(getString(R.string.txt_error_nid))
                } else if (binding.radioBirthCertificate.isChecked) {
                    mCallback?.showToastMessage(getString(R.string.txt_error_bc))
                }

                return@handleClickEvent
            }


            if (binding.radionId.isChecked && (docNumber.length < 10 || docNumber.length > 16)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_valid_nid_number))
                return@handleClickEvent
            }

            if (binding.radioBirthCertificate.isChecked && (docNumber.length != 17)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_valid_bc_number))
                return@handleClickEvent
            }

            if (binding.radionId.isChecked && file == null) {
                mCallback?.showToastMessage(getString(R.string.txt_error_file_nid))
                return@handleClickEvent
            }

            if (binding.radioBirthCertificate.isChecked && file == null) {
                mCallback?.showToastMessage(getString(R.string.txt_error_file_bc))
                return@handleClickEvent
            }

            if (nameGuardian.isEmpty()) {

                if (binding.radionIdMarried.isChecked) {
                    mCallback?.showToastMessage("স্বামী/স্ত্রী এর নাম লিখুন")
                } else if (binding.radioUnMarried.isChecked) {
                    mCallback?.showToastMessage("পিতার নাম লিখুন")
                }
                return@handleClickEvent
            }

            val item = PersonalInfoItem(
                file,
                name, dob, gender, docType, docNumber,
                maritalStatus, nameGuardian
            )
            viewModel.selectItem(item)

            viewModel.gotoNext(1)
        }

        currentCalender()

        binding.layoutDOB.handleClickEvent {
            datePickerDialogPopUp()
        }

        binding.imgUploadNid.handleClickEvent {

            certificateType = 0

            mCallback?.showDialogWithActionAndParam(
                DialogType.ImagePickOptionDialog,
                actionOneWithNoParameter = actionCamera,
                actionTwoWithNoParameter = actionGalleryPhoto
            )
        }

        binding.imgUploadBirthCer.handleClickEvent {
            certificateType = 1
            mCallback?.showDialogWithActionAndParam(
                DialogType.ImagePickOptionDialog,
                actionOneWithNoParameter = actionCamera,
                actionTwoWithNoParameter = actionGalleryPhoto
            )
        }

        binding.radioGroupGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioMale -> {

                }

                else -> {

                }
            }
        }
        binding.radioGroupNid.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radionId -> {

                    binding.clNid.visibility = View.VISIBLE
                    binding.clBirthCertificate.visibility = View.GONE
                }

                else -> {

                    binding.clNid.visibility = View.GONE
                    binding.clBirthCertificate.visibility = View.VISIBLE
                }
            }
        }

        binding.radioGroupMarried.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radionIdMarried -> {

                    binding.clHusbandName.visibility = View.VISIBLE
                    binding.clFatherName.visibility = View.GONE
                }

                else -> {

                    binding.clHusbandName.visibility = View.GONE
                    binding.clFatherName.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjPersonalInfoFragment()
    }

    private fun currentCalender() {
        val calendar = Calendar.getInstance()
        date = calendar[Calendar.DAY_OF_MONTH]
        month = calendar[Calendar.MONTH]
        year = calendar[Calendar.YEAR]
    }

    private fun datePickerDialogPopUp() {
        val datePicker = DatePickerDialog(requireContext(), { datePicker, year, month, date ->
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val mCalendar = Calendar.getInstance()

            // Set static variables of Calendar instance
            mCalendar[Calendar.YEAR] = year

            mCalendar[Calendar.MONTH] = month

            mCalendar[Calendar.DAY_OF_MONTH] = date

            val txt = "" + (month + 1) + "-" + date.toString() + "-" + year
            when (Util.checkSelectedDate(dateFormat.format(mCalendar.time))) {
                true -> {
                    binding.tvDOB.text = txt
                }
                false -> {
                    mCallback?.showToastMessage(getString(R.string.txt_error_correct_dob))
                }
            }

        }, year, month, date)

        datePicker.show()
    }

    private var performCameraAction: () -> Unit = {

        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri -> takeImageResult.launch(uri) }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".png", requireActivity().cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            requireContext().applicationContext,
            "com.gakk.noorlibrary.provider",
            tmpFile
        )
    }

    private var performGalaryAction: () -> Unit = {
        selectImageFromGalleryResult.launch("image/*")
    }

}




