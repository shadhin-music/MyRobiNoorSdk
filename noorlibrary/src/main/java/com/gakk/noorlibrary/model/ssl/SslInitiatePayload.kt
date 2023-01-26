package com.gakk.noorlibrary.model.ssl

import androidx.annotation.Keep

@Keep
data class SslInitiatePayload(
    val MSISDN: String,
    val serviceid: String,
    val puser: String,
    val cus_name: String,
    val cus_email: String,
    val channel: String,
    val amount: String? = ""
)
