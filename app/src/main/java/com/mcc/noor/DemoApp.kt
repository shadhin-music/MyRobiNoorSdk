package com.mcc.noor

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noor.BuildConfig

class DemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}