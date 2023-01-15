package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.tracker.AllPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.PostPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.SalahStatus
import com.gakk.noorlibrary.model.tracker.ramadan.AllRamadanDataResponse
import com.gakk.noorlibrary.model.tracker.ramadan.add.PostRamadanDataResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class TrackerViewModel(private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::TrackerViewModel)
    }

    var prayerListData: MutableLiveData<Resource<AllPrayerDataResponse>> = MutableLiveData()
    var addPrayerData: MutableLiveData<Resource<PostPrayerDataResponse>> = MutableLiveData()
    var updatePrayerData: MutableLiveData<Resource<PostPrayerDataResponse>> = MutableLiveData()
    var ramadanListData: MutableLiveData<Resource<AllRamadanDataResponse>> = MutableLiveData()
    var addRamadanData: MutableLiveData<Resource<PostRamadanDataResponse>> = MutableLiveData()
    var updateRamadanData: MutableLiveData<Resource<PostRamadanDataResponse>> = MutableLiveData()


    fun loadAllPrayerData(fromMonth: String, toMonth: String) {
        viewModelScope.launch {
            prayerListData.postValue(Resource.loading(data = null))

            try {
                prayerListData.postValue(
                    Resource.success(
                        repository.getAllPrayerData(fromMonth, toMonth)
                    )
                )
            } catch (e: Exception) {
                Log.e("ERR", e.message!!)
                prayerListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun postPrayerData(
        createdOn: String,
        language: String,
        salahStatus: SalahStatus
    ) {
        addPrayerData.postValue(Resource.loading(data = null))
        viewModelScope.launch {
            try {
                addPrayerData.postValue(
                    Resource.success(
                        data = repository.addPrayerData(
                            createdOn, language,
                            salahStatus
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("EXCWWD", e.localizedMessage)
                addPrayerData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun updatePrayerData(
        id: String,
        createdBy: String,
        createdOn: String,
        language: String,
        salahStatus: SalahStatus
    ) {
        updatePrayerData.postValue(Resource.loading(data = null))
        viewModelScope.launch {
            try {
                updatePrayerData.postValue(
                    Resource.success(
                        data = repository.updatePrayerData(
                            id,
                            createdBy,
                            createdOn, language,
                            salahStatus
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("EXCWWD", e.localizedMessage)
                updatePrayerData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadAllRamadanData(fromMonth: String, toMonth: String) {
        viewModelScope.launch {
            ramadanListData.postValue(Resource.loading(data = null))

            try {
                ramadanListData.postValue(
                    Resource.success(
                        repository.getAllRamadanData(fromMonth, toMonth)
                    )
                )
            } catch (e: Exception) {
                Log.e("ERR", e.message!!)
                ramadanListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun postRamadanData(
        createdOn: String,
        language: String,
        ramadanStatus: Boolean
    ) {
        addRamadanData.postValue(Resource.loading(data = null))
        viewModelScope.launch {
            try {
                addRamadanData.postValue(
                    Resource.success(
                        data = repository.addRamadanData(
                            createdOn, language,
                            ramadanStatus
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("EXCWWD", e.localizedMessage)
                addRamadanData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun updateRamadanData(
        id: String,
        createdBy: String,
        createdOn: String,
        language: String,
        ramadanStatus: Boolean
    ) {
        updateRamadanData.postValue(Resource.loading(data = null))
        viewModelScope.launch {
            try {
                updateRamadanData.postValue(
                    Resource.success(
                        data = repository.updateRamadanData(
                            id,
                            createdBy,
                            createdOn, language,
                            ramadanStatus
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("EXCWWD", e.localizedMessage)
                updateRamadanData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}