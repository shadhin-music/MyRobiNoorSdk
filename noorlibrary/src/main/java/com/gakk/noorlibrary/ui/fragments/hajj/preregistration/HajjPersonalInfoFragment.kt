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
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.hajjpackage.PersonalInfoItem
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class HajjPersonalInfoFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
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
                    tvUploadFileNid.setText(fileName)
                } else {
                    tvUploadFileBirthCer.setText(fileName)
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
                    tvUploadFileNid.setText(fileName)
                } else {
                    tvUploadFileBirthCer.setText(fileName)
                }
            }
        }

    private lateinit var viewModel: PreregistrationViewModel
    private var gender = "Male"
    private var docType = "NID"
    private lateinit var docNumber: String
    private lateinit var nameGuardian: String
    private lateinit var maritalStatus: String
    private lateinit var tvUploadFileNid: AppCompatTextView
    private lateinit var tvUploadFileBirthCer: AppCompatTextView
    private lateinit var btnNext: AppCompatButton
    private lateinit var etYourName: AppCompatEditText
    private lateinit var tvDOB: AppCompatTextView
    private lateinit var radionId: RadioButton
    private lateinit var etNid: AppCompatEditText
    private lateinit var radioBirthCertificate: RadioButton
    private lateinit var etBirthCertificate: AppCompatEditText
    private lateinit var radionIdMarried: RadioButton
    private lateinit var etHusbandName: AppCompatEditText
    private lateinit var radioUnMarried: RadioButton
    private lateinit var etFatherName: AppCompatEditText
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var layoutDOB: ConstraintLayout
    private lateinit var imgUploadNid: ImageView
    private lateinit var imgUploadBirthCer: ImageView
    private lateinit var radioGroupNid: RadioGroup
    private lateinit var clNid: ConstraintLayout
    private lateinit var clBirthCertificate: ConstraintLayout
    private lateinit var radioGroupMarried: RadioGroup
    private lateinit var clHusbandName: ConstraintLayout
    private lateinit var clFatherName: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_personal_info,
            container, false
        )

        tvUploadFileNid = view.findViewById(R.id.tvUploadFileNid)
        tvUploadFileBirthCer = view.findViewById(R.id.tvUploadFileBirthCer)
        btnNext = view.findViewById(R.id.btnNext)
        etYourName = view.findViewById(R.id.etYourName)
        tvDOB = view.findViewById(R.id.tvDOB)
        radionId = view.findViewById(R.id.radionId)
        etNid = view.findViewById(R.id.etNid)
        radioBirthCertificate = view.findViewById(R.id.radioBirthCertificate)
        etBirthCertificate = view.findViewById(R.id.etBirthCertificate)
        radionIdMarried = view.findViewById(R.id.radionIdMarried)
        etHusbandName = view.findViewById(R.id.etHusbandName)
        radioUnMarried = view.findViewById(R.id.radioUnMarried)
        etFatherName = view.findViewById(R.id.etFatherName)
        radioMale = view.findViewById(R.id.radioMale)
        radioFemale = view.findViewById(R.id.radioFemale)
        layoutDOB = view.findViewById(R.id.layoutDOB)
        imgUploadNid = view.findViewById(R.id.imgUploadNid)
        imgUploadBirthCer = view.findViewById(R.id.imgUploadBirthCer)
        radioGroupNid = view.findViewById(R.id.radio_group_nid)
        clNid = view.findViewById(R.id.clNid)
        clBirthCertificate = view.findViewById(R.id.clBirthCertificate)
        radioGroupMarried = view.findViewById(R.id.radio_group_married)
        clHusbandName = view.findViewById(R.id.clHusbandName)
        clFatherName = view.findViewById(R.id.clFatherName)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]
        btnNext.handleClickEvent {


            val name = etYourName.text.toString()
            val dob = tvDOB.text.toString()

            if (radionId.isChecked) {
                docType = "NID"
                docNumber = etNid.text.toString()
            } else if (radioBirthCertificate.isChecked) {
                docType = "BC"
                docNumber = etBirthCertificate.text.toString()
            }

            if (radionIdMarried.isChecked) {
                nameGuardian = etHusbandName.text.toString()
                maritalStatus = "Married"
            } else if (radioUnMarried.isChecked) {
                nameGuardian = etFatherName.text.toString()
                maritalStatus = "UnMarried"
            }

            if (radioMale.isChecked) {
                gender = "Male"
            } else if (radioFemale.isChecked) {
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
                if (radionId.isChecked) {
                    mCallback?.showToastMessage(getString(R.string.txt_error_nid))
                } else if (radioBirthCertificate.isChecked) {
                    mCallback?.showToastMessage(getString(R.string.txt_error_bc))
                }

                return@handleClickEvent
            }


            if (radionId.isChecked && (docNumber.length < 10 || docNumber.length > 16)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_valid_nid_number))
                return@handleClickEvent
            }

            if (radioBirthCertificate.isChecked && (docNumber.length != 17)) {
                mCallback?.showToastMessage(getString(R.string.txt_error_valid_bc_number))
                return@handleClickEvent
            }

            if (radionId.isChecked && file == null) {
                mCallback?.showToastMessage(getString(R.string.txt_error_file_nid))
                return@handleClickEvent
            }

            if (radioBirthCertificate.isChecked && file == null) {
                mCallback?.showToastMessage(getString(R.string.txt_error_file_bc))
                return@handleClickEvent
            }

            if (nameGuardian.isEmpty()) {

                if (radionIdMarried.isChecked) {
                    mCallback?.showToastMessage("স্বামী/স্ত্রী এর নাম লিখুন")
                } else if (radioUnMarried.isChecked) {
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

        layoutDOB.handleClickEvent {
            datePickerDialogPopUp()
        }

        imgUploadNid.handleClickEvent {

            certificateType = 0

            mCallback?.showDialogWithActionAndParam(
                DialogType.ImagePickOptionDialog,
                actionOneWithNoParameter = actionCamera,
                actionTwoWithNoParameter = actionGalleryPhoto
            )
        }

        imgUploadBirthCer.handleClickEvent {
            certificateType = 1
            mCallback?.showDialogWithActionAndParam(
                DialogType.ImagePickOptionDialog,
                actionOneWithNoParameter = actionCamera,
                actionTwoWithNoParameter = actionGalleryPhoto
            )
        }

        radioGroupNid.setOnCheckedChangeListener { group, checkedId ->
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

        radioGroupMarried.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radionIdMarried -> {

                    clHusbandName.visibility = View.VISIBLE
                    clFatherName.visibility = View.GONE
                }

                else -> {

                    clHusbandName.visibility = View.GONE
                    clFatherName.visibility = View.VISIBLE
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
                    tvDOB.text = txt
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




