package com.gakk.noorlibrary.ui.fragments.subscription

import com.gakk.noorlibrary.data.rest.Resource

sealed interface SubsResource {
    data class SubscriptionRobi(val data: Resource<String>, val subscriptionId: String) :
        SubsResource

    object Loading : SubsResource
    data class Error(val msg: String) : SubsResource
}