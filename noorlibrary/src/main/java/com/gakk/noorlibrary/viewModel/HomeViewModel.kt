package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.billboard.BillboardResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjLocationShareRequestResponse
import com.gakk.noorlibrary.model.home.HomeDataResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class HomeViewModel(private val repository: RestRepository) : ViewModel() {
    var billboardResponse: MutableLiveData<Resource<BillboardResponse>> = MutableLiveData()
    var homeResponse: MutableLiveData<Resource<HomeDataResponse>> = MutableLiveData()
    var addDeviceInfo: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()

    companion object {
        val FACTORY = singleArgViewModelFactory(::HomeViewModel)
    }

    fun getBillboradData() {
        viewModelScope.launch {
            billboardResponse.postValue(Resource.loading(data = null))

            try {
                billboardResponse.postValue(Resource.success(data = repository.getBillboradList()))
            } catch (e: Exception) {
                billboardResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getHomeData() {
        viewModelScope.launch {
            homeResponse.postValue(Resource.loading(data = null))

            try {
                homeResponse.postValue(Resource.success(data = repository.getHomeDatalist()))
            } catch (e: Exception) {
                homeResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun addDeviceInfo(deviceId: String) {
        viewModelScope.launch {
            addDeviceInfo.postValue(Resource.loading(data = null))

            try {
                addDeviceInfo.postValue(Resource.success(data = repository.addDeviceinfo(deviceId)))
            } catch (e: Exception) {
                addDeviceInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}