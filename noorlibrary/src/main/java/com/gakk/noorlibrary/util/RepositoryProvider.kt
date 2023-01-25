package com.gakk.noorlibrary.util

import androidx.annotation.Keep
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.rest.api.RetrofitBuilder
import kotlinx.coroutines.runBlocking

@Keep
class RepositoryProvider {
    companion object {
        suspend fun getRepository(): RestRepository {
            return RestRepository(
                contentApiService = RetrofitBuilder.getApiService(BASE_URL_SECURITY_CONTENT, true),
                nearbyApiService = RetrofitBuilder.getApiService(BASE_URL_NEARBY, false),
                subApiService = RetrofitBuilder.getApiService(BASE_URL_SUBSCRIPTION, false),
                subApiServiceRobi = RetrofitBuilder.getApiService(
                    BASE_URL_SUBSCRIPTION_ROBI,
                    false
                ),
                dataApiService = RetrofitBuilder.getScalarsApiService(
                    BASE_URL_CHECK_CONNECTION,
                    false
                ),
                subApiServiceNagad = RetrofitBuilder.getApiService(
                    BASE_URL_SUBSCRIPTION_NAGAD,
                    false
                ),
                subApiServiceSsl = RetrofitBuilder.getApiService(
                    BASE_URL_SUBSCRIPTION_SSL,
                    false
                ),
                authService = RetrofitBuilder.getApiService(
                    BASE_URL_SECURITY_CONTENT,
                    false
                )
            )
        }
    }
}