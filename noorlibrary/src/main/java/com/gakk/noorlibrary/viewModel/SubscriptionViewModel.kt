package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.nagad.NagadSubStatusResponse
import com.gakk.noorlibrary.model.nagad.PaymentInitiateResponse
import com.gakk.noorlibrary.model.ssl.SslPaymentInitiateResponse
import com.gakk.noorlibrary.model.subs.CheckSubResponse
import com.gakk.noorlibrary.ui.fragments.subscription.SubsResource
import com.gakk.noorlibrary.util.DONATION_CHANNEL
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class SubscriptionViewModel(private val repository: RestRepository) : ViewModel() {

    var canelSubInfo: MutableLiveData<Resource<String>> = MutableLiveData()
    var dailySubInfoRobi: MutableLiveData<Resource<String>> = MutableLiveData()
    var fifteenSubInfoRobi: MutableLiveData<Resource<String>> = MutableLiveData()
    var networkInfo: MutableLiveData<Resource<String>> = MutableLiveData()
    var paymentSsl: MutableLiveData<Resource<SslPaymentInitiateResponse>> = MutableLiveData()

    private val _subscription_robi : MutableLiveData<SubsResource> = MutableLiveData()


    companion object {
        val FACTORY = singleArgViewModelFactory(::SubscriptionViewModel)
    }


    // Robi subscription part

    fun subscriptionCheckRobi(msisdn: String, subscriptionId: String)
    {

        viewModelScope.launch {
            Log.e("sub model",subscriptionId)
            _subscription_robi.value = SubsResource.Loading

            try {

                _subscription_robi.value = SubsResource.SubscriptionRobi(
                    Resource.success(
                        data = repository.checkSubscriptionRobi(msisdn, subscriptionId)
                    ),
                    subscriptionId)

            } catch (e: Exception) {

                _subscription_robi.value = SubsResource.Error(e.message ?: "Error Occurred!")
            }

        }


    }
    fun checkSubscriptionRobi(msisdn: String, subscriptionId: String) {
        viewModelScope.launch {
            dailySubInfoRobi.postValue(Resource.loading(data = null))
            try {
                dailySubInfoRobi.postValue(
                    Resource.success(
                        data = repository.checkSubscriptionRobi(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                dailySubInfoRobi.postValue(
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
            fifteenSubInfoRobi.postValue(Resource.loading(data = null))
            try {
                fifteenSubInfoRobi.postValue(
                    Resource.success(
                        data = repository.checkSubscriptionRobi(msisdn, subscriptionId)
                    )
                )

            } catch (e: Exception) {
                fifteenSubInfoRobi.postValue(
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

    fun initiatePaymentSslRange(
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
                        data = repository.initiatePaymentSslRange(
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


    fun initiatePaymentSslRangeDonation(
        msisdn: String,
        serviceid: String,
        customerName: String,
        customerEmail: String,
        amount:String
    ) {
        viewModelScope.launch {
            paymentSsl.postValue(Resource.loading(data = null))
            try {
                paymentSsl.postValue(
                    Resource.success(
                        data = repository.initiatePaymentSslRange(
                            msisdn,
                            serviceid,
                            customerName,
                            customerEmail,
                            DONATION_CHANNEL,
                            amount
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