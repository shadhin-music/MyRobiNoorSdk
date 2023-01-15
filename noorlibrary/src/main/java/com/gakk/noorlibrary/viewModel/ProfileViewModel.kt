package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.profile.UserInfoResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: RestRepository) : ViewModel() {
    var profile: MutableLiveData<Resource<UserInfoResponse>> = MutableLiveData()


    companion object {
        val FACTORY = singleArgViewModelFactory(::ProfileViewModel)
    }

    fun getUserInfo(id: String) {
        viewModelScope.launch {
            profile.postValue(Resource.loading(data = null))
            try {
                profile.postValue(Resource.success(data = repository.getUserInfo(id)))
            } catch (e: Exception) {
                profile.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

}