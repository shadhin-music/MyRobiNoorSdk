package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.names.NamesOfAllahApiResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch
import java.lang.Exception

internal class NinetyNineNamesOfAllahViewModel(private val repository: RestRepository): ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::NinetyNineNamesOfAllahViewModel)
    }

    var nineNamesOfAllahData: MutableLiveData<Resource<NamesOfAllahApiResponse>> = MutableLiveData()

    fun loadNamesOfAllah(){
        viewModelScope.launch {
            nineNamesOfAllahData.postValue(Resource.loading(data = null))
            try {
                nineNamesOfAllahData.postValue(
                    Resource.success(
                        repository.getNinetyNineNamesOfAllah()
                    )
                )

            } catch (e: Exception) {
                Log.i("ERR", "Error NinetyNineNamesOfAllah")
                nineNamesOfAllahData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}