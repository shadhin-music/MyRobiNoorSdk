package com.gakk.noorlibrary.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.spherical.SphericalGLSurfaceView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.ActivityKafelaPlayerBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import com.gakk.noorlibrary.util.PodViewModelFactory
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.SUB_CAT_ID_UNDEFINED
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.PodcastViewModel
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch


internal class KafelaPlayerActivity : BaseActivity() {


    private lateinit var progressLayout:View
    private lateinit var videoView: PlayerView
    private lateinit var bufferProgress: View

    private var player: ExoPlayer? = null
    private lateinit var model: PodcastViewModel
    private lateinit var repository: RestRepository
    private lateinit var modelLiterature: LiteratureViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       setContentView( R.layout.activity_kafela_player)
        setupUi()

        lifecycleScope.launch() {
            val job = launch() {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            val factory = PodViewModelFactory(repository)
            model = ViewModelProvider(
                this@KafelaPlayerActivity,
                factory
            ).get(PodcastViewModel::class.java)

            modelLiterature = ViewModelProvider(
                this@KafelaPlayerActivity,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            modelLiterature.loadTextBasedLiteratureListBySubCategory(
                "62c12ec03a387048db3d6877",
                SUB_CAT_ID_UNDEFINED,
                "1"
            )

            subscribeObserver()
        }

    }

    private fun setupUi() {
        progressLayout = findViewById(R.id.progressLayout)
        videoView = findViewById(R.id.videoView)
        bufferProgress= findViewById(R.id.bufferProgress)
    }

    private fun subscribeObserver() {
        modelLiterature.literatureListData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()
                    Log.e("listsize", "ss" + literatureList.size)
                    handleYoutubeAndNormalVideo(literatureList.get(0).refUrl!!)

                    progressLayout.visibility = View.GONE
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

    }

    private fun handleYoutubeAndNormalVideo(url: String) {

        model.fetchYoutubeVideo(url, false)?.observe(
            this
        ) { details: YoutubeVideoDetails? ->

            val videoUrl = details?.getVideoFromAdaptiveFormatsUrl("1080s")
            val audioUrl = details?.getAudioFromAdaptiveFormatsUrl()
            Log.i("handleYoutubeAndNorma", "video : ${videoUrl}")
            Log.i("handleYoutubeAndNorma", "audio : ${audioUrl}")
            playVideo(videoUrl, audioUrl)

            /*if (details?.getVideoUrlFromFormat("720p") != null) {
                Log.e("TAG", "" + details.getVideoUrlFromFormat("720p"))
                playVideo(details.getVideoUrlFromFormat("720p")!!)
                binding.videoView.onResume()
            } else {
                Toast.makeText(
                    this,
                    "Can't play this video",
                    Toast.LENGTH_LONG
                ).show()
            }*/
        }
    }

    private fun playVideo(videoUrl: String?, audioUrl: String?) {
        val toggleOrientationButton: AppCompatImageButton =
            videoView.findViewById(R.id.toggleOrientationButton)
        toggleOrientationButton.visibility = View.GONE

        if (player == null) {
            player = ExoPlayer.Builder(this)
                .build()
        }

        val videoMediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()

        val audioMediaItem = MediaItem.Builder()
            .setUri(audioUrl)
            .build()

        val videoSource =
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@KafelaPlayerActivity))
                .createMediaSource(videoMediaItem)
        val audioSource =
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@KafelaPlayerActivity))
                .createMediaSource(audioMediaItem)
        val mergeMediaSource = MergingMediaSource(videoSource, audioSource)

        player?.clearMediaItems()
        player?.setMediaSource(mergeMediaSource, 1000L)
        player?.playWhenReady = true
        player?.prepare()
        (videoView.getVideoSurfaceView() as SphericalGLSurfaceView).setDefaultStereoMode(C.STEREO_MODE_TOP_BOTTOM)
        videoView.player = player
        player?.addListener(playbackStatus)

        videoView.onResume()
    }

    private val playbackStatus: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_BUFFERING -> {
                    showBuffering()
                }
                ExoPlayer.STATE_READY -> {
                    hideBuffering()
                }
                else -> {
                    hideBuffering()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.i("onPlayerError", "onPlayerError: ${error.message}")

        }
    }

    private fun showBuffering() {
        bufferProgress.visibility = View.VISIBLE
        videoView.useController = false
    }

    private fun hideBuffering() {
        bufferProgress.visibility = View.GONE
        videoView.useController = true
    }

    override fun onPause() {
        if (player != null) {
            if (player?.isPlaying == true) {
                player?.pause()
            }
        }
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }

}