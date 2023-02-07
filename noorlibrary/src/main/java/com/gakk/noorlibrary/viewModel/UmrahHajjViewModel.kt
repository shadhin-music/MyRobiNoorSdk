package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import com.gakk.noorlibrary.model.umrah_hajj.UmrahHajjPersonalPostModel
import com.gakk.noorlibrary.ui.fragments.hajj.umrah_hajj.UmrahHajjResource
import kotlinx.coroutines.launch

class UmrahHajjViewModel(private val repository: RestRepository) : ViewModel() {

    companion object
    {
        val FACTORY = singleArgViewModelFactory(::UmrahHajjViewModel)
    }

    private val _umrah_hajj_package_list : MutableLiveData<UmrahHajjResource> = MutableLiveData()
    val umrah_hajj_package_list: LiveData<UmrahHajjResource> get() = _umrah_hajj_package_list

    private val _umrah_hajj_personal_info : MutableLiveData<UmrahHajjResource> = MutableLiveData()
    val umrah_hajj_personal_info: LiveData<UmrahHajjResource> get() = _umrah_hajj_personal_info


    private val _umrah_hajj_payment_history : MutableLiveData<UmrahHajjResource> = MutableLiveData()
    val umrah_hajj_payment_history: LiveData<UmrahHajjResource> get() = _umrah_hajj_payment_history


    fun getUmrahHajjPackageList()
    {
        viewModelScope.launch {

            _umrah_hajj_package_list.postValue(UmrahHajjResource.Loading)
            try {
                _umrah_hajj_package_list.postValue(
                    UmrahHajjResource.UmrahHajjPackListresponse(
                        Resource.success(
                            data = repository.getUmrahHajjPackage()
                        )

                    )

                )
            } catch (e: Exception) {
                _umrah_hajj_package_list.postValue(
                    UmrahHajjResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }
        }
    }

    fun postUmrahPersonalInfo(data: UmrahHajjPersonalPostModel)
    {
        viewModelScope.launch {

            _umrah_hajj_personal_info.postValue(UmrahHajjResource.Loading)
            try {
                _umrah_hajj_personal_info.postValue(
                    UmrahHajjResource.UmrahHajjPersonalPostResponse(
                        Resource.success(
                            data = repository.postUmrahHajjPersoanlInfo(data)
                        )

                    )

                )
            } catch (e: Exception) {
                _umrah_hajj_personal_info.postValue(
                    UmrahHajjResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }

        }
    }

    fun checkUmrahPersonalInfo(passport:String)
    {
        viewModelScope.launch {

            _umrah_hajj_personal_info.postValue(UmrahHajjResource.Loading)
            try {

                _umrah_hajj_personal_info.postValue(
                    UmrahHajjResource.CheckUmrahPersonalInfo(
                        Resource.success(
                            data = repository.checkUmrahPersonalInfo(passport)
                        )
                    )
                )

            } catch (e: Exception) {
                _umrah_hajj_personal_info.postValue(
                    UmrahHajjResource.Error(
                        e.message ?: "Error Occurred!"
                    )
                )
            }

        }
    }

    fun getAllPaymentHistory(Msisdn:String)
    {

        viewModelScope.launch {

            _umrah_hajj_payment_history.postValue(UmrahHajjResource.Loading)
            try {

                _umrah_hajj_payment_history.postValue(
                    UmrahHajjResource.GetAllPaymentHistory(
                        Resource.success(
                            data = repository.GetAllPaymentHistoryUmrah(Msisdn)
                        )
                    )
                )


            } catch (e: Exception) {
                _umrah_hajj_payment_history.postValue(
                    UmrahHajjResource.Error(
                        e.message ?: "Error Occurred!"
                    )

                )
            }

        }
    }

}