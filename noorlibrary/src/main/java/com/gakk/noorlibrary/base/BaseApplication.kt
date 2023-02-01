package com.gakk.noorlibrary.base

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class BaseApplication : Application() {


    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
   // val database by lazy { ZakatRoomDatabase.getDatabase(this) }
    //val repository by lazy { RoomRepository(database.zakatDao()) }


    companion object {
        lateinit var application: BaseApplication

        fun getApplicationInstance() = application

        private var appContext: Context? = null


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


    override fun onCreate() {
        super.onCreate()
        appContext = this
        application = this

    }
}

