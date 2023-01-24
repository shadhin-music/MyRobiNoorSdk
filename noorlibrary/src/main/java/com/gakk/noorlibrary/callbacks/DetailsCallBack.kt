package com.gakk.noorlibrary.callbacks

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.databinding.LayoutRozaPrimaryHeaderBinding
import com.gakk.noorlibrary.ui.adapter.DivisionCallbackFunc
import com.gakk.noorlibrary.ui.adapter.SurahListAdapter
import com.gakk.noorlibrary.ui.fragments.PageReloadCallBack

val MORE = "more"
val FAV = "fav"
val FAV_FILLED = "fav_filled"
val NOTFICATION = "notification"
val SHARE = "share"

interface DetailsCallBack {

    fun handleNavigationUpAction()
    fun addFragmentToStackAndShow(fragment: Fragment)
    fun setToolBarTitle(title: String?)
    fun toggleToolBarActionIconsVisibility(
        isVisible: Boolean,
        buttonType: ActionButtonType? = null,
        @DrawableRes
        buttonRes: Int? = null
    )
    fun showDialogWithActionAndParam(
        dialogType: DialogType, surahListAdapter: SurahListAdapter? = null,
        binding: LayoutRozaPrimaryHeaderBinding? = null,
        actionWithSingleParam: ((String) -> Unit)? = null,
        actionOneWithNoParameter: (() -> Unit)? = null,
        actionTwoWithNoParameter: (() -> Unit)? = null,
        pageReloadCallBack: PageReloadCallBack? = null,
        title: String? = null,
        description: String? = null,
        numberAyah: String? = null,
        textAyah: String? = null,
        textName: String? = null,
        divisionCallbackFunc: DivisionCallbackFunc? = null
    )

    fun showAyaListBottomSheetDialog(id: String)
    fun getWindowHeight(): Int
    fun setOrUpdateActionButtonTag(tag: String, buttonType: ActionButtonType)
    fun setActionOfActionButton(action: () -> Unit, actionButtonType: ActionButtonType)
    fun getScreenWith(): Int
    fun showToastMessage(message: String)
    fun startDownloadIfPermissionGiven(action: () -> Unit)
    fun startDownloadCertificateIfPermissionGiven(action: () -> Unit)
    fun openFileInGalary(path: String)
    fun performAction(nameOfPermision: String, action: () -> Unit)
    fun openUrl(url: String)
    fun showDialer(number: String?)
}

sealed class ActionButtonType {
    object TypeOne : ActionButtonType()
    object TypeTwo : ActionButtonType()
    object TypeThree : ActionButtonType()
}

