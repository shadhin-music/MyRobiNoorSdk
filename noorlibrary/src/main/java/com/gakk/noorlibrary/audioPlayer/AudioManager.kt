package com.gakk.noorlibrary.audioPlayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.util.NEXT_COMMAND
import com.gakk.noorlibrary.util.SURAH_LIST_TYPE
import kotlin.math.E


object AudioManager {

    private var listType: String? = null
    private var currentIndex: Int? = null
    private var surahList: MutableList<Data>? = null
    private var isNotPaused = true


    private var audioPlayer: ExoPlayer? = null
    private var mediaSource: MediaSource? = null

    fun getAudioPlayer() = com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer


    private var trackEndedListener: Player.Listener? = null

    object PlayListControl {
        fun setPlayListType(type: String) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.listType = type
        }

        fun getPlayListType() = com.gakk.noorlibrary.audioPlayer.AudioManager.listType

        fun setplayList(surahs: MutableList<Data>? = null) {
            surahs?.let {
                com.gakk.noorlibrary.audioPlayer.AudioManager.surahList = surahs
            }
        }

        fun getSurahList() = com.gakk.noorlibrary.audioPlayer.AudioManager.surahList

        fun getCurrentSurah() = com.gakk.noorlibrary.audioPlayer.AudioManager.surahList?.get(
            com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex ?: 0)

//        fun clearPlayList(){
//            surahList= null
//        }

        fun isPlayListNull(): Boolean {
            when (com.gakk.noorlibrary.audioPlayer.AudioManager.listType) {
                SURAH_LIST_TYPE -> return com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.isSurahListNull()
                else -> return com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.isIslamicSongListNull()
            }
        }

        fun isSurahListNull() = com.gakk.noorlibrary.audioPlayer.AudioManager.surahList == null
        fun isIslamicSongListNull() = true

        fun setCurrentIndex(index: Int) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex = index
        }

        fun getCurrentIndex(): Int {
            return com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex ?: 0
        }

        fun incrementCurrentIndex() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex?.let {
                if (com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex!! < com.gakk.noorlibrary.audioPlayer.AudioManager.surahList!!.size - 1) {
                    com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex = com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex!! + 1
                }
                Log.e("SERVICE___", "Current Surah Index : ${com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex}")
            }


        }

        fun decrementCurrentIndex() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex?.let {
                if (com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex!! > 0) {
                    com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex = com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex!! - 1
                }
                Log.i("SERVICE___", "Current Surah Index : ${com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex}")
            }


        }

    }


    object InstanceControl {

        //fun isPlayerInitialised()= audioPlayer!=null
        fun initAudioPlayer(context: Context) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.InstanceControl.destroyAudioPlayerInstance()

            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer = ExoPlayer.Builder(context).build()
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()

            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)

            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    playbackState: Int
                ) {
                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        Log.i("STATE", "PLAYING =$playWhenReady")
                        //isNotPaused=playWhenReady
//                        if (AudioManager.isServiceStarted) {
//                            AudioManager.resumePlayButtonAction()
//                        }

                        // media actually playing
                    } else if (playWhenReady) {
                        Log.i("STATE", "PLAYING =$playWhenReady")
                        //isNotPaused=playWhenReady
                        // might be idle (plays after prepare()),
                        // buffering (plays when data available)
                        // or ended (plays when seek away from end)
                    } else {
                        Log.i("STATE", "PLAYING =$playWhenReady")
                        //isNotPaused=playWhenReady
                        //  Log.i("STATE", "PAUSED")
//                        if (AudioManager.isServiceStarted) {
//                            AudioManager.pauseButtonAction()
//                        }

                        // player paused in any state
                    }
                }
            })


            com.gakk.noorlibrary.audioPlayer.AudioManager.InstanceControl.initTrackEndedListener()
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.addListener(com.gakk.noorlibrary.audioPlayer.AudioManager.trackEndedListener!!)

        }

        fun initTrackEndedListener() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.trackEndedListener = object : Player.Listener {
                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    playbackState: Int
                ) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        BaseApplication.getAppContext().let {
                            AudioPlayerService.executePlayerCommand(NEXT_COMMAND)
                        }

                    }
                }
            }
        }

        fun destroyAudioPlayerInstance() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.let {
                it.release()
            }
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer = null
        }


    }

    object PlayerControl {

        fun setIsNotPauseToTrue() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused = true
        }

        fun getIsNotPaused(): Boolean = com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused

        fun playAction(context: Context) {

            val appName = com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getApplicationName(
                context!!
            )!!
            val path =
                com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getPathByListType()
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(
                        context!!,
                        appName
                    )
                )

            com.gakk.noorlibrary.audioPlayer.AudioManager.mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.fromUri(
                        Uri.parse(
                            path
                        )
                    )
                )

            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.prepare(com.gakk.noorlibrary.audioPlayer.AudioManager.mediaSource!!)
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.playWhenReady =
                com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused

            Log.i("SERVICE___", "ISNOTPAUSED:${com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused}")

        }

        fun getApplicationName(context: Context): String? {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
        }

        fun getPathByListType(): String {
            when (com.gakk.noorlibrary.audioPlayer.AudioManager.listType) {
                SURAH_LIST_TYPE -> return com.gakk.noorlibrary.audioPlayer.AudioManager.surahList!!.get(
                    com.gakk.noorlibrary.audioPlayer.AudioManager.currentIndex!!).audioUrl
                else -> return ""
            }
        }

        fun pauseAction() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused = false
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.playWhenReady =
                com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused
        }

        fun resumeAction() {
            com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused = true
            com.gakk.noorlibrary.audioPlayer.AudioManager.audioPlayer?.playWhenReady =
                com.gakk.noorlibrary.audioPlayer.AudioManager.isNotPaused
        }

        fun nextAction(context: Context) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.incrementCurrentIndex()
            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.playAction(context)
        }

        fun prevAction(context: Context) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.decrementCurrentIndex()
            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.playAction(context)
        }

    }

}