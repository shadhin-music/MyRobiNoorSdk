package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.*
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.zakat.ZakatListResponse
import com.gakk.noorlibrary.model.zakat.ZakatModel
import com.gakk.noorlibrary.ui.fragments.zakat.ZakatResource
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class ZakatViewModel(private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::ZakatViewModel)
    }

    private var _zakat_calculator : MutableLiveData<ZakatResource>? = MutableLiveData()
    val zakat_calculator: MutableLiveData<ZakatResource>? get() = _zakat_calculator

    private val _zakat_list : MutableLiveData<ZakatResource> = MutableLiveData()
    val zakat_list: LiveData<ZakatResource> get() = _zakat_list

    private val _zakatCallback : MutableLiveData<Int> = MutableLiveData()
    val zakatCallback: LiveData<Int> get() = _zakatCallback

    fun callback(value:Int)
    {
        _zakatCallback.postValue(value)
    }


    fun saveZakatData(payload: ZakatModel)
    {
        viewModelScope.launch {

            _zakat_calculator?.postValue(ZakatResource.Loading)
            try {
                _zakat_calculator?.postValue(
                    ZakatResource.zakatSave(
                        Resource.success(
                            data = repository.saveZakatData(payload)
                        )

                    )

                )
            } catch (e: Exception) {
                _zakat_calculator?.postValue(
                    ZakatResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }
        }
    }


    fun getZakatList()
    {
        viewModelScope.launch {

            _zakat_list.postValue(ZakatResource.Loading)
            try {
                _zakat_list.postValue(
                    ZakatResource.zakatList(
                        Resource.success(
                            data = repository.getZakatList()
                        )

                    )

                )
            } catch (e: Exception) {
                _zakat_list.postValue(
                    ZakatResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }
        }
    }

    fun delZakat(id:String)
    {
        viewModelScope.launch {

            _zakat_list.postValue(ZakatResource.Loading)
            try {
                _zakat_list.postValue(
                    ZakatResource.zakatDelete(
                        Resource.success(
                            data = repository.deleteZakat(id)
                        )

                    )

                )
            } catch (e: Exception) {
                _zakat_list.postValue(
                    ZakatResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }
        }
    }

    fun clearLiveData()
    {
        _zakatCallback.postValue(0)
        _zakat_calculator = null
    }

}
