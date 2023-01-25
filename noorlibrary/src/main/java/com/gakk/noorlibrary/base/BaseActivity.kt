package com.gakk.noorlibrary.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.*
import com.gakk.noorlibrary.ui.adapter.DivisionAdapter
import com.gakk.noorlibrary.ui.adapter.DivisionCallbackFunc
import com.gakk.noorlibrary.ui.adapter.SurahListAdapter
import com.gakk.noorlibrary.ui.fragments.PageReloadCallBack
import com.gakk.noorlibrary.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseActivity : AppCompatActivity() {

    companion object {
        lateinit var alertDialog: AlertDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun performActionifGivenPermision(action: () -> Unit) {
        action()
    }

    open fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    open fun setStatusbarTextDark() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
    }

    fun showDialogWithParamByType(
        dilogType: DialogType,
        surahListAdapter: SurahListAdapter? = null,
        binding: View? = null,
        actionWithSingleParam: ((String) -> Unit)? = null,
        actionOneWithNoParameter: (() -> Unit)? = null,
        actionTwoWithNoParameter: (() -> Unit)? = null,
        pageReloadCallBack: PageReloadCallBack? = null,
        title: String? = null,
        description: String? = null,
        numberAyah: String? = null,
        textAyah: String? = null,
        divisionCallbackFunc: DivisionCallbackFunc? = null
    ) {
        when (dilogType) {
            DialogType.SurahListDialog -> showSurahListDialog(
                surahListAdapter,
                pageReloadCallBack!!
            )

            DialogType.SurahActionListDialog -> showSurahOrAyahActionsDialog(DialogType.SurahActionListDialog)
            DialogType.AyahActionListDialog -> showSurahOrAyahActionsDialog(
                DialogType.AyahActionListDialog,
                numberAyah, textAyah
            )
            DialogType.RozaNotificationSettingDialog -> showRozaNotificationSettingAlert()
            DialogType.RozaDivisionListDialog -> {
                showDivisionListDialog(binding, divisionCallbackFunc = divisionCallbackFunc)
            }

            DialogType.ImagePickOptionDialog -> {
                showImagePickOptioneDialog(
                    actionCamera = actionOneWithNoParameter,
                    actionGalley = actionTwoWithNoParameter
                )
            }

            DialogType.DownloadConfirmDialog -> {
                showDownloadDialog(actionOneWithNoParameter)
            }

            else -> {
                showZakatInfoDialog(title, description)
            }
        }
    }

    fun showZakatInfoDialog(title: String?, description: String?) {

        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_info_show, null,false
        )

        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        val tvTitleInfo = dialogView.findViewById<AppCompatTextView>(R.id.tvTitleInfo)
        val tvDescription = dialogView.findViewById<AppCompatTextView>(R.id.tvDescription)
        val imgClose = dialogView.findViewById<AppCompatImageView>(R.id.imgClose)

        tvTitleInfo.text = title
        tvDescription.text = description

        alertDialog = customDialog.show()

        imgClose.handleClickEvent { alertDialog.dismiss() }
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(true)

    }

    fun showSurahListDialog(adapter: SurahListAdapter?, pageReloadCallBack: PageReloadCallBack) {

        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_surah_list, null,false
        )

        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)

        val rvSurahList = view.findViewById<RecyclerView>(R.id.rvSurahList)

        rvSurahList.adapter = adapter
        rvSurahList.setDivider(R.drawable.recycler_view_divider)

        setSurahListDiaLogViewsClickEvents(view, pageReloadCallBack, adapter)
    }


    private fun setSurahListDiaLogViewsClickEvents(
        binding: View,
        pageReloadCallBack: PageReloadCallBack,
        adapter: SurahListAdapter?
    ) {

        val btnDismiss = binding.findViewById<ImageButton>(R.id.btnDismiss)
        val btnSelectSurah = binding.findViewById<AppCompatButton>(R.id.btnSelectSurah)

        btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
        btnSelectSurah.handleClickEvent {
            alertDialog.dismiss()
            var selectedId = adapter?.getViewHolderSelectionControl()?.getSelectedId()
            SurahListControl.updateSelectedIndex(selectedId!!)
            pageReloadCallBack.reloadPage()
        }
    }

    fun showImagePickOptioneDialog(actionCamera: (() -> Unit)?, actionGalley: (() -> Unit)?) {


        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_photo_option, null,false
        )


        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(true)

        setImagePickDialogViewClickEvents(
            view,
            actionCamera = actionCamera,
            actionGalley = actionGalley
        )

    }


    fun setImagePickDialogViewClickEvents(
        binding: View,
        actionCamera: (() -> Unit)?,
        actionGalley: (() -> Unit)?
    ) {

        val btnCamera = binding.findViewById<AppCompatImageButton>(R.id.btnCamera)
        val btnGallery = binding.findViewById<ImageButton>(R.id.btnGallery)

        btnCamera.handleClickEvent {
            actionCamera?.invoke()
            alertDialog.dismiss()
        }
        btnGallery.handleClickEvent {
            actionGalley?.invoke()
            alertDialog.dismiss()
        }
    }

    fun showSurahOrAyahActionsDialog(
        dilogType: DialogType,
        numberAyah: String? = null,
        textAyah: String? = null
    ) {

        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_surah_or_ayah_action_list, null,false
        )

        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)


        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)

        val tvSurahNameOrAyahTxt = view.findViewById<AppCompatTextView>(R.id.tvSurahNameOrAyahTxt)
        val tvSurahOrAyahNumber = view.findViewById<AppCompatTextView>(R.id.tvSurahOrAyahNumber)
        val layoutCopyAction = view.findViewById<ConstraintLayout>(R.id.layoutCopyAction)
        val layoutShareAction = view.findViewById<ConstraintLayout>(R.id.layoutShareAction)

        if (dilogType == DialogType.AyahActionListDialog) {
            tvSurahNameOrAyahTxt.setText(resources.getText(R.string.ayah))
            tvSurahOrAyahNumber.setText(numberAyah)
        }

        layoutCopyAction.handleClickEvent {
            if (textAyah != null) {
                this.copyToClipboard(textAyah)
                Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_LONG).show()
                alertDialog.dismiss()
            }
        }

        layoutShareAction.handleClickEvent {
            val shareText: String = textAyah!!
            val sendIntent = Intent()
            sendIntent.action = "android.intent.action.SEND"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            sendIntent.putExtra("android.intent.extra.TEXT", shareText)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

            alertDialog.dismiss()
        }

        setUpActionLayouts(
            layoutType = ActionLayoutType.CopyActionLayout,
            binding = view,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.ShareActionLayout,
            binding = view,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.FavouriteActionLayout,
            binding = view,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.PlayActionLayout,
            binding = view,
            dilogType = dilogType
        )
        setSurahOrAyahActionViewsClickEvents(view)
    }


    fun setSurahOrAyahActionViewsClickEvents(binding: View) {

        binding.findViewById<ImageButton>(R.id.btnDismiss).handleClickEvent {
            alertDialog.dismiss()
        }
    }

    private fun setUpActionLayouts(
        dilogType: DialogType,
        layoutType: ActionLayoutType,
        binding: View
    ) {

        when (layoutType) {
            ActionLayoutType.CopyActionLayout -> {
                val btnAction = binding.findViewById<ImageView>(R.id.btnAction)
                btnAction.setImageResource(R.drawable.ic_copy)
                when (dilogType) {
                    DialogType.SurahActionListDialog -> {
                        val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)
                        tvActionName.setText(resources.getString(R.string.copy_surah))
                    }
                    DialogType.AyahActionListDialog -> {
                        val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)
                        tvActionName.setText(resources.getString(R.string.copy_ayah))
                    }
                    else -> {}
                }

            }
            ActionLayoutType.ShareActionLayout -> {

                val btnAction = binding.findViewById<ImageButton>(R.id.btnAction)
                val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)

                btnAction.setImageResource(R.drawable.ic_share)
                tvActionName.setText(resources.getString(R.string.share))
            }
            ActionLayoutType.FavouriteActionLayout -> {
                val btnAction = binding.findViewById<ImageButton>(R.id.btnAction)
                val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)

                btnAction.setImageResource(R.drawable.ic_favorite)
                tvActionName.setText(resources.getString(R.string.favourite))
            }
            ActionLayoutType.PlayActionLayout -> {
                val btnAction = binding.findViewById<ImageButton>(R.id.btnAction)
                val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)

                btnAction.setImageResource(R.drawable.ic_play_no_bg)
                tvActionName.setText(resources.getString(R.string.play_audio))
            }
        }

        when (dilogType) {
            DialogType.SurahActionListDialog -> {
                val layoutFavAction = binding.findViewById<ConstraintLayout>(R.id.layoutFavAction)
                val layoutPlayAudioAction = binding.findViewById<ConstraintLayout>(R.id.layoutPlayAudioAction)

                layoutFavAction.visibility = VISIBLE
                layoutPlayAudioAction.visibility = VISIBLE
            }
            DialogType.AyahActionListDialog -> {
                val layoutFavAction = binding.findViewById<ConstraintLayout>(R.id.layoutFavAction)
                val layoutPlayAudioAction = binding.findViewById<ConstraintLayout>(R.id.layoutPlayAudioAction)

                layoutFavAction.visibility = GONE
                layoutPlayAudioAction.visibility = GONE
            }
            else -> {}
        }
    }

    fun showDownloadDialog(action: (() -> Unit)?) {


        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_confirmation_dialog, null,false
        )


        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        val btnYes = view.findViewById<AppCompatButton>(R.id.btnYes)

        btnYes.handleClickEvent {
            action?.invoke()
            alertDialog.dismiss()
        }
        val btnNo = view.findViewById<AppCompatButton>(R.id.btnNo)

        btnNo.handleClickEvent {
            alertDialog.dismiss()
        }
    }

    fun showRozaNotificationSettingAlert() {

        val view = LayoutInflater.from(this).inflate(
            R.layout.layout_roza_notification_settings, null,false
        )


        val dialogView: View = view
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        val btnDismiss = view.findViewById<ImageButton>(R.id.btnDismiss)

        btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
        setUpRozaNotificationControls(view)
    }

    fun setUpRozaNotificationControls(binding: View) {

        val layoutSehriAlertControl = binding.findViewById<ConstraintLayout>(R.id.layoutSehriAlertControl)

        layoutSehriAlertControl.let {

            val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)
            val checkboxActionIcon = binding.findViewById<AppCompatCheckBox>(R.id.checkboxActionIcon)
            tvActionName.setText(R.string.sehri_alert)
            checkboxActionIcon.setButtonDrawable(R.drawable.selector_notification)

            it.handleClickEvent {


                when (checkboxActionIcon.isChecked) {
                    true -> {
                        checkboxActionIcon.isChecked = false
                        AppPreference.sehriAlertOn = false
                    }
                    false -> {
                        checkboxActionIcon.isChecked = true
                        AppPreference.sehriAlertOn = true
                        AppPreference.nitificationflag = true
                    }
                }
            }


            checkboxActionIcon.isChecked = AppPreference.sehriAlertOn

        }

        val layoutIfterAlertControl = binding.findViewById<ConstraintLayout>(R.id.layoutIfterAlertControl)

        val tvActionName = binding.findViewById<AppCompatTextView>(R.id.tvActionName)
        val checkboxActionIcon = binding.findViewById<AppCompatCheckBox>(R.id.checkboxActionIcon)


        layoutIfterAlertControl.let {
            tvActionName.setText(R.string.ifter_alert)
            checkboxActionIcon.setButtonDrawable(R.drawable.selector_notification)
            it.handleClickEvent {
                when (checkboxActionIcon.isChecked) {
                    true -> {
                        checkboxActionIcon.isChecked = false
                        AppPreference.ifterAlertOn = false
                    }
                    false -> {
                        checkboxActionIcon.isChecked = true
                        AppPreference.ifterAlertOn = true
                        AppPreference.nitificationflag = true
                    }
                }
            }

            checkboxActionIcon.isChecked = AppPreference.ifterAlertOn

        }

        val layoutNotificationSoundControl = binding.findViewById<ConstraintLayout>(R.id.layoutNotificationSoundControl)



        layoutNotificationSoundControl.let {
            tvActionName.setText(R.string.notification_sound)
            checkboxActionIcon.setButtonDrawable(R.drawable.selector_check_box)
            it.handleClickEvent {
                when (checkboxActionIcon.isChecked) {
                    true -> {
                        checkboxActionIcon.isChecked = false
                        AppPreference.sehriOrifterAlertSoundOn = false
                    }
                    false -> {
                        checkboxActionIcon.isChecked = true
                        AppPreference.sehriOrifterAlertSoundOn = true
                    }
                }
            }

            checkboxActionIcon.isChecked = AppPreference.sehriOrifterAlertSoundOn

        }

        val layoutNotificationVibrationControl = binding.findViewById<ConstraintLayout>(R.id.layoutNotificationVibrationControl)

        layoutNotificationVibrationControl.let {
            tvActionName.setText(R.string.notification_vibration)
            checkboxActionIcon.setButtonDrawable(R.drawable.selector_check_box)
            it.handleClickEvent {
                when (checkboxActionIcon.isChecked) {
                    true -> {
                        checkboxActionIcon.isChecked = false
                        AppPreference.sehriOrifterAlertVibrationOn = false

                    }
                    false -> {
                        checkboxActionIcon.isChecked = true
                        AppPreference.sehriOrifterAlertVibrationOn = true
                    }
                }
            }

            checkboxActionIcon.isChecked = AppPreference.sehriOrifterAlertVibrationOn

        }
    }

    fun showDivisionListDialog(
        rozaHeaderBinding: View?,
        divisions: List<String>? = resources.getStringArray(R.array.bd_divisions)
            .toList(),
        divisionCallbackFunc: DivisionCallbackFunc? = null
    ) {

        var binding: LayoutRozaDivisionListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_roza_division_list,
            null,
            false
        )


        // val divisions = resources.getStringArray(R.array.bd_divisions).toList()
        val adapter = divisions?.let { DivisionAdapter(it) }
        binding.rvDistricts.adapter = adapter
        adapter?.divisionCallbackFunc = divisionCallbackFunc

        val dialogView: View = binding.root
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()
        binding.tvDivision.setText("")
        binding.btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
    }

    fun getWindowHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}

sealed class ActionLayoutType {
    object CopyActionLayout : ActionLayoutType()
    object ShareActionLayout : ActionLayoutType()
    object FavouriteActionLayout : ActionLayoutType()
    object PlayActionLayout : ActionLayoutType()
}

sealed class DialogType {
    object SurahListDialog : DialogType()
    object SurahActionListDialog : DialogType()
    object AyahActionListDialog : DialogType()
    object RozaNotificationSettingDialog : DialogType()
    object RozaDivisionListDialog : DialogType()
    object ImagePickOptionDialog : DialogType()
    object DownloadConfirmDialog : DialogType()
    object ZakatInfoShow : DialogType()
}
