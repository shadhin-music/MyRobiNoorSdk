package com.gakk.noorlibrary.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.databinding.ActivityCommonYoutubePlayerBinding
import com.gakk.noorlibrary.util.IS_IJTEMA_LIVE_VIDEO
import com.gakk.noorlibrary.util.Util.parseAndGetKey

class YoutubePlayerActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityCommonYoutubePlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_common_youtube_player)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        getParcelable()
    }

    private fun getParcelable() {

        val isLand = intent.getBooleanExtra(IS_IJTEMA_LIVE_VIDEO, false)
        if (isLand) {
            BaseApplication.IJTEMA_LIVE_VIDEO_ID?.let { initPlayer(it) }
        } else {
            BaseApplication.LIVE_VIDEO_ID?.let { initPlayer(it) }
        }

    }

    private fun initPlayer(id: String) {
        binding.youtubePlayerView.initialize(
            parseAndGetKey(),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    player: YouTubePlayer?,
                    wasRestored: Boolean
                ) {
                    Log.d("youtubePlayer", "onInitializationSuccess: called")
                    player?.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                    player?.loadVideo(id)
                    player?.play()

                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    error: YouTubeInitializationResult?
                ) {
                    val errorMessage = error.toString()
                    Toast.makeText(this@YoutubePlayerActivity, errorMessage, Toast.LENGTH_LONG)
                        .show()
                    Log.d("errorMessage:", errorMessage)
                }
            })
    }
}