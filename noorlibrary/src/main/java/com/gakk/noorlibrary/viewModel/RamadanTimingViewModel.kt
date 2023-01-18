package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class RamadanTimingViewModel (private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::RamadanTimingViewModel)
    }
        var ramadanListData: MutableLiveData<Resource<IftarAndSheriTimeforBD>> = MutableLiveData()

    fun loadRamadanTimingData(name: String) {
        viewModelScope.launch {
            ramadanListData.postValue(Resource.loading(data = null))

            try {
                ramadanListData.postValue(
                    Resource.success(
                        repository.getRamadanTimingData(name)
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
}