package com.gakk.noorlibrary.ui.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.ActivityYoutubePlayerBinding
import com.gakk.noorlibrary.model.quranSchool.QuranSchoolModel
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.adapter.QuranSchoolAdapter
import com.gakk.noorlibrary.ui.adapter.QuranSchoolChildAdapter
import com.gakk.noorlibrary.util.*


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/4/2021, Sun
 */

internal class QuranSchoolPlayerActivity : BaseActivity(),
    QuranSchoolChildAdapter.OnItemClickListener,
    QuranSchoolAdapter.OnItemClickListener {

    private lateinit var binding: ActivityYoutubePlayerBinding
    private lateinit var playerControlView: ConstraintLayout
    private lateinit var tvVideoTitle: TextView
    private lateinit var btnShare: ImageButton
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private lateinit var listQuranClass: ArrayList<Data>
    private var position = 0
    private lateinit var toggleOrientationButton: AppCompatImageButton
    private var isPortrait: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube_player)

        setStatusColor(R.color.txt_color_title)
        getParcelable()
    }

    private fun getParcelable() {
        val isQuranSchool = intent.getBooleanExtra(IS_QURAN_SCHOOL, false)
        val isQuranLiveClass = intent.getBooleanExtra(IS_QURAN_LIVE_CLASS, false)
        val isInstructiveVideo = intent.getBooleanExtra(IS_INSTRUCTIVE_VIDEO, false)
        val catType = intent.getStringExtra("CatType")

        position = intent.getIntExtra(YOUTUBE_SELECTED_VIDEO_TAG, 0)

        var item: Data? = null
        if (isQuranSchool || isInstructiveVideo || isQuranLiveClass) {
            listQuranClass =
                intent.getSerializableExtra(YOUTUBE_VIDEO_TAG_SCHOOL) as ArrayList<Data>


            listQuranClass.let {
                item = it[position]

                checkItemsSizeQuran(it)
                if (isQuranLiveClass){
                    binding.scholarsName.text = item?.singer ?: ""
                }else {
                    binding.scholarsName.text = item?.contenTtitle ?: ""
                }

            }
        } else {
            val list = intent.getParcelableArrayListExtra<QuranSchoolModel>(YOUTUBE_VIDEO_TAG)
            list?.let {
                val itemVideo = it[position]
                it.removeAt(position)

                checkItemsSize(it)
                binding.scholarsName.text = itemVideo.scholarName ?: ""
            }
        }

        when (isInstructiveVideo) {
            true -> {
                if (catType.equals("Instructive Video")) {
                    binding.tvTitlePlayer.setText(getString(R.string.cat_instructive_video))
                } else {
                    binding.tvTitlePlayer.setText(getString(R.string.cat_live_qa))
                }
            }

            false -> {
                if (isQuranLiveClass) {
                    binding.tvTitlePlayer.setText(item?.contenTtitle)
                } else {
                    binding.tvTitlePlayer.setText(getString(R.string.digital_school))
                }
            }
        }

    }

    private fun checkItemsSize(it: ArrayList<QuranSchoolModel>) {
        if (it.size >= 2) {
            binding.emptyLayout.hide()
            binding.nextVideoRv.show()
            setUpRV(it)
        } else {
            binding.emptyLayout.show()
            binding.nextVideoRv.hide()
        }
    }

    private fun checkItemsSizeQuran(it: ArrayList<Data>) {
        if (it.size >= 2) {
            binding.emptyLayout.hide()
            binding.nextVideoRv.show()
            setUpRVQuran(it)
        } else {
            binding.emptyLayout.show()
            binding.nextVideoRv.hide()
        }
    }

    private fun setUpRV(it: ArrayList<QuranSchoolModel>) {
        val adapter = QuranSchoolChildAdapter(this).apply {
            submitList(it)
        }
        binding.nextVideoRv.adapter = adapter
    }

    private fun setUpRVQuran(it: ArrayList<Data>) {
        val adapter = QuranSchoolAdapter(this).apply {
            submitList(it)
        }
        binding.nextVideoRv.adapter = adapter
    }

    override fun onItemClick(postion: Int, currentList: List<QuranSchoolModel>) {
        if (postion < currentList.size) {
            val item = currentList[postion]
        }
    }

    override fun onItemClickVideo(postion: Int, currentList: List<Data>) {
        Log.d("check_onclick", "onItemClick: called " + postion + " " + currentList.size)
        if (postion < currentList.size) {
            val item = currentList[postion]
            item.let { model ->
                binding.scholarsName.text = item.contenTtitle ?: ""
                setData(item.contentFullUrl)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (Util.SDK_INT >= 24) {
            initializePlayer()
            setData(listQuranClass[position].contentFullUrl)
        }
    }

    fun setData(url: String) {
        player?.seekTo(currentWindow, playbackPosition)
        val mediaItem = url.let { MediaItem.fromUri(it) }
        player?.setMediaItem(mediaItem)
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

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
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

    private fun setUpPlayerControlView() {
        playerControlView = findViewById(R.id.playerControlView)!!
        toggleOrientationButton = binding.videoView.findViewById(R.id.toggleOrientationButton)
        tvVideoTitle = playerControlView.findViewById(R.id.tvVideoTitle)
        btnShare = playerControlView.findViewById(R.id.btnShare)

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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.mainContainer
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}