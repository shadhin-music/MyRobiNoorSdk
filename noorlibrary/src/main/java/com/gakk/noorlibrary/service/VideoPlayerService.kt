package com.gakk.noorlibrary.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.model.khatam.Videos
import com.gakk.noorlibrary.model.khatam.toMediaItems
import com.gakk.noorlibrary.ui.activity.khatamquran.KhatamQuranVideoActivity
import com.gakk.noorlibrary.util.KHATAM_QURAN_VIDEOS_TAG
import com.gakk.noorlibrary.util.VIDEO_PLAYER_NOTIFICATION_CHANNEL_ID


private const val TAG = "VideoPlayerService"

class VideoPlayerService : Service() {

    private val binder: IBinder = VideoBinder()
    var player: ExoPlayer? = null
    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var notificationId = 239

    override fun onCreate() {
        super.onCreate()

        val context = this
        player = ExoPlayer.Builder(this)
            .build()

        player?.playWhenReady = true
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }


        playerNotificationManager =
            PlayerNotificationManager.Builder(
                this,
                notificationId,
                VIDEO_PLAYER_NOTIFICATION_CHANNEL_ID
            )

                .setMediaDescriptionAdapter(object :
                    PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): CharSequence {
                        return player.currentMediaItem?.mediaMetadata?.title!!
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        // return pending intent
                        val intent = Intent(context, KhatamQuranVideoActivity::class.java);
                        return PendingIntent.getActivity(
                            context, 0, intent,
                            flags
                        )
                    }

                    override fun getCurrentContentText(player: Player): CharSequence? {
                        return player.currentMediaItem?.mediaMetadata?.description
                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {
                        val bitmap: Bitmap = BitmapFactory.decodeResource(
                            context.getResources(),
                            R.drawable.bg_quran
                        )
                        return bitmap
                    }

                })

                .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: Notification,
                        ongoing: Boolean
                    ) {

                        Log.i(TAG, "onNotificationPosted: ")
                        startForeground(notificationId, notification)
                    }

                    override fun onNotificationCancelled(
                        notificationId: Int,
                        dismissedByUser: Boolean
                    ) {
                        Log.i(TAG, "onNotificationCancelled: ")
                        stopSelf()
                    }
                })
                .build()
        playerNotificationManager.setSmallIcon(R.drawable.ic_notification_icon_noor)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        playerNotificationManager.setPlayer(player)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        val videos = intent?.getSerializableExtra(KHATAM_QURAN_VIDEOS_TAG)
        if (videos is Videos) {
            addVideosOnPlayer(videos.videos)
        }
        return binder
    }

    private fun addVideosOnPlayer(videos: List<KhatamQuranVideosResponse.Data>) {

        player?.setMediaItems(videos.toMediaItems())
        // player?.playWhenReady = videos.playWhenReady
        player?.prepare()

        Log.i(TAG, "addVideosOnPlayer")
    }


    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.i(TAG, "onRebind: ${intent?.getStringExtra("demo")}")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind: ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.clearMediaItems()
        player?.stop()
        player?.release()
        player = null
        Log.i(TAG, "onDestroy: ")
    }

    inner class VideoBinder : Binder() {
        val player: ExoPlayer?
            get() = this@VideoPlayerService.player
    }

}
