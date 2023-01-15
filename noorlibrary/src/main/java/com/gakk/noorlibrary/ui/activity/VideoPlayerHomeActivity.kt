package com.gakk.noorlibrary.ui.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.ActivityVideoPlayerHomeBinding
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.adapter.NextVideosAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch


class VideoPlayerHomeActivity : BaseActivity(), VideoDataCallback {

    private lateinit var binding: ActivityVideoPlayerHomeBinding
    private var player: ExoPlayer? = null
    private lateinit var playerControlView: ConstraintLayout
    private lateinit var tvVideoTitle: TextView
    private lateinit var btnShare: ImageButton
    private lateinit var toggleOrientationButton: AppCompatImageButton
    private var isPortrait: Boolean = true

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private lateinit var mVideoData: Data
    private lateinit var mVideoCatId: String
    private lateinit var mVideoSubCatId: String

    private lateinit var repository: RestRepository
    private lateinit var adapter: NextVideosAdapter
    private lateinit var videoModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreference.language?.let { setApplicationLanguage(it) }


        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player_home)

        setStatusColor(R.color.bg)
        setStatusbarTextDark()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.toolBar.title.setText(getString(R.string.app_name))
        // binding.toolBar.btnCustomActionTwo.setImageResource(R.drawable.ic_share)
        //  binding.toolBar.btnCustomActionTwo.visibility = View.VISIBLE


        intent.let {
            mVideoData = intent.getSerializableExtra(VIDEO_DATA) as Data
            mVideoCatId = intent.getStringExtra(VIDEO_CAT_ID)!!
            mVideoSubCatId = intent.getStringExtra(VIDEO_SUBCAT_ID)!!
        }


        binding.toolBar.btnBack.handleClickEvent { finish() }

        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            videoModel = ViewModelProvider(
                this@VideoPlayerHomeActivity,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)


            videoModel.videoList.observe(this@VideoPlayerHomeActivity, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.e("videodata", "" + it.data?.data?.size)
                        adapter = NextVideosAdapter(it.data?.data!!, this@VideoPlayerHomeActivity)
                        binding.rvVideoList.adapter = adapter
                    }
                    Status.LOADING -> {
                        Log.e("videodata", "Loading")
                    }
                    Status.ERROR -> {
                        Log.e("videodata", "Error")
                    }
                }
            })

            videoModel.loadIslamicVideosByCatId(mVideoCatId, mVideoSubCatId, "1")
        }

    }

    override fun onStart() {
        super.onStart()

        if (Util.SDK_INT >= 24) {
            initializePlayer()
            setData(mVideoData)
        }
    }

    override fun onStop() {
        super.onStop()

        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onPause() {
        super.onPause()

        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()

        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
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

    private fun initializePlayer() {

        setUpPlayerControlView()
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            ExoPlayer.STATE_BUFFERING -> {
                                playerControlView.findViewById<ProgressBar>(R.id.progressCircular).visibility =
                                    View.VISIBLE
                            }

                            ExoPlayer.STATE_READY -> {
                                playerControlView.findViewById<ProgressBar>(R.id.progressCircular).visibility =
                                    View.GONE
                            }
                            ExoPlayer.STATE_ENDED -> {
                                Log.e("Exoplayer", "STATE_ENDED")

                            }

                            ExoPlayer.STATE_IDLE -> {

                            }
                        }
                    }
                })
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    private fun setUpPlayerControlView() {
        playerControlView = findViewById(R.id.playerControlView)!!
        tvVideoTitle = playerControlView.findViewById(R.id.tvVideoTitle)
        btnShare = playerControlView.findViewById(R.id.btnShare)
        toggleOrientationButton = binding.videoView.findViewById(R.id.toggleOrientationButton)


        toggleOrientationButton.handleClickEvent {
            isPortrait = if (isPortrait) {
                doLandscape()
                false
            } else {
                doPortrait()
                true
            }
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
        val layoutParams = binding.videoView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        hideSystemUI()
    }

    private fun preparePortraitUI() {
        toggleOrientationButton.setImageResource(R.drawable.ic_baseline_fullscreen_24)
        val layoutParams = binding.videoView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = 0
        layoutParams.height = 0
        showSystemUI()
    }

    private fun hideSystemUI() {
        binding.toolBar.root.visibility = GONE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.layoutParent).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        binding.toolBar.root.visibility = VISIBLE
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.layoutParent
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    override fun setData(data: Data) {
        binding.video = data
        tvVideoTitle.setText(data.contenTtitle)

        player?.seekTo(currentWindow, playbackPosition)
        val mediaItem = data.contentFullUrl.let { MediaItem.fromUri(it) }
        player?.setMediaItem(mediaItem)
    }

    override fun onBackPressed() {
        finish()
    }
}

interface VideoDataCallback {
    fun setData(data: Data)
}