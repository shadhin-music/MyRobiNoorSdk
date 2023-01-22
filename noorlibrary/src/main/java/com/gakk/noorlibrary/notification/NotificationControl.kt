package com.gakk.noorlibrary.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.receiver.AudioPlayerActionReceiver
import com.gakk.noorlibrary.service.AudioPlayerService.Companion.mediaSession
import com.gakk.noorlibrary.ui.activity.MainActivity
import com.gakk.noorlibrary.util.*

object NotificationControl {

    private lateinit var notificationManager: NotificationManager


    fun initNotificationManager(context: Context) {
        if (!this::notificationManager.isInitialized) {
            notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    fun getNotificationManager() = notificationManager


    fun createNotificationChannel(channelName: String, desc: String, channelID: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName//ROZA_NOTIFICATION_CHANNEL_NAME
            val descriptionText = desc//ROZA_NOTIFICATION_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                channelID/*ROZA_NOTIFICATION_CHANNEL_ID*/,
                name,
                importance
            ).apply {
                description = descriptionText

            }
            channel.setSound(null, null)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getAudioPlayerNotification(
        context: Context,
        contentType: String,
        contentId: String,
        contentTitle: String,
        contentText: String,
        duration: Long,
        artworkBitmap: Bitmap
    ): NotificationCompat.Builder? {

        val metaData =
            getMetaDataForMediaSession(contentTitle, contentText, duration, artworkBitmap)
        mediaSession.setMetadata(metaData)
        val state = com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()
        Log.i("PLAYBACK_STATE", state.toString())
        mediaSession.setPlaybackState(getPlayBackState(state))

        var title =
            mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        var text =
            mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        var thumb =
            mediaSession.controller.metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

        var list = com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getSurahList()
        Log.i("NOTIF___", "${list!!.size}")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DESTINATION_FRAGMENT, PAGE_SURAH_DETAILS)
        }


        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, OPEN_APP_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession?.sessionToken)
                // .setCancelButtonIntent(getAudioPlayerNotificationDeleteIntent(context))
            )
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(thumb)
            .setSmallIcon(R.drawable.ic_notification_icon_noor)
            .setDeleteIntent(getAudioPlayerNotificationDeleteIntent(context))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            // .setAutoCancel(true)
            .setColorized(false)
            .setDeleteIntent(getAudioPlayerNotificationDeleteIntent(context))


        when (com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
            true -> {
                builder
                    .addAction(
                        R.drawable.ic_prev_enabled,
                        "",
                        getAudioPlayerNotificationPrevIntent(context)
                    )
                    .addAction(
                        R.drawable.ic_pause_filled_enabled,
                        "",
                        getAudioPlayerNotificationPauseIntent(context)
                    )
                    .addAction(
                        R.drawable.ic_next_enabled,
                        "",
                        getAudioPlayerNotificationNextIntent(context)
                    )
                    .setOngoing(true)

            }
            false -> {
                builder
                    .addAction(
                        R.drawable.ic_prev_enabled,
                        "",
                        getAudioPlayerNotificationPrevIntent(context)
                    )
                    .addAction(
                        R.drawable.ic_play_filled_enabled,
                        "",
                        getAudioPlayerNotificationResumeIntent(context)
                    )
                    .addAction(
                        R.drawable.ic_next_enabled,
                        "",
                        getAudioPlayerNotificationNextIntent(context)
                    )
                    .setOngoing(false)

            }
        }



        builder.setNotificationSilent()



        return builder
    }

    fun getMetaDataForMediaSession(
        title: String,
        text: String,
        duration: Long,
        bitmap: Bitmap
    ): MediaMetadataCompat {
        var metadata = MediaMetadataCompat.Builder()
            .putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                title
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                text
            )

            .putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                duration
            )
            .putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                bitmap
            )

        return metadata.build()

    }

    fun getPlayBackState(isPlaying: Boolean): PlaybackStateCompat {


        return when (isPlaying) {
            true -> PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    com.gakk.noorlibrary.audioPlayer.AudioManager.getAudioPlayer()?.currentPosition ?: 0,
                    1.0f,
                    SystemClock.elapsedRealtime()
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            else -> PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    com.gakk.noorlibrary.audioPlayer.AudioManager.getAudioPlayer()?.currentPosition ?: 0,
                    1.0f,
                    SystemClock.elapsedRealtime()
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
        }
    }

    fun getAudioPlayerNotificationDeleteIntent(context: Context): PendingIntent? {
        val dismissAction = PendingIntent.getBroadcast(
            context,
            DISMISS_AUDIO_PLAYER_NOTIFICATION_SERVICE_CODE,
            Intent(
                context,
                AudioPlayerActionReceiver::class.java
            ).putExtra(ACTION_TYPE, DISMISS_AUDIO_PLAYER_NOTIFICATION_SERVICE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return dismissAction
    }

    fun getAudioPlayerNotificationResumeIntent(context: Context): PendingIntent? {
        val playAction = PendingIntent.getBroadcast(
            context,
            RESUME_AUDIO_CODE,
            Intent(
                context,
                AudioPlayerActionReceiver::class.java
            ).putExtra(ACTION_TYPE, RESUME_AUDIO),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return playAction
    }

    fun getAudioPlayerNotificationPauseIntent(context: Context): PendingIntent? {
        val pauseAction = PendingIntent.getBroadcast(
            context,
            PAUSE_AUDIO_CODE,
            Intent(
                context,
                AudioPlayerActionReceiver::class.java
            ).putExtra(ACTION_TYPE, PAUSE_AUDIO),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pauseAction
    }

    fun getAudioPlayerNotificationNextIntent(context: Context): PendingIntent? {
        val nextAction = PendingIntent.getBroadcast(
            context,
            NEXT_AUDIO_CODE,
            Intent(
                context,
                AudioPlayerActionReceiver::class.java
            ).putExtra(ACTION_TYPE, NEXT_AUDIO),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return nextAction
    }

    fun getAudioPlayerNotificationPrevIntent(context: Context): PendingIntent? {
        val prevAction = PendingIntent.getBroadcast(
            context,
            PREV_AUDIO_CODE,
            Intent(
                context,
                AudioPlayerActionReceiver::class.java
            ).putExtra(ACTION_TYPE, PREV_AUDIO),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return prevAction
    }
}