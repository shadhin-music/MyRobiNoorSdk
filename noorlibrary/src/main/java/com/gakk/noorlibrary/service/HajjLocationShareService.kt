package com.gakk.noorlibrary.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.LocationHelper
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.hajjtracker.HajjSharingListResponse
import com.gakk.noorlibrary.ui.fragments.azan.azan_schedular.NotificaitonDissmissedReceiver
import com.gakk.noorlibrary.ui.fragments.azan.azan_schedular.NotificationClickReceiver
import com.gakk.noorlibrary.util.ACTION_SHARE
import com.gakk.noorlibrary.util.CHANNEL_ID
import com.gakk.noorlibrary.util.RepositoryProvider
import kotlinx.coroutines.*
import java.util.*

class HajjLocationShareService : Service() {

    private lateinit var repository: RestRepository
    private var apiScope: CoroutineScope? = null

    // Binder given to clients
    private val binder = HajjBinder()
    var res: List<HajjSharingListResponse.Data> = mutableListOf()
    var myListener: DataUpdate? = null
    private lateinit var locationHelper: LocationHelper

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        if (apiScope == null) {
            apiScope = CoroutineScope(SupervisorJob())
            locationHelper = LocationHelper(this)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sendNotification()
        apiScope?.launch {
            repository = RepositoryProvider.getRepository()
            launch {

                while (true) {
                    res = repository.getHajjSharingList().data
                    myListener?.updateData(res)
                    locationHelper.requestLocation()
                    val locationSave = repository.hajjLocationSave(
                        AppPreference.getUserCurrentLocation().lat.toString(),
                        AppPreference.getUserCurrentLocation().lng.toString()
                    )
                    Log.e("locationsave", "$locationSave")
                    delay(1000 * 60)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun sendNotification() {
        val now = Calendar.getInstance(TimeZone.getDefault())
        now.timeInMillis = System.currentTimeMillis()
        val body = "আপনার অবস্থান শেয়ারিং হচ্ছে!"
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val shareIntent = Intent(ACTION_SHARE)
        shareIntent.setClass(this, NotificationClickReceiver::class.java)
        val contentIntent =
            PendingIntent.getBroadcast(this, 0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon_noor)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setContentIntent(contentIntent)
            .setContentText(body)
            .setDeleteIntent(createOnDismissedIntent(this))
            .setAutoCancel(true)
           // .addAction(R.drawable.ic_alarm_off,"Stop",null)
        if (Build.VERSION.SDK_INT >= 26) {
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            //  mChannel.setSound(uri, new AudioAttributes.Builder().setUsage(5).setContentType(1).build());
            mNotificationManager.createNotificationChannel(mChannel)
        }
        val notification: Notification = mBuilder.build()
        notification.defaults = 6
        mBuilder.setContentIntent(contentIntent)
        mNotificationManager.notify(9251, notification)
        startForeground(9251, notification)

        stopSelf()
    }

    private fun createOnDismissedIntent(context: Context): PendingIntent? {
        val intent = Intent(context, NotificaitonDissmissedReceiver::class.java)
        return PendingIntent.getBroadcast(
            context.applicationContext,
            9251, intent, 0
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        apiScope?.cancel()
        apiScope = null
    }

    inner class HajjBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): HajjLocationShareService = this@HajjLocationShareService
    }
}

interface DataUpdate {
    fun updateData(list: List<HajjSharingListResponse.Data>)
}