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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
        binding: LayoutRozaPrimaryHeaderBinding? = null,
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

        val binding: LayoutInfoShowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_info_show,
            null,
            false
        )
        val dialogView: View = binding.root
        val customDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        customDialog.setView(dialogView)
        binding.tvTitleInfo.text = title
        binding.tvDescription.text = description

        alertDialog = customDialog.show()

        binding.imgClose.handleClickEvent { alertDialog.dismiss() }
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(true)

    }

    fun showSurahListDialog(adapter: SurahListAdapter?, pageReloadCallBack: PageReloadCallBack) {
        var binding: LayoutSurahListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_surah_list,
            null,
            false
        )
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

        binding.rvSurahList.adapter = adapter
        binding.rvSurahList.setDivider(R.drawable.recycler_view_divider)

        setSurahListDiaLogViewsClickEvents(binding, pageReloadCallBack, adapter)
    }


    private fun setSurahListDiaLogViewsClickEvents(
        binding: LayoutSurahListBinding,
        pageReloadCallBack: PageReloadCallBack,
        adapter: SurahListAdapter?
    ) {

        binding.btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
        binding.btnSelectSurah.handleClickEvent {
            alertDialog.dismiss()
            var selectedId = adapter?.getViewHolderSelectionControl()?.getSelectedId()
            SurahListControl.updateSelectedIndex(selectedId!!)
            pageReloadCallBack.reloadPage()
        }
    }

    fun showImagePickOptioneDialog(actionCamera: (() -> Unit)?, actionGalley: (() -> Unit)?) {

        val binding: LayoutPhotoOptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_photo_option,
            null,
            false
        )
        val dialogView: View = binding.root
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
            binding,
            actionCamera = actionCamera,
            actionGalley = actionGalley
        )

    }


    fun setImagePickDialogViewClickEvents(
        binding: LayoutPhotoOptionBinding,
        actionCamera: (() -> Unit)?,
        actionGalley: (() -> Unit)?
    ) {
        binding.btnCamera.handleClickEvent {
            actionCamera?.invoke()
            alertDialog.dismiss()
        }
        binding.btnGallery.handleClickEvent {
            actionGalley?.invoke()
            alertDialog.dismiss()
        }
    }

    fun showSurahOrAyahActionsDialog(
        dilogType: DialogType,
        numberAyah: String? = null,
        textAyah: String? = null
    ) {
        var binding: LayoutSurahOrAyahActionListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_surah_or_ayah_action_list,
            null,
            false
        )
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

        if (dilogType == DialogType.AyahActionListDialog) {
            binding.tvSurahNameOrAyahTxt.setText(resources.getText(R.string.ayah))
            binding.tvSurahOrAyahNumber.setText(numberAyah)
        }

        binding.layoutCopyAction.root.handleClickEvent {
            if (textAyah != null) {
                this.copyToClipboard(textAyah)
                Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_LONG).show()
                alertDialog.dismiss()
            }
        }

        binding.layoutShareAction.root.handleClickEvent {
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
            binding = binding,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.ShareActionLayout,
            binding = binding,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.FavouriteActionLayout,
            binding = binding,
            dilogType = dilogType
        )
        setUpActionLayouts(
            layoutType = ActionLayoutType.PlayActionLayout,
            binding = binding,
            dilogType = dilogType
        )
        setSurahOrAyahActionViewsClickEvents(binding)
    }


    fun setSurahOrAyahActionViewsClickEvents(binding: LayoutSurahOrAyahActionListBinding) {
        binding.btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
    }

    private fun setUpActionLayouts(
        dilogType: DialogType,
        layoutType: ActionLayoutType,
        binding: LayoutSurahOrAyahActionListBinding
    ) {

        when (layoutType) {
            ActionLayoutType.CopyActionLayout -> {
                binding.layoutCopyAction.btnAction.setImageResource(R.drawable.ic_copy)
                when (dilogType) {
                    DialogType.SurahActionListDialog -> {
                        binding.layoutCopyAction.tvActionName.setText(resources.getString(R.string.copy_surah))
                    }
                    DialogType.AyahActionListDialog -> {
                        binding.layoutCopyAction.tvActionName.setText(resources.getString(R.string.copy_ayah))
                    }
                    else -> {}
                }

            }
            ActionLayoutType.ShareActionLayout -> {
                binding.layoutShareAction.btnAction.setImageResource(R.drawable.ic_share)
                binding.layoutShareAction.tvActionName.setText(resources.getString(R.string.share))
            }
            ActionLayoutType.FavouriteActionLayout -> {
                binding.layoutFavAction.btnAction.setImageResource(R.drawable.ic_favorite)
                binding.layoutFavAction.tvActionName.setText(resources.getString(R.string.favourite))
            }
            ActionLayoutType.PlayActionLayout -> {
                binding.layoutPlayAudioAction.btnAction.setImageResource(R.drawable.ic_play_no_bg)
                binding.layoutPlayAudioAction.tvActionName.setText(resources.getString(R.string.play_audio))
            }
        }

        when (dilogType) {
            DialogType.SurahActionListDialog -> {
                binding.layoutFavAction.root.visibility = VISIBLE
                binding.layoutPlayAudioAction.root.visibility = VISIBLE
            }
            DialogType.AyahActionListDialog -> {
                binding.layoutFavAction.root.visibility = GONE
                binding.layoutPlayAudioAction.root.visibility = GONE
            }
            else -> {}
        }
    }

    fun showDownloadDialog(action: (() -> Unit)?) {
        var binding: LayoutConfirmationDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_confirmation_dialog,
            null,
            false
        )


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

        binding.btnYes.handleClickEvent {
            action?.invoke()
            alertDialog.dismiss()
        }

        binding.btnNo.handleClickEvent {
            alertDialog.dismiss()
        }
    }

    fun showRozaNotificationSettingAlert() {

        var binding: LayoutRozaNotificationSettingsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_roza_notification_settings,
            null,
            false
        )


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

        binding.btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
        setUpRozaNotificationControls(binding)
    }

    fun setUpRozaNotificationControls(binding: LayoutRozaNotificationSettingsBinding) {

        binding.layoutSehriAlertControl.let {
            it.tvActionName.setText(R.string.sehri_alert)
            it.checkboxActionIcon.setButtonDrawable(R.drawable.selector_notification)
            it.root.handleClickEvent {
                when (it.checkboxActionIcon.isChecked) {
                    true -> {
                        it.checkboxActionIcon.isChecked = false
                        AppPreference.sehriAlertOn = false
                    }
                    false -> {
                        it.checkboxActionIcon.isChecked = true
                        AppPreference.sehriAlertOn = true
                        AppPreference.nitificationflag = true
                    }
                }
            }
            it.isChecked = AppPreference.sehriAlertOn
        }

        binding.layoutIfterAlertControl.let {
            it.tvActionName.setText(R.string.ifter_alert)
            it.checkboxActionIcon.setButtonDrawable(R.drawable.selector_notification)
            it.root.handleClickEvent {
                when (it.checkboxActionIcon.isChecked) {
                    true -> {
                        it.checkboxActionIcon.isChecked = false
                        AppPreference.ifterAlertOn = false
                    }
                    false -> {
                        it.checkboxActionIcon.isChecked = true
                        AppPreference.ifterAlertOn = true
                        AppPreference.nitificationflag = true
                    }
                }
            }
            it.isChecked = AppPreference.ifterAlertOn
        }

        binding.layoutNotificationSoundControl.let {
            it.tvActionName.setText(R.string.notification_sound)
            it.checkboxActionIcon.setButtonDrawable(R.drawable.selector_check_box)
            it.root.handleClickEvent {
                when (it.checkboxActionIcon.isChecked) {
                    true -> {
                        it.checkboxActionIcon.isChecked = false
                        AppPreference.sehriOrifterAlertSoundOn = false
                    }
                    false -> {
                        it.checkboxActionIcon.isChecked = true
                        AppPreference.sehriOrifterAlertSoundOn = true
                    }
                }
            }
            it.isChecked = AppPreference.sehriOrifterAlertSoundOn
        }

        binding.layoutNotificationVibrationControl.let {
            it.tvActionName.setText(R.string.notification_vibration)
            it.checkboxActionIcon.setButtonDrawable(R.drawable.selector_check_box)
            it.root.handleClickEvent {
                when (it.checkboxActionIcon.isChecked) {
                    true -> {
                        it.checkboxActionIcon.isChecked = false
                        AppPreference.sehriOrifterAlertVibrationOn = false

                    }
                    false -> {
                        it.checkboxActionIcon.isChecked = true
                        AppPreference.sehriOrifterAlertVibrationOn = true
                    }
                }
            }
            it.isChecked = AppPreference.sehriOrifterAlertVibrationOn
        }
    }

    fun showDivisionListDialog(
        rozaHeaderBinding: LayoutRozaPrimaryHeaderBinding?,
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
