package com.gakk.noorlibrary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.nagad.NagadSubStatusResponse
import com.gakk.noorlibrary.model.nagad.PaymentInitiateResponse
import com.gakk.noorlibrary.model.ssl.SslPaymentInitiateResponse
import com.gakk.noorlibrary.model.subs.CheckSubResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class SubscriptionViewModel(private val repository: RestRepository) : ViewModel() {

    var weeklySubInfo: MutableLiveData<Resource<CheckSubResponse>> = MutableLiveData()
    var monthlySubInfo: MutableLiveData<Resource<CheckSubResponse>> = MutableLiveData()
    var canelSubInfo: MutableLiveData<Resource<String>> = MutableLiveData()
    var weeklySubInfoRobi: MutableLiveData<Resource<String>> = MutableLiveData()
    var monthlySubInfoRobi: MutableLiveData<Resource<String>> = MutableLiveData()
    var networkInfo: MutableLiveData<Resource<String>> = MutableLiveData()
    var nagadSubInfoHalfYearly: MutableLiveData<Resource<NagadSubStatusResponse>> =
        MutableLiveData()
    var paymentSsl: MutableLiveData<Resource<SslPaymentInitiateResponse>> = MutableLiveData()


    companion object {
        val FACTORY = singleArgViewModelFactory(::SubscriptionViewModel)
    }

    fun checkSubscription(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            weeklySubInfo.postValue(Resource.loading(data = null))
            try {
                weeklySubInfo.postValue(
                    Resource.success(
                        data = repository.checkSubscription(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                weeklySubInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun checkSubscriptionMonthly(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            monthlySubInfo.postValue(Resource.loading(data = null))
            try {
                monthlySubInfo.postValue(
                    Resource.success(
                        data = repository.checkSubscription(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                monthlySubInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun cancelSubscription(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            canelSubInfo.postValue(Resource.loading(data = null))
            try {
                canelSubInfo.postValue(
                    Resource.success(
                        data = repository.cancelSubscription(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                canelSubInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    // Robi subscription part

    fun checkSubscriptionRobi(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            weeklySubInfoRobi.postValue(Resource.loading(data = null))
            try {
                weeklySubInfoRobi.postValue(
                    Resource.success(
                        data = repository.checkSubscriptionRobi(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                weeklySubInfoRobi.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun checkSubscriptionFifteenDays(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            monthlySubInfoRobi.postValue(Resource.loading(data = null))
            try {
                monthlySubInfoRobi.postValue(
                    Resource.success(
                        data = repository.checkSubscriptionRobi(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                monthlySubInfoRobi.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun cancelSubscriptionRobi(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            canelSubInfo.postValue(Resource.loading(data = null))
            try {
                canelSubInfo.postValue(
                    Resource.success(
                        data = repository.cancelSubscriptionRobi(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                canelSubInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun checkNetworkStatus() {
        viewModelScope.launch {
            networkInfo.postValue(Resource.loading(data = null))
            try {
                networkInfo.postValue(
                    Resource.success(
                        data = repository.checkNetwork()
                    )
                )

            } catch (e: Exception) {
                networkInfo.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun checkNagadSubStatusHalfYearly(msisdn: String, serviceid: String) {
        viewModelScope.launch {
            nagadSubInfoHalfYearly.postValue(Resource.loading(data = null))
            try {
                nagadSubInfoHalfYearly.postValue(
                    Resource.success(
                        data = repository.checkSubStatusNagad(msisdn, serviceid)
                    )
                )

            } catch (e: Exception) {
                nagadSubInfoHalfYearly.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    // Ssl subscription part

    fun initiatePaymentSsl(
        msisdn: String,
        serviceid: String,
        customerName: String,
        customerEmail: String
    ) {
        viewModelScope.launch {
            paymentSsl.postValue(Resource.loading(data = null))
            try {
                paymentSsl.postValue(
                    Resource.success(
                        data = repository.initiatePaymentSsl(
                            msisdn,
                            serviceid,
                            customerName,
                            customerEmail
                        )
                    )
                )

            } catch (e: Exception) {
                paymentSsl.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}