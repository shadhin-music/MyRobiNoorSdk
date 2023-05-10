package com.mcc.noor

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class DemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}