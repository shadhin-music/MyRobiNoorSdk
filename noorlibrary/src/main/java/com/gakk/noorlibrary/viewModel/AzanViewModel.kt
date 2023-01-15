package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/3/2021, Mon
 */
class AzanViewModel(private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::AzanViewModel)
    }

    var timerLiveData: MutableLiveData<Boolean> =
        MutableLiveData()


    var job: Job? = null

    fun startTimer() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                timerLiveData.postValue(true)
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}