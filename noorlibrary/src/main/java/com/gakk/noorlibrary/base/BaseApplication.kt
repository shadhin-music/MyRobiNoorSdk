package com.gakk.noorlibrary.base

//import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.gakk.noorlibrary.BuildConfig
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.roomdb.RoomRepository
import com.gakk.noorlibrary.data.roomdb.ZakatRoomDatabase
import com.gakk.noorlibrary.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class BaseApplication : Application() {

    var mainCallback: MainCallback? = null
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ZakatRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RoomRepository(database.zakatDao()) }


    companion object {
        lateinit var application: BaseApplication

        fun getApplicationInstance() = application

        //lateinit var applicationScope:CoroutineScope
        var isDefaultLoginEnabled = false
        var isFirstRunCheckEnabled = true
        private var appContext: Context? = null
        lateinit var profileImageSignature: String

        fun updateProfileImageSignature() {
            profileImageSignature = System.currentTimeMillis().toString()
        }

        fun getAppContext() = appContext!!


        /**
         * keeps track of time of last click of all clickable views(buttons,imageButton etc)
         * across the application
         *
         */
        private var mLastClickTime: Long = 0
        var LIVE_VIDEO_ID: String? = ""
        var IJTEMA_LIVE_VIDEO_ID: String? = ""

        fun getMLastClickTime() = mLastClickTime

        fun setMLastClickTime(clickTime: Long) {
            mLastClickTime = clickTime
        }

    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = VIDEO_PLAYER_NOTIFICATION_CHANNEL_NAME
            val descriptionText = VIDEO_PLAYER_NOTIFICATION_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_NONE
            val mChannel =
                NotificationChannel(VIDEO_PLAYER_NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }


    override fun onCreate() {
        super.onCreate()
        //  applicationScope= CoroutineScope(SupervisorJob())
        appContext = this
        application = this
        createNotificationChannel()
        AppPreference.init(this)
        AppPreference.language?.let { setApplicationLanguage(it) }

        updateProfileImageSignature()
        clearProgressOfUnFinishedDownloads()

    }

    private fun clearProgressOfUnFinishedDownloads() {
        val tempMap = AppPreference.downloadProgressMap
        for ((k, v) in tempMap) {
            v?.let {
                if (it >= 0 && it < 100) {
                    tempMap[k] = -1
                }
            }
        }
        AppPreference.downloadProgressMap = tempMap
    }
}

