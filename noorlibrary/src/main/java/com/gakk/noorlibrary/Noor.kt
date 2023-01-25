package com.gakk.noorlibrary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.Keep
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.ui.activity.MainActivity
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.VIDEO_PLAYER_NOTIFICATION_CHANNEL_DESC
import com.gakk.noorlibrary.util.VIDEO_PLAYER_NOTIFICATION_CHANNEL_ID
import com.gakk.noorlibrary.util.VIDEO_PLAYER_NOTIFICATION_CHANNEL_NAME
import kotlinx.coroutines.*

@Keep
object Noor {

    private var scope = CoroutineScope(Dispatchers.IO)

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    fun openNoor(context: Context, msisdn: String) {

        this.appContext = context.applicationContext
        AppPreference.init(appContext!!)
        createNotificationChannel()
        scope.launch {
            val token = RepositoryProvider.getRepository().login(msisdn)
            withContext(Dispatchers.Main) {
                if (token != null) {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Authentication Error", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    @JvmStatic
    fun destroySDK() {
        scope.cancel()
        appContext = null
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
