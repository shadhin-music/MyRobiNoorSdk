package com.gakk.noorlibrary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.Keep
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.activity.MainActivity
import com.gakk.noorlibrary.util.*
import kotlinx.coroutines.*

@Keep
object Noor {

    private var scope = CoroutineScope(Dispatchers.IO)
    private var  CallBackListener : NoorAuthCallBack? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var token: String? = null

    @JvmStatic
    fun authNoor(context: Context, msisdn: String,callback:NoorAuthCallBack?) {

        this.appContext = context.applicationContext
        this.CallBackListener = callback
        AppPreference.init(appContext!!)
        createNotificationChannel()
        scope.launch {
             token = RepositoryProvider.getRepository().login(msisdn)
            withContext(Dispatchers.Main) {
                if (token != null) {
                    //val intent = Intent(context, MainActivity::class.java)
                    //context.startActivity(intent)
                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }
    }

    @JvmStatic
    fun destroySDK() {
        scope.cancel()
        appContext = null
    }

    @JvmStatic
    fun openHome(context: Context)
    {

        if (token != null) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        } else {
            CallBackListener?.onAuthFailed()
        }

    }

    @JvmStatic
    fun openQuran(context: Context)
    {

        if (token != null) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(PAGE_NAME, PAGE_QURAN_HOME)
            intent.putExtra(SDK_PAGE_CALL, true)
            context.startActivity(intent)
        } else {
            CallBackListener?.onAuthFailed()
        }

    }

    @JvmStatic
    fun openRamadan(context: Context)
    {

        if (token != null) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(DESTINATION_FRAGMENT, PAGE_ROZA)
            intent.putExtra(SDK_PAGE_CALL, true)
            context.startActivity(intent)
        } else {
            CallBackListener?.onAuthFailed()
        }

    }


    @JvmStatic
    fun openPodcast(context: Context)
    {

        if (token != null) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(PAGE_NAME, PAGE_ISLAMIC_PODCAST)
            intent.putExtra(SDK_PAGE_CALL, true)
            context.startActivity(intent)
        } else {
            CallBackListener?.onAuthFailed()
        }

    }

    @JvmStatic
    fun openHajjPreRegistration(context: Context)
    {

        if (token != null) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(PAGE_NAME, PAGE_HAJJ_PRE_HOME)
            intent.putExtra(SDK_PAGE_CALL, true)
            context.startActivity(intent)
        } else {
            CallBackListener?.onAuthFailed()
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
            val notificationManager =
                appContext?.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }


}

interface NoorAuthCallBack
{
    fun onAuthSuccess()
    fun onAuthFailed()
}


