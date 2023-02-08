package com.gakk.noorlibrary.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.audioPlayer.AudioManager
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.notification.NotificationControl
import com.gakk.noorlibrary.ui.fragments.SurahDetailsAudioPlayerCallBack
import com.gakk.noorlibrary.ui.fragments.SurahDetailsHeaderPlayStatControl
import com.gakk.noorlibrary.ui.fragments.SurahFullPlayerAudioPlayerCallBack
import com.gakk.noorlibrary.util.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class AudioPlayerService : Service() {
    private lateinit var serviceScope: CoroutineScope

    fun dismissForegroundService() {
        Log.i("DISMISSFG", "DISMISS_FOREGROUND CALLED")
        AudioManager.PlayerControl.setIsNotPauseToTrue()
        AudioManager.InstanceControl.destroyAudioPlayerInstance()
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                weakReference!!.get()!!.stopForeground(true)
            } else {
                weakReference!!.get()!!.stopSelf()
            }

        } catch (e: Exception) {

        }

        mediaSession.isActive = false
        isServiceRunning = false
        surahDetailsCallBack?.cleanUpUI()
        surahFullPlayerCallBack?.cleanUpUI()
        serviceScope.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("ClearFromRecentService", "END")
        Intent(this, AudioPlayerService::class.java).also {
            stopService(it)
        }
        stopSelf()
    }


    companion object {
        var context: Context? = null
        var weakReference: WeakReference<AudioPlayerService>? = null

        fun isCurrentSurahPlaying(id: String): Boolean {
            return AudioManager.PlayListControl.getPlayListType() == SURAH_LIST_TYPE && AudioManager.PlayListControl.getCurrentSurah()?.id ?: "-1" == id && AudioManager.PlayerControl.getIsNotPaused() == true && isServiceRunning == true
        }

        var surahDetailsCallBack: SurahDetailsAudioPlayerCallBack? = null
        var surahFullPlayerCallBack: SurahFullPlayerAudioPlayerCallBack? = null

        var isServiceRunning: Boolean? = null

        lateinit var mediaSession: MediaSessionCompat
        lateinit var connector: MediaSessionConnector


        fun attatchSurahFullPlayerCallBack(surahFullPlayerCallBack: SurahFullPlayerAudioPlayerCallBack?) {
            this.surahFullPlayerCallBack = surahFullPlayerCallBack
        }

        fun detachSurahFullPlayerCallBack() {
            this.surahFullPlayerCallBack = null
        }

        fun attatchSurahDetailsCallBack(surahDetailsCallBack: SurahDetailsAudioPlayerCallBack?) {
            this.surahDetailsCallBack = surahDetailsCallBack
        }

        fun detachSurahDetailsCallBack() {
            this.surahDetailsCallBack = null
        }

        fun executePlayerCommand(command: String) {

            isServiceRunning?.let {
                when (it) {
                    true -> {
                        when (command) {
                            PLAY_COMMAND -> {
                                AudioManager.PlayerControl.setIsNotPauseToTrue()
                                AudioManager.PlayerControl.playAction(context!!)
                                surahDetailsCallBack?.updateMiniPlayerPlayPauseButton(true)
                                surahFullPlayerCallBack?.updatePlayerControlPlayPauseButton(true)
                            }

                            RESUME_COMMAND -> {
                                AudioManager.PlayerControl.resumeAction()
                                surahDetailsCallBack?.updateMiniPlayerPlayPauseButton(true)
                                surahFullPlayerCallBack?.updatePlayerControlPlayPauseButton(true)
                            }

                            PAUSE_COMMAND -> {
                                AudioManager.PlayerControl.pauseAction()
                                surahDetailsCallBack?.updateMiniPlayerPlayPauseButton(false)
                                surahFullPlayerCallBack?.updatePlayerControlPlayPauseButton(false)
                            }
                            PREV_COMMAND -> {
                                AudioManager.PlayerControl.prevAction(context!!)
                                AudioManager.PlayListControl.getCurrentSurah()?.id?.let { it1 ->
                                    surahDetailsCallBack?.reloadDetailsWithUpdatedSurahIndex(
                                        it1
                                    )
                                }
                                surahFullPlayerCallBack?.loadUIWithUpdatedIndex()
                            }
                            NEXT_COMMAND -> {
                                AudioManager.PlayerControl.nextAction(context!!)
                                AudioManager.PlayListControl.getCurrentSurah()?.id?.let { it1 ->
                                    surahDetailsCallBack?.reloadDetailsWithUpdatedSurahIndex(
                                        it1
                                    )
                                }
                                surahFullPlayerCallBack?.loadUIWithUpdatedIndex()

                            }

                            DISMISS_COMMAND -> {
                                AudioPlayerServiceInstanceControl.stopService(context!!)
                            }
                        }

                        if (command != DISMISS_COMMAND) {

                            val title = AudioManager.PlayListControl.getCurrentSurah()?.name
                            val ayahCountWithPrefix =
                                AudioManager.PlayListControl.getCurrentSurah()?.ayahCountWithPrefix
                            val bitmap: Bitmap = BitmapFactory.decodeResource(
                                context!!.getResources(),
                                R.drawable.bg_quran
                            )
                            val duration =
                                AudioManager.PlayListControl.getCurrentSurah()?.durationInMs
                            val builder = ayahCountWithPrefix?.let { it1 ->
                                title?.let { it2 ->
                                    duration?.let { it3 ->
                                        NotificationControl.getAudioPlayerNotification(
                                            context!!,
                                            "",
                                            "",
                                            it2,
                                            it1,
                                            it3,
                                            bitmap
                                        )
                                    }
                                }
                            }


                            if (builder != null) {
                                weakReference?.get()
                                    ?.startForeground(
                                        AUDIO_PLAYER_NOTIFICATION_ID,
                                        builder!!.build()
                                    )
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    weakReference!!.get()!!.stopForeground(true)
                                } else {
                                    weakReference!!.get()!!.stopSelf()
                                }
                            }
                        }
                    }
                    else -> {
                        //do nothing
                    }
                }
            }

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        weakReference = WeakReference(this)
        Log.i("SERVICE___", "AudioPlayerServic onCreate()")
        context = this
        serviceScope = CoroutineScope(Job())


        serviceScope.launch(Dispatchers.Main) {
            while (true) {
                AudioManager?.getAudioPlayer()?.duration?.let {
                    withContext(Dispatchers.Main) {
                        surahDetailsCallBack?.updateMiniPlayerTotalDuration(it)
                        surahFullPlayerCallBack?.updatePlayerControlTotalDuration(it)
                    }
                }

                AudioManager?.getAudioPlayer()?.currentPosition?.let {
                    withContext(Dispatchers.Main) {
                        surahDetailsCallBack?.updateMiniPlayerCurrentDuration(it)
                        surahFullPlayerCallBack?.updatePlayerControlCurrentDuration(it)
                    }

                }

                withContext(Dispatchers.Main) {
                    SurahDetailsHeaderPlayStatControl.updatePlayStat()
                    Log.i(
                        "CONFIG_",
                        "IsServiceRunning-$isServiceRunning AudioManager.PlayListControl.getPlayListType()== SURAH_LIST_TYPE ${AudioManager.PlayListControl.getPlayListType() == SURAH_LIST_TYPE}"
                    )
                    if (isServiceRunning == true && AudioManager.PlayListControl.getPlayListType() == SURAH_LIST_TYPE) {
                        surahDetailsCallBack?.inflateMiniPlayerWithSelectedSurah()
                        surahDetailsCallBack?.toggleMiniPlayerVisibility(true)
                        surahFullPlayerCallBack?.togglePlayerControlVisibility(true)
                    } else {
                        surahDetailsCallBack?.toggleMiniPlayerVisibility(false)
                        surahFullPlayerCallBack?.togglePlayerControlVisibility(false)
                    }
                }





                delay(1000)
            }
        }
        isServiceRunning = true
        AudioManager.InstanceControl.initAudioPlayer(context!!)
        NotificationControl.initNotificationManager(context!!)
        NotificationControl.createNotificationChannel(
            AUDIO_PLAYER_NOTIFICATION_CHANNEL_NAME,
            AUDIO_PLAYER_NOTIFICATION_CHANNEL_DESC, AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID
        )


        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val pendingItent = PendingIntent.getBroadcast(
            baseContext,
            0, mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSessionCompat(baseContext, "Media Session", null, pendingItent).also {
            it.isActive = true
        }


        connector = MediaSessionConnector(mediaSession)

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        connector.setPlayer(AudioManager.getAudioPlayer())
        mediaSession.isActive = true

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                executePlayerCommand(RESUME_COMMAND)
            }

            override fun onPause() {
                executePlayerCommand(PAUSE_COMMAND)
            }

            override fun onSkipToNext() {
                executePlayerCommand(NEXT_COMMAND)
            }

            override fun onSkipToPrevious() {
                executePlayerCommand(PREV_COMMAND)
            }

            override fun onSeekTo(pos: Long) {
                AudioManager?.getAudioPlayer()?.seekTo(pos)
                surahDetailsCallBack?.updateMiniPlayerCurrentDuration(pos)
            }
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        AudioManager.PlayListControl.setPlayListType(SURAH_LIST_TYPE)


        var currentSurahIndex = intent?.getIntExtra(CURRENT_INDEX, 0)
        if (currentSurahIndex != null) {
            AudioManager.PlayListControl.setCurrentIndex(currentSurahIndex)
        }
        var surahList: MutableList<Data>? =
            intent?.getSerializableExtra(PLAY_LIST) as MutableList<Data>?
        surahList?.let {
            AudioManager.PlayListControl.setplayList(it)
            executePlayerCommand(PLAY_COMMAND)
        }

        Log.e("audioplayer", "service called")

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        dismissForegroundService()

        NotificationControl.getNotificationManager()?.cancel(AUDIO_PLAYER_NOTIFICATION_ID)
        Log.i("DESTROY", "Service destroyed")
    }
}