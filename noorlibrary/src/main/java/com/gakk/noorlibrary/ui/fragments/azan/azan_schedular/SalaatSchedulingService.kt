package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.util.*
import java.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
class SalaatSchedulingService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayer_name = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: ""

        Log.d(TAG_ALARM, "salat scheduling service called ${prayer_name}")

        playAzan(prayer_name)

        try {
            sendNotification(prayer_name)
        } catch (e: Exception) {
            Log.d(TAG_ALARM, "salat scheduling service exception")
        }

        return START_STICKY
    }


    private fun playAzan(prayerName: String) {
        if (prayerName.equals(getString(R.string.fajr), false)) {
            AzanPlayer.playAdanFromRawFolder(
                AZAN_FAJR_URL
            )
        } else {
            AzanPlayer.playAdanFromRawFolder(
                AZAN_COMMON_URL
            )
        }
    }

    override fun onDestroy() {
        Log.i("RECEIVER", "Service destroyed")
        AzanPlayer.releaseMediaPlayer()
        super.onDestroy()
    }


    private fun sendNotification(prayer_name: String) {
        val now = Calendar.getInstance(TimeZone.getDefault())
        now.timeInMillis = System.currentTimeMillis()
        val body = "$prayer_name Prayer time !"
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val shareIntent = Intent(ACTION_SHARE)
        shareIntent.setClass(this, NotificationClickReceiver::class.java)
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        val contentIntent =
            PendingIntent.getBroadcast(this, 0, shareIntent, flags)
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
        if (Build.VERSION.SDK_INT >= 26) {
            StrictMode.setVmPolicy(VmPolicy.Builder().build())
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
        mNotificationManager.notify(NOTIFICATION_ID, notification)
        //startForeground(NOTIFICATION_ID, notification);

    }

    private fun createOnDismissedIntent(context: Context): PendingIntent? {
        val intent = Intent(context, NotificaitonDissmissedReceiver::class.java)
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        return PendingIntent.getBroadcast(
            context.applicationContext,
            NOTIFICATION_ID, intent, flags
        )
    }

}
