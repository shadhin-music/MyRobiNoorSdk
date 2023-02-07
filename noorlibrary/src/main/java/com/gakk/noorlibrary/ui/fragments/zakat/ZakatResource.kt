package com.gakk.noorlibrary.ui.fragments.zakat

import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.model.zakat.SaveZakatResponse
import com.gakk.noorlibrary.model.zakat.ZakatDelResponse
import com.gakk.noorlibrary.model.zakat.ZakatListResponse

sealed interface ZakatResource
{
    object Loading: ZakatResource
    data class zakatSave(val data: Resource<SaveZakatResponse>?): ZakatResource
    data class zakatList(val data: Resource<ZakatListResponse>): ZakatResource
    data class zakatDelete(val data: Resource<ZakatDelResponse>): ZakatResource
    data class Error(val errorMsg:String): ZakatResource
}