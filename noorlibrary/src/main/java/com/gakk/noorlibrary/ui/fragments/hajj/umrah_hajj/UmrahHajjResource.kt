package com.gakk.noorlibrary.ui.fragments.hajj.umrah_hajj

import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.model.umrah_hajj.CheckUmrahReg
import com.gakk.noorlibrary.model.umrah_hajj.UmrahHajjModel
import com.gakk.noorlibrary.model.umrah_hajj.UmrahHajjRegResponse

sealed interface UmrahHajjResource
{

    object Loading:UmrahHajjResource
    data class UmrahHajjPackListresponse(val data: Resource<UmrahHajjModel>):UmrahHajjResource
    data class Error(val errorMsg:String):UmrahHajjResource
    data class UmrahHajjPersonalPostResponse(val data: Resource<UmrahHajjRegResponse>):UmrahHajjResource
    data class CheckUmrahPersonalInfo(val data: Resource<CheckUmrahReg>):UmrahHajjResource
    data class GetAllPaymentHistory(val data: Resource<CheckUmrahReg>):UmrahHajjResource

}