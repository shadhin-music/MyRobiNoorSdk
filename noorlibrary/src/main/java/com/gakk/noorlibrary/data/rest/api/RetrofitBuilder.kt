package com.gakk.noorlibrary.data.rest.api

import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.youtube.YoutubeApiService
import com.gakk.noorlibrary.util.FuskaIntercept
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

const val CONNECTION_TIME_OUT = 20L

class RetrofitBuilder {


    companion object {
        private val mNoAuthClient: OkHttpClient
            get() {

                val httpBuilder = OkHttpClient.Builder()
                httpBuilder
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)

                //.addInterceptor(OAuthInterceptor("Bearer", "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIwNjE5ZWEzNi04YmUyLTQwNDItOGRmNi0xYzAyZjMyZTUwYzQiLCJSb2xlIjoiR2VuZXJhbCBVc2VyIiwibmJmIjoxNTgzODMwMzA3LCJleHAiOjE1ODQ0MzUxMDcsImlhdCI6MTU4MzgzMDMwNywiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QiLCJhdWQiOiJodHRwczovL2xvY2FsaG9zdCJ9.sNa3O3d51LY2QeOEZ3bZJdu-w7G2Ul6ZlzxEK8rqt5UAjlvBLQoGBVrXjLegi4cNGDR6yhJJAvr3FIeRYt38qQ"))
                return httpBuilder.build()

            }

        private val mAuthClient: OkHttpClient?
            get() {
                val httpBuilder = OkHttpClient.Builder()
                httpBuilder
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(FuskaIntercept())
                  // .addInterceptor(OAuthInterceptor("Bearer", "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiI1ZmZiZjM4NDgxNzI4ZThjNzdjZGRkNGQiLCJVc2VySWQiOiI1ZmZiZjM4NDgxNzI4ZThjNzdjZGRkNGQiLCJVc2VyTmFtZSI6Ijg4MDE2NzIyMTk5OTIiLCJJbWFnZSI6InByb2ZpbGUvODg4Nl84ODAxNjcyMjE5OTkyLmpwZWciLCJNc2lzZG4iOiI4ODAxNjcyMjE5OTkyIiwicm9sZSI6WyJhZG1pbiIsInVzZXIiXSwibmJmIjoxNjc0NTYxMzk2LCJleHAiOjE5ODk5MjEzOTYsImlhdCI6MTY3NDU2MTM5NiwiaXNzIjoibG9sbGlwb3AiLCJhdWQiOiJsb2xsaXBvcCJ9.QjiY2fLXAhVjK9WlVFc6WStK1fpGyF5I4Q2Jn-9V068AKcPF3LcGryBNpfuD03CLKrh268GA5aHPO0yL_eUh2A"))
                    .addInterceptor(
                        OAuthInterceptor(
                            "Bearer",
                            AppPreference.userToken?: ""
                        )

                    )
                return httpBuilder.build()

            }

        private fun getClient(hasAuthorization: Boolean): OkHttpClient? {
            return when (hasAuthorization) {
                true -> mAuthClient
                false -> mNoAuthClient
            }
        }

        private fun getRetrofitInstance(url: String, hasAuthorization: Boolean): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .client(getClient(hasAuthorization))
                .build()
        }

        private fun getScalarsRetroInstance(url: String, hasAuthorization: Boolean): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getClient(hasAuthorization))
                .build()
        }

        suspend fun getApiService(url: String, hasAuthorization: Boolean): ApiService {
            return withContext(Dispatchers.IO) {
                getRetrofitInstance(url, hasAuthorization).create(ApiService::class.java)
            }

        }

        suspend fun getScalarsApiService(url: String, hasAuthorization: Boolean): ApiService {
            return withContext(Dispatchers.IO) {
                getScalarsRetroInstance(url, hasAuthorization).create(ApiService::class.java)
            }
        }
    }

    suspend fun getYoutubeService(url: String): YoutubeApiService {
        return withContext(Dispatchers.IO){
            Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mNoAuthClient)
                .build().create(YoutubeApiService::class.java)
        }

    }

}

class OAuthInterceptor(private val tokenType: String, private val acceessToken: String?) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", "$tokenType $acceessToken").build()
        return chain.proceed(request)
    }
}