package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.UserLocation
import com.gakk.noorlibrary.model.nearby.NearbyResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class NearbyViewModel(private val repository: RestRepository) : ViewModel() {
    var nearbyInfo: MutableLiveData<Resource<NearbyResponse>> = MutableLiveData()

    companion object {
        val FACTORY = singleArgViewModelFactory(::NearbyViewModel)
    }

    fun loadNearbyPlaceInfo(
        key: String,
        radius: String,
        location: UserLocation,
        placeType: String,
        language: String
    ) {
        viewModelScope.launch {
            nearbyInfo.postValue(Resource.loading(data = null))
            try {
                nearbyInfo.postValue(
                    Resource.success(
                        data = repository.getNearbyPlaceOfGivenType(
                            key,
                            radius,
                            location,
                            placeType,
                            language
                        )
                    )
                )

            } catch (e: Exception) {
                nearbyInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}