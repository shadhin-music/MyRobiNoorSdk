package com.gakk.noorlibrary.audioPlayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.util.NEXT_COMMAND
import com.gakk.noorlibrary.util.SURAH_LIST_TYPE
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


object AudioManager {

    private var listType: String? = null
    private var currentIndex: Int? = null
    private var surahList: MutableList<Data>? = null
    private var isNotPaused = true


    private var audioPlayer: ExoPlayer? = null
    private var mediaSource: MediaSource? = null

    fun getAudioPlayer() = audioPlayer


    private var trackEndedListener: Player.Listener? = null

    object PlayListControl {
        fun setPlayListType(type: String) {
            listType = type
        }

        fun getPlayListType() = listType

        fun setplayList(surahs: MutableList<Data>? = null) {
            surahs?.let {
                surahList = surahs
            }
        }

        fun getSurahList() = surahList

        fun getCurrentSurah() = surahList?.get(
            currentIndex ?: 0
        )

        fun isPlayListNull(): Boolean {
            when (listType) {
                SURAH_LIST_TYPE -> return isSurahListNull()
                else -> return isIslamicSongListNull()
            }
        }

        fun isSurahListNull() = surahList == null
        fun isIslamicSongListNull() = true

        fun setCurrentIndex(index: Int) {
            currentIndex = index
        }

        fun getCurrentIndex(): Int {
            return currentIndex ?: 0
        }

        fun incrementCurrentIndex() {
            currentIndex?.let {
                if (currentIndex!! < surahList!!.size - 1) {
                    currentIndex = currentIndex!! + 1
                }
                Log.e("SERVICE___", "Current Surah Index : $currentIndex")
            }


        }

        fun decrementCurrentIndex() {
            currentIndex?.let {
                if (currentIndex!! > 0) {
                    currentIndex = currentIndex!! - 1
                }
                Log.i("SERVICE___", "Current Surah Index : $currentIndex")
            }


        }

    }


    object InstanceControl {

        fun initAudioPlayer(context: Context) {
            destroyAudioPlayerInstance()

            audioPlayer = ExoPlayer.Builder(context).build()
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()

            audioPlayer?.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)

            audioPlayer?.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    playbackState: Int
                ) {
                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        Log.i("STATE", "PLAYING =$playWhenReady")
                    } else if (playWhenReady) {
                        Log.i("STATE", "PLAYING =$playWhenReady")

                    } else {
                        Log.i("STATE", "PLAYING =$playWhenReady")

                    }
                }
            })


            initTrackEndedListener()
            audioPlayer?.addListener(trackEndedListener!!)

        }

        fun initTrackEndedListener() {
            trackEndedListener = object : Player.Listener {
                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    playbackState: Int
                ) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        Noor.appContext.let {
                            AudioPlayerService.executePlayerCommand(NEXT_COMMAND)
                        }

                    }
                }
            }
        }

        fun destroyAudioPlayerInstance() {
            audioPlayer?.let {
                it.release()
            }
            audioPlayer = null
        }


    }

    object PlayerControl {

        fun setIsNotPauseToTrue() {
            isNotPaused = true
        }

        fun getIsNotPaused(): Boolean = isNotPaused

        fun playAction(context: Context) {

            val appName = getApplicationName(
                context!!
            )!!
            val path =
                getPathByListType()
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(
                        context!!,
                        appName
                    )
                )

            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.fromUri(
                        Uri.parse(
                            path
                        )
                    )
                )

            audioPlayer?.prepare(mediaSource!!)
            audioPlayer?.playWhenReady =
                isNotPaused

            Log.i("SERVICE___", "ISNOTPAUSED:${isNotPaused}")

        }

        fun getApplicationName(context: Context): String? {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
        }

        fun getPathByListType(): String {
            when (listType) {
                SURAH_LIST_TYPE -> return surahList!!.get(
                    currentIndex!!
                ).audioUrl
                else -> return ""
            }
        }

        fun pauseAction() {
            isNotPaused = false
            audioPlayer?.playWhenReady =
                isNotPaused
        }

        fun resumeAction() {
            isNotPaused = true
            audioPlayer?.playWhenReady =
                isNotPaused
        }

        fun nextAction(context: Context) {
            PlayListControl.incrementCurrentIndex()
            playAction(context)
        }

        fun prevAction(context: Context) {
            PlayListControl.decrementCurrentIndex()
            playAction(context)
        }

    }

}