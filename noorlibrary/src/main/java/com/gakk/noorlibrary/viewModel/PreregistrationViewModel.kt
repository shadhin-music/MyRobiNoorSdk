package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gakk.noorlibrary.model.hajjpackage.PersonalInfoItem
import com.gakk.noorlibrary.model.hajjpackage.PreRegisteredUserInfo

internal class PreregistrationViewModel : ViewModel() {

    private val mutableSelectedItem = MutableLiveData<PersonalInfoItem>()
    val selectedItem: LiveData<PersonalInfoItem> get() = mutableSelectedItem

    private val pagerPosition = MutableLiveData<Int>()
    val pagerSelectedPos: LiveData<Int> get() = pagerPosition

    private val preRegisteredUserInfo = MutableLiveData<PreRegisteredUserInfo?>()
    val selectedUserInfo: MutableLiveData<PreRegisteredUserInfo?> get() = preRegisteredUserInfo

    fun selectItem(item: PersonalInfoItem) {
        mutableSelectedItem.value = item
    }

    fun gotoNext(position: Int) {
        pagerPosition.value = position
    }

    fun selectedInfo(info: PreRegisteredUserInfo?) {
        preRegisteredUserInfo.value = info
    }
}