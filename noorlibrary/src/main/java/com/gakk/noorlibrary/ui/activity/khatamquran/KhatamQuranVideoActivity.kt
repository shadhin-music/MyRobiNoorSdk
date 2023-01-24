package com.gakk.noorlibrary.ui.activity.khatamquran

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.model.khatam.Videos
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.service.VideoPlayerService
import com.gakk.noorlibrary.ui.adapter.khatamquran.KhatamQuranVideoAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.VideoViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch


internal class KhatamQuranVideoActivity : BaseActivity(), ItemClickControl {

    private var player: ExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition = 0L
    private lateinit var playerControlView: ConstraintLayout
    private lateinit var toggleOrientationButton: AppCompatImageButton
    private lateinit var backButton: AppCompatImageButton
    private var isPortrait: Boolean = true
    private lateinit var repository: RestRepository
    private lateinit var videoModel: VideoViewModel
    private lateinit var adapter: KhatamQuranVideoAdapter

    //view
    private lateinit var videoView : PlayerView
    private lateinit var switchAudio : SwitchCompat
    private lateinit var rvQuranVideos: RecyclerView
    private lateinit var noDataLayout:  ConstraintLayout
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var clKhatamQuran : ConstraintLayout
    private lateinit var tvLearnQuran : AppCompatTextView
    private lateinit var tvDesQuranLearn : AppCompatTextView
    private lateinit var imgNoInternet: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_khatam_quran_video)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initUI()

    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("notification", "created")
            // Create the NotificationChannel
            val name = VIDEO_PLAYER_NOTIFICATION_CHANNEL_NAME
            val descriptionText = VIDEO_PLAYER_NOTIFICATION_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel =
                NotificationChannel(VIDEO_PLAYER_NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun startVideoService(video: Videos) {
        Log.i("VideoPlayerService", "startVideoService")

        val intent = Intent(this, VideoPlayerService::class.java)
        intent.putExtra(KHATAM_QURAN_VIDEOS_TAG, video)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            initializePlayer(service)
            Log.i(
                "VideoPlayerService",
                "onServiceDisconnected: ${System.identityHashCode((service as VideoPlayerService.VideoBinder).player)}"
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("VideoPlayerService", "onServiceDisconnected: ${name?.className}")
            videoView.player = null
        }

    }

    @SuppressLint("MissingPermission")
    private fun initUI() {

        videoView = findViewById(R.id.videoView)
        switchAudio = findViewById(R.id.switchAudio)
        rvQuranVideos = findViewById(R.id.rvQuranVideos)
        noDataLayout = findViewById(R.id.noDataLayout)
        progressLayout = findViewById(R.id.progressLayout)
        clKhatamQuran = findViewById(R.id.clKhatamQuran)
        tvLearnQuran = findViewById(R.id.tvLearnQuran)
        tvDesQuranLearn = findViewById(R.id.tvDesQuranLearn)

        setStatusColor(R.color.black1)
        setStatusbarTextDark()

        var number = ""
        AppPreference.userNumber.let {
            number = it!!
        }

        createNotificationChannel()

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()


            videoModel = ViewModelProvider(
                this@KhatamQuranVideoActivity,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)


            videoModel.loadKhatamQuranVideos()
            subscribeObserver()
        }

        switchAudio.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                player?.setVideoSurface(null)
            } else {
                val surface = videoView.videoSurfaceView as SurfaceView
                player?.setVideoSurfaceView(surface)
            }

        }
    }

    private fun subscribeObserver() {
        videoModel.quranvideosResponse.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            val videoList =
                                it.data.data as MutableList<KhatamQuranVideosResponse.Data>

                            val sortedList =
                                videoList.sortedByDescending { videoList.indexOf(it) }
                                    .toMutableList()

                            val  video = sortedList.get(0)
                            tvLearnQuran.text = video.title
                            tvDesQuranLearn.text = video.text

                            adapter = KhatamQuranVideoAdapter(sortedList, this)
                            rvQuranVideos.adapter = adapter
                            startVideoService(Videos(videos = sortedList, playWhenReady = true))
                        }
                        STATUS_NO_DATA -> {
                            val item = ImageFromOnline("bg_no_data.png")
                            setImageFromUrlNoProgress(imgNoInternet,item.fullImageUrl)
                            noDataLayout.visibility = View.VISIBLE
                        }
                    }
                    progressLayout.visibility = View.GONE
                }
                Status.LOADING -> {
                    Log.e("videoQuran", "Loading")
                    progressLayout.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    Log.e("videoQuran", "Error")
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }

    fun setData() {
        playbackPosition = currentWindow.toLong()
    }

    private fun initializePlayer(service: IBinder?) {

        when (AudioPlayerService.isServiceRunning) {
            null, false -> {
                Log.e("PodcastActivity", ":service paused ");
            }
            true -> {
                when (com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
                    true -> {
                        AudioPlayerService.executePlayerCommand(PAUSE_COMMAND)
                    }
                    false -> {
                        Log.e("PodcastActivity", ":service paused ");
                    }
                }
            }

        }

        setUpPlayerControlView()
        player = (service as VideoPlayerService.VideoBinder).player
        videoView.player = player

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Log.i("VideoPlayerService", "xonIsPlayingChanged: $isPlaying")
                adapter.setPlayingSong(player?.currentMediaItem?.mediaId, isPlaying)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.i("VideoPlayerService", "xonPlayerError: ${error.message}")
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                adapter.setPlayingSong(mediaItem?.mediaId, false)
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                Log.i("VideoPlayerService", "xonPlaybackStateChanged: $state")

                when (state) {
                    ExoPlayer.STATE_BUFFERING -> {
                        playerControlView.findViewById<ProgressBar>(R.id.progressCircular).visibility =
                            View.VISIBLE
                        playerControlView.findViewById<AppCompatImageButton>(R.id.exo_play).visibility =
                            View.GONE
                        playerControlView.findViewById<AppCompatImageButton>(R.id.exo_pause).visibility =
                            View.GONE
                    }


                    ExoPlayer.STATE_READY -> {
                        playerControlView.findViewById<ProgressBar>(R.id.progressCircular).visibility =
                            View.GONE
                    }
                    ExoPlayer.STATE_ENDED -> {


                    }

                    ExoPlayer.STATE_IDLE -> {
                        Log.e("Exoplayer", "STATE_IDLE")
                    }
                }
            }
        })
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            prepareLandscapeUI()
            isPortrait = false
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            preparePortraitUI()
            isPortrait = true
        }
    }

    private fun setUpPlayerControlView() {
        playerControlView = findViewById(R.id.playerControlCourse)!!
        playerControlView.findViewById<DefaultTimeBar>(R.id.exo_progress)
        toggleOrientationButton = videoView.findViewById(R.id.toggleOrientationButton)
        backButton = videoView.findViewById(R.id.backButton)

        toggleOrientationButton.handleClickEvent {
            isPortrait = if (isPortrait) {
                doLandscape()
                false
            } else {
                doPortrait()
                true
            }
        }

        backButton.handleClickEvent {
            onBackPressed()
        }

    }

    private fun doLandscape() {
        prepareLandscapeUI()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun doPortrait() {
        preparePortraitUI()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun prepareLandscapeUI() {
        toggleOrientationButton.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
        val layoutParams = videoView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        hideSystemUI()
    }

    private fun preparePortraitUI() {
        toggleOrientationButton.setImageResource(R.drawable.ic_baseline_fullscreen_24)
        val layoutParams = videoView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = 0
        layoutParams.height = 0
        showSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, clKhatamQuran).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            clKhatamQuran
        ).show(WindowInsetsCompat.Type.systemBars())
    }


    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            doPortrait()
        } else {
            super.onBackPressed()
        }
    }

    override fun setVideoData(video: KhatamQuranVideosResponse.Data, index: Int) {
        if (video.id.equals(player?.currentMediaItem?.mediaId)) {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.play()
            }
        } else {
            player?.seekTo(index, 0L)
            tvLearnQuran.text = video.title
            tvDesQuranLearn.text = video.text
        }
    }
}

interface ItemClickControl {
    fun setVideoData(video: KhatamQuranVideosResponse.Data, index: Int)
}