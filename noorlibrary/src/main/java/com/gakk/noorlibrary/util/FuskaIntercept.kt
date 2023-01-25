package com.gakk.noorlibrary.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer

class FuskaIntercept : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.toString()
        val newRequest = chain.request().newBuilder().url(url.decodeFuskaUrl()).build()
        val response = chain.proceed(newRequest)
        val responseBody = response.body
        val encryptString = responseBody?.string()
        val decryptStr = encryptString?.let { decryptData(it) }

        val buffer = Buffer()
        buffer.write((decryptStr?:"").toByteArray())
        val newBody = buffer.asResponseBody(responseBody?.contentType(),responseBody?.contentLength()?:0L)

        return response.newBuilder()
            .body(newBody)
            .build()
    }
}

