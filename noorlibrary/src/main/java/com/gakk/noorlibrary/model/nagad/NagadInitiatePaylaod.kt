package com.gakk.noorlibrary.model.nagad

import androidx.annotation.Keep

@Keep
data class NagadInitiatePaylaod(val MSISDN: String, val serviceid: String, val puser: String)
