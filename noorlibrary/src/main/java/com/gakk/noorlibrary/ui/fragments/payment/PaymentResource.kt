package com.mcc.noor.ui.fragments.payment

import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.model.CommonApiResponse
import com.mcc.noor.model.umrah_hajj.UmrahHajjRegResponse

sealed interface PaymentResource
{
    data class hajj_pre_reg(val data: Resource<CommonApiResponse>):PaymentResource
    data class umrah_hajj_reg(val data: Resource<UmrahHajjRegResponse>):PaymentResource
    data class Error(val errorMsg:String): PaymentResource
    object Loading: PaymentResource
}