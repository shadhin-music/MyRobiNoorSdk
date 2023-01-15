package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/5/2021, Wed
 */
object AzanPlayer {

    private var mMediaPlayer: MediaPlayer? = null

    fun playAdanFromRawFolder(url: String) {
        releaseMediaPlayer()

        mMediaPlayer = MediaPlayer().apply {
            setDataSource(url)
        }

        mMediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mMediaPlayer?.prepare()
        mMediaPlayer?.setOnPreparedListener {
            it?.start()
        }
    }

    fun releaseMediaPlayer() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

}