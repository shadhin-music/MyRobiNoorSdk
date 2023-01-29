package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.quranSchool.QuranSchoolResponse
import com.gakk.noorlibrary.model.quranSchool.ScholarsResponse
import com.gakk.noorlibrary.model.quranSchool.SingleScholarResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/5/2021, Mon
 */
internal class QuranSchoolViewModel(private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::QuranSchoolViewModel)
    }

    var scholarsLiveData: MutableLiveData<Resource<ScholarsResponse>> = MutableLiveData()

    var singleScholarsLiveData: MutableLiveData<Resource<SingleScholarResponse>> = MutableLiveData()

    var quranSchoolLiveData: MutableLiveData<Resource<QuranSchoolResponse>> = MutableLiveData()

    fun getScholarsById(id: String) {
        viewModelScope.launch {
            singleScholarsLiveData.postValue(Resource.loading(data = null))

            try {
                singleScholarsLiveData.postValue(Resource.success(data = repository.getScholarsById(id)))
            } catch (e: Exception) {
                singleScholarsLiveData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun getQuranSchoolByScholars(id: String){
        viewModelScope.launch {
            quranSchoolLiveData.postValue(Resource.loading(data = null))

            try {
                quranSchoolLiveData.postValue(Resource.success(data = repository.getQuranSchoolByScholars(id)))
            } catch (e: Exception) {
                quranSchoolLiveData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

}