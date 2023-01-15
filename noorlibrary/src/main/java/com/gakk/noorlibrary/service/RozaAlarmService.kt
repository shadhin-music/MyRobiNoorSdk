package com.gakk.noorlibrary.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.notification.NotificationControl
import com.gakk.noorlibrary.util.*
import java.lang.ref.WeakReference

class RozaAlarmService : Service() {

    private var mMediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG_ALARM, "RozaAlarmService called ")

        //show notification only if user is logged in
        weakReference = WeakReference(this)
        AppPreference.cachedUser?.let {

            NotificationControl.initNotificationManager(this)
            NotificationControl.createNotificationChannel(
                ROZA_NOTIFICATION_CHANNEL_NAME,
                ROZA_NOTIFICATION_CHANNEL_DESC, ROZA_NOTIFICATION_CHANNEL_ID
            )

            val actionType = intent?.getStringExtra(ACTION_TYPE)
            var message = ""

            when (actionType == SHOW_SEHRI_NOTIFICATION) {
                true -> {
                    message = resources.getString(R.string.sehri_time_up)
                    if (AppPreference.sehriOrifterAlertSoundOn) {
                        playAdanFromRawFolder(AZAN_FAJR_URL)
                    }

                    val builder = NotificationControl.getRozaNotification(
                        this, ROZA_NOTIFICATION_CHANNEL_ID,
                        message,
                        AppPreference.sehriOrifterAlertVibrationOn
                    )

                    startForeground(ROZA_NOTIFICATION_ID, builder!!.build())

                }
                else -> {
                    message = resources.getString(R.string.time_for_iftari)
                    if (AppPreference.sehriOrifterAlertSoundOn) {
                        playAdanFromRawFolder(AZAN_COMMON_URL)
                    }

                    val builder = NotificationControl.getRozaNotification(
                        this, ROZA_NOTIFICATION_CHANNEL_ID,
                        message,
                        AppPreference.sehriOrifterAlertVibrationOn
                    )
                    startForeground(ROZA_NOTIFICATION_ID, builder!!.build())

                }

            }


        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.e("RECEIVER", "Service destroyed")
        releaseMediaPlayer()
        AppPreference.lastRozaAlarmDisMissTimeMs = System.currentTimeMillis()
        super.onDestroy()
    }

    companion object {
        private var weakReference: WeakReference<RozaAlarmService>? = null
    }

    private fun playAdanFromRawFolder(url: String) {
        releaseMediaPlayer()
        mMediaPlayer?.setDataSource(url)
        mMediaPlayer?.prepare()
        mMediaPlayer?.start()
    }

    private fun releaseMediaPlayer() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }
}