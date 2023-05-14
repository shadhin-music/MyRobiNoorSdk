package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.trackuser.TrackUserResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class AddUserTrackigViewModel(private val repository: RestRepository) : ViewModel() {

    var trackUser: MutableLiveData<Resource<TrackUserResponse>> =
        MutableLiveData()

    companion object {
        val FACTORY = singleArgViewModelFactory(::AddUserTrackigViewModel)
    }


    fun addTrackDataUser(msisdn: String, pageName: String) {
        viewModelScope.launch {
            trackUser.postValue(Resource.loading(data = null))
            try {
                trackUser.postValue(
                    Resource.success(
                        data = repository.userTrackAdd(msisdn, pageName)
                    )
                )

            } catch (e: Exception) {
                trackUser.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}