package com.gakk.noorlibrary.ui.activity.podcast

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.audioPlayer.AudioManager
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.ActivityPodcastBinding
import com.gakk.noorlibrary.databinding.DialogNoLiveBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.podcast.CommentListResponse
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.adapter.podcast.CommentPagingAdapter
import com.gakk.noorlibrary.ui.adapter.podcast.LiveVideoAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.PodcastViewModel
import kotlinx.coroutines.launch

class PodcastActivity : BaseActivity(), ItemClickListener {

    private lateinit var binding: ActivityPodcastBinding
    private lateinit var repository: RestRepository
    private lateinit var model: PodcastViewModel
    private var player: ExoPlayer? = null
    private lateinit var fullscreenToggleButton: ImageButton
    private lateinit var backButton: ImageButton
    private val videoWidth = 854
    private val videoHeight = 480
    private lateinit var playerOnScaleGestureListener: PlayerOnScaleGestureListener
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var commentBaseAdapter: CommentPagingAdapter? = null
    private val PAGE_START = 1
    private var currentPage: Int = PAGE_START
    private var TOTAL_PAGES = 1
    private var PER_PAGE = 10
    private var isLastPage = false
    private var isLoading = false
    private var notifyOnly = false
    private var notifyPage = 1
    private var upPos = -1
    private lateinit var categoryId: String
    private lateinit var textContentId: String
    private lateinit var subCategoryId: String
    private lateinit var liveMessage: String
    private var linearLayoutManager: LinearLayoutManager? = null
    private val periodicHandler = Handler()
    private val periodicRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                if (!player!!.playWhenReady) {
                    //  binding.btnPlayPause.setImageResource(R.drawable.exo_controls_play);
                } else {
                    //  binding.btnPlayPause.setImageResource(R.drawable.exo_controls_pause);
                }
            } catch (e: Exception) {
            }
            periodicHandler.postDelayed(this, 1000)
        }
    }

    private val hideControlHandler = Handler()
    private val hideControlRunnable = Runnable {
        //binding.controlView.setVisibility(GONE);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_podcast)
        this.setStatusColor(R.color.black1)

        intent.let {
            subCategoryId = intent.getStringExtra(LIVE_PODCAST_TAG) ?: ""
        }
        setupUi()
        gestureSetup()

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            val factory = PodViewModelFactory(repository)
            model = ViewModelProvider(
                this@PodcastActivity,
                factory
            ).get(PodcastViewModel::class.java)

            var number = ""
            AppPreference.userNumber.let {
                number = it!!
            }

            // val categoryId: String
            if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                binding.cardLiveCollapse.visibility = View.GONE
                binding.tvLiveVideos.setText("ভিডিও সমূহ")
                categoryId = "62457f53577c4a348361ded2"


            } else {
                binding.cardLiveCollapse.visibility = View.VISIBLE
                binding.tvLiveVideos.setText("Live Videos")
                categoryId = "62457f6b577c4a348361ded3"
            }

            model.loadLiveVideos(categoryId)
            model.loadLiveUrl("622f0159803daefd93291ba3", subCategoryId)


            subscribeObserver()
        }

        periodicHandler.postDelayed(periodicRunnable, 1000)

    }

    private fun subscribeObserver() {

        model.livevideosResponse.observe(this@PodcastActivity) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("YLActivity", ":LOADING ");
                }

                Status.SUCCESS -> {
                    Log.e("YLActivity", ":SUCCESS ");


                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            val liveVideos = it.data?.data
                            binding.rvLiveVideos.adapter = LiveVideoAdapter(liveVideos, this)
                        }
                        STATUS_NO_DATA -> {
                            binding.itemNoData = ImageFromOnline("bg_no_data.png")
                            binding.llNoLiveVideos.visibility = View.VISIBLE
                        }
                    }
                }

                Status.ERROR -> {
                    Log.e("YLActivity", ":SUCCESS ");
                }
            }
        }

        model.liveUrlResponse.observe(
            this@PodcastActivity
        ) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("YLActivity", ":LOADING ");
                }

                Status.SUCCESS -> {
                    Log.e("YLActivity", ":SUCCESS ")
                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            val liveData = it.data?.data?.get(0)
                            liveData.let {
                                liveData?.textInArabic.let {
                                    liveMessage =
                                        liveData?.textInArabic ?: "এই মুহূর্তে কোন লাইভ ভিডিও নেই"
                                }

                            }
                            categoryId = liveData?.category!!
                            textContentId = liveData?.id!!

                            binding.item = liveData
                            if (subCategoryId.equals("6243e1ca807cf5d3bb259b1a")) {
                                showAndHandleCommentSection()
                            }
                            if (liveData?.refUrl.isNullOrEmpty()) {
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    binding.cardLiveCollapse.visibility = View.GONE
                                } else {
                                    binding.cardLive.visibility = View.GONE
                                    binding.cardLiveCollapse.visibility = View.VISIBLE
                                }

                                showNoLiveDialog(liveMessage)
                                binding.ivOverlay.visibility = View.VISIBLE

                                liveData?.imageUrl.let {
                                    Glide.with(BaseApplication.getAppContext())
                                        .load(liveData?.contentBaseUrl + "/" + liveData?.imageUrl)
                                        .listener(object : RequestListener<Drawable> {
                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                isFirstResource: Boolean
                                            ): Boolean {

                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {

                                                return false
                                            }

                                        })
                                        .error(R.drawable.place_holder_16_9_ratio)
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(binding.ivOverlay)
                                }

                            } else {
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    binding.cardLiveCollapse.visibility = View.GONE
                                } else {
                                    binding.cardLive.visibility = View.VISIBLE
                                    binding.commentRoot.visibility = View.VISIBLE
                                    binding.rvLiveVideos.visibility = View.GONE
                                    binding.tvLiveVideos.visibility = View.GONE
                                    binding.cardLiveCollapse.visibility = View.GONE
                                }

                                binding.ivOverlay.visibility = View.GONE
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    setLiveData(liveData = liveData!!, false)
                                } else {
                                    setLiveData(liveData = liveData!!, true)
                                }
                            }
                        }

                        STATUS_NO_DATA -> {
                            Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show()
                        }
                    }

                }

                Status.ERROR -> {
                    Log.e("YLActivity", ":SUCCESS ");

                }
            }
        }

        model.commentListData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("commentListData", ":LOADING ");
                }

                Status.SUCCESS -> {
                    Log.e("commentListData", ":SUCCESS ");

                    binding.tvCount.text = it.data?.totalRecords.toString()
                    binding.tvCountCollapse.text = it.data?.totalRecords.toString()
                    Log.e(
                        "base_api: refreshed %s",
                        upPos.toString() + " " + currentPage + " " + notifyOnly + " " + notifyPage + " " + it.data?.data?.size
                    )

                    if (notifyOnly && notifyPage != 0 && upPos != -1) {
                        val en: Int = PER_PAGE * (notifyPage - 1)
                        val f: Int = upPos - en
                        if (f < it.data?.data?.size!!) {
                            commentBaseAdapter!!.modifyItem(upPos, it.data.data.get(f))
                        }
                        notifyOnly = false
                        notifyPage = 0
                    } else {
                        setCommentAdapter(it.data)
                    }
                }

                Status.ERROR -> {
                    Log.e("commentListData", ":ERROR " + it.message);
                }
            }
        }

        model.postComment.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("postComment", ":LOADING ")
                }

                Status.SUCCESS -> {
                    Log.e("postComment", ":SUCCESS ")
                    binding.commentLayout.commentEd.setText("")
                    resetPaging()
                    showAndHandleCommentSection()
                }

                Status.ERROR -> {
                    Log.e("postComment", ":ERROR " + it.message)
                }
            }
        }

        model.addLikeComment.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("addLikeComment", ":LOADING ")
                }

                Status.SUCCESS -> {
                    Log.e("addLikeComment", ":SUCCESS ")

                }

                Status.ERROR -> {
                    Log.e("addLikeComment", ":ERROR " + it.message)
                }
            }
        }
    }

    private fun setLiveData(liveData: LiveVideosResponse.Data, isYoutube: Boolean) {
        if (binding.ivOverlay.isVisible) {
            binding.ivOverlay.visibility = View.GONE
        }
        binding.item = liveData
        val id = liveData.refUrl
        handleYoutubeAndNormalVideo(id!!, isYoutube)
        categoryId = "622f0159803daefd93291ba3"
        textContentId = liveData.id!!
    }

    private fun setupUi() {

        fullscreenToggleButton = binding.playerView.findViewById(R.id.toggleOrientationButton)
        backButton = binding.playerView.findViewById(R.id.backButton)
        fullscreenToggleButton.setOnClickListener({ view: View? -> toggleOrientation() })
        backButton.setOnClickListener({ view: View? -> onBackPressed() })
        configOrientation(resources.configuration.orientation)

        binding.cardLiveCollapse.handleClickEvent {
            binding.cardLive.visibility = View.VISIBLE
            binding.commentRoot.visibility = View.VISIBLE
            binding.rvLiveVideos.visibility = View.GONE
            binding.tvLiveVideos.visibility = View.GONE
            binding.cardLiveCollapse.visibility = View.GONE

            if (binding.llNoLiveVideos.isVisible) {
                binding.llNoLiveVideos.visibility = View.GONE
            }
        }

        binding.cardLive.handleClickEvent {
            binding.cardLive.visibility = View.GONE
            binding.commentRoot.visibility = View.GONE
            binding.rvLiveVideos.visibility = View.VISIBLE
            binding.tvLiveVideos.visibility = View.VISIBLE
            binding.cardLiveCollapse.visibility = View.VISIBLE
        }
    }

    private fun showAndHandleCommentSection() {
        binding.commentLayout.commentRv.invalidate()
        commentBaseAdapter = null
        linearLayoutManager = null

        binding.commentLayout.ivComment.handleClickEvent {
            val message = binding.commentLayout.commentEd.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this, "Please write your question!", Toast.LENGTH_LONG).show()
            } else {
                model.addComment(categoryId, textContentId, message)
            }

        }

        commentBaseAdapter =
            com.gakk.noorlibrary.ui.adapter.podcast.CommentPagingAdapter(this, this)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        commentBaseAdapter!!.clearAll()
        binding.commentLayout.commentRv.layoutManager = linearLayoutManager

        binding.commentLayout.commentRv.adapter = commentBaseAdapter
        binding.commentLayout.commentRv.isNestedScrollingEnabled = true


        model.loadCommentList(categoryId, textContentId, 1)
    }

    private fun setCommentAdapter(data: CommentListResponse?) {

        if (currentPage == 1) {
            if (data != null) {
                val commentDataList: List<CommentListResponse.Data?>? = data.data
                if (data.totalRecords!! < 20) {
                    val params = binding.commentLayout.commentRv.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.commentLayout.commentRv.layoutParams = params
                }
                TOTAL_PAGES = data.totalPage!!
                PER_PAGE = commentDataList?.size!!

                commentBaseAdapter?.addAll(commentDataList)
                if (currentPage < TOTAL_PAGES) {
                    commentBaseAdapter!!.addLoadingFooter()
                } else {
                    isLastPage = true
                }
            }
        } else {
            if (data != null) {
                commentBaseAdapter!!.removeLoadingFooter()
                isLoading = false
                val commentDataList: List<CommentListResponse.Data?>? = data.data
                commentBaseAdapter!!.addAll(commentDataList)
                if (currentPage < TOTAL_PAGES) {
                    commentBaseAdapter!!.addLoadingFooter()
                } else {
                    isLastPage = true
                }
            }
        }
    }

    private fun resetPaging() {
        isLoading = false
        isLastPage = false
        TOTAL_PAGES = 1
        PER_PAGE = 10
        currentPage = PAGE_START
    }

    private fun toggleOrientation() {
        val screenOrientation: Int
        screenOrientation = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                fullscreenToggleButton.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                fullscreenToggleButton.setImageResource(R.drawable.ic_baseline_fullscreen_24)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            else -> {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
        requestedOrientation = screenOrientation
    }

    private fun configOrientation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                prepareLandscapeUI()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                preparePortraitUI()
            }
            else -> {
                preparePortraitUI()
            }
        }
    }

    private fun prepareLandscapeUI() {
        setLandscapePlayerSize()
        fullscreenToggleButton.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
    }

    private fun setLandscapePlayerSize() {
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        binding.playerLayout.layoutParams = layoutParams
    }

    private fun preparePortraitUI() {
        delay(100) {
            setPortraitPlayerSize()
            null
        }
        fullscreenToggleButton.setImageResource(R.drawable.ic_baseline_fullscreen_24)
    }

    private fun setPortraitPlayerSize() {
        val displayWidth = com.gakk.noorlibrary.util.UtilHelper.getScreenSize(this).x
        val height = calculateVideoHeight(displayWidth, videoWidth, videoHeight)
        val layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        binding.playerLayout.layoutParams = layoutParams
    }



    private fun handleYoutubeAndNormalVideo(url: String, isYoutube: Boolean) {

        val exoProgressBar = binding.playerView.findViewById<DefaultTimeBar>(R.id.exo_progress)
        val liveIcon = binding.playerView.findViewById<AppCompatImageView>(R.id.noor_live)
        val liveText = binding.playerView.findViewById<AppCompatTextView>(R.id.tvLive)
        val videoDurationText =
            binding.playerView.findViewById<AppCompatTextView>(R.id.exo_position)
        val totalDurationText =
            binding.playerView.findViewById<AppCompatTextView>(R.id.exo_duration)

        if (isYoutube) {
            model.fetchYoutubeVideo(url, true)?.observe(this
            ) { details: YoutubeVideoDetails? ->
                if (details != null && details.getUrl("480p") != null) {
                    Log.e("TAG", "" + details.getUrl("480p"))
                    playVideo(details.getUrl("480p")!!)
                } else {
                    Toast.makeText(
                        this,
                        "Can't play this video",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            liveIcon.visibility = View.VISIBLE
            liveText.visibility = View.VISIBLE
            exoProgressBar.visibility = View.GONE
            videoDurationText.visibility = View.GONE
            totalDurationText.visibility = View.GONE

        } else {
            if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                exoProgressBar.visibility = View.GONE
                videoDurationText.visibility = View.GONE
                totalDurationText.visibility = View.GONE
            } else {
                exoProgressBar.visibility = View.VISIBLE
                videoDurationText.visibility = View.VISIBLE
                totalDurationText.visibility = View.VISIBLE

            }
            liveIcon.visibility = View.GONE
            liveText.visibility = View.GONE
            playVideo(CONTENT_BASE_URL + url)
        }
    }

    private fun playVideo(url: String) {

        when (AudioPlayerService.isServiceRunning) {
            null, false -> {
                Log.e("CourseStartTabFragment", ":service paused ");
            }
            true -> {
                when (com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
                    true -> {
                        AudioPlayerService.executePlayerCommand(PAUSE_COMMAND)
                    }
                    false -> {
                        Log.e("CourseStartTabFragment", ":service paused ");
                    }
                }
            }

        }

        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        player?.clearMediaItems()
        player?.setMediaItem(
            MediaItem.Builder()
                .setUri(url)
                .build()
        )
        player?.prepare()
        binding.playerView.player = player
        player?.setPlayWhenReady(true)
        player?.addListener(playbackStatus)
        binding.videoView.setOnClickListener {
            try {
                hideControlHandler.removeCallbacks(hideControlRunnable)
            } catch (e: Exception) {
            }
            hideControlHandler.postDelayed(hideControlRunnable, 5000)
        }
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

            //showNoLiveDialog(liveMessage)
        }
    }

    private fun showBuffering() {
        binding.bufferProgress.visibility = View.VISIBLE
        binding.playerView.useController = false
    }

    private fun hideBuffering() {
        binding.bufferProgress.visibility = View.GONE
        binding.playerView.useController = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        configOrientation(newConfig.orientation)
    }


    private fun gestureSetup() {
        playerOnScaleGestureListener = PlayerOnScaleGestureListener(binding.playerView, this)
        scaleGestureDetector = ScaleGestureDetector(this, playerOnScaleGestureListener)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (scaleGestureDetector != null) {
                event?.let { scaleGestureDetector!!.onTouchEvent(it) }
            }
        }
        return true
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }

    fun removeAllHandlers() {
        try {
            periodicHandler.removeCallbacks(periodicRunnable)
            hideControlHandler.removeCallbacks(hideControlRunnable)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        releasePlayer()
        removeAllHandlers()
        super.onDestroy()
    }

    override fun onPause() {
        if (player != null) {
            if (player?.isPlaying == true) {
                player?.pause()
            }
        }
        super.onPause()
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toggleOrientation()
        } else {
            super.onBackPressed()
        }
    }

    override fun onLikeButtonClicked(id: String, isRefresh: Boolean, page: Int) {
        model.likeComment(id)
    }

    override fun setData(item: LiveVideosResponse.Data) {
        resetPaging()
        setLiveData(item, false)
    }

    override fun onMoreButtonClicked() {

        if (linearLayoutManager == null) {
            return
        }
        val visibleItemCount = linearLayoutManager!!.childCount
        val totalItemCount = linearLayoutManager!!.itemCount
        val firstVisibleItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
        if (!isLoading && !isLastPage) {

            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PER_PAGE) {
                isLoading = true
                currentPage += 1
                model.loadCommentList(categoryId, textContentId, currentPage)
            }
        }
    }

    fun showNoLiveDialog(liveMessage: String = "এই মুহূর্তে কোন লাইভ ভিডিও নেই") {
        val customDialog =
            MaterialAlertDialogBuilder(
                this,
                R.style.MaterialAlertDialog_rounded
            )
        val binding: DialogNoLiveBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_no_live,
            null,
            false
        )


        val dialogView: View = binding.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )


        binding.tvTitleExit.setText(liveMessage)
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()


        binding.btnComplete.handleClickEvent {
            alertDialog.dismiss()
        }

    }
}

interface ItemClickListener {
    fun onLikeButtonClicked(id: String, isRefresh: Boolean, page: Int)
    fun setData(item: LiveVideosResponse.Data)
    fun onMoreButtonClicked()
}