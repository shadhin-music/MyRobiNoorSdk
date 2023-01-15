package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.roomdb.RoomRepository
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class ZakatViewModel(private val repository: RoomRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::ZakatViewModel)
    }

    val allData: LiveData<List<ZakatDataModel>> = repository.allDataZakat.asLiveData()
    fun insert(dataModel: ZakatDataModel) {
        viewModelScope.launch {
            repository.insert(dataModel)
        }
    }

    fun delete(dataModel: ZakatDataModel) {
        viewModelScope.launch {
            repository.delete(dataModel)
        }
    }
}
