package com.gakk.noorlibrary.ui.activity.podcast

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
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
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.podcast.CommentListResponse
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.adapter.podcast.CommentPagingAdapter
import com.gakk.noorlibrary.ui.adapter.podcast.LiveVideoAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.PodcastViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

internal class PodcastActivity : BaseActivity(), ItemClickListener {

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

    // view
    private lateinit var cardLiveCollapse: RelativeLayout
    private lateinit var tvLiveVideos: AppCompatTextView
    private lateinit var rvLiveVideos: RecyclerView
    private lateinit var cardLive: CardView
    private lateinit var imgNoInternet: ImageView
    private lateinit var llNoLiveVideos: LinearLayout
    private lateinit var ivOverlay: AppCompatImageView
    private lateinit var tvCount: AppCompatTextView
    private lateinit var tvCountCollapse: AppCompatTextView
    private lateinit var comment_root: RelativeLayout
    private lateinit var commentLayout: ConstraintLayout
    private lateinit var comment_ed: AppCompatEditText
    private lateinit var comment_rv: RecyclerView
    private lateinit var ivComment: ImageView
    private lateinit var playerLayout: FrameLayout
    private lateinit var playerView: PlayerView
    private lateinit var videoView: RelativeLayout
    private lateinit var bufferProgress: ProgressBar
    private lateinit var videoTitle: TextView
    private lateinit var tvSpeaker: AppCompatTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var modelUserTracking: AddUserTrackigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_podcast)
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

            modelUserTracking = ViewModelProvider(
                this@PodcastActivity,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            var number = ""
            AppPreference.userNumber.let {
                number = it!!
            }

            val pageName: String
            // val categoryId: String
            if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                cardLiveCollapse.visibility = View.GONE
                tvLiveVideos.setText("ভিডিও সমূহ")
                categoryId = "62457f53577c4a348361ded2"
                pageName = PAGE_ISLAMIC_PODCAST_RECORDED
            } else {
                cardLiveCollapse.visibility = View.VISIBLE
                tvLiveVideos.setText("Live Videos")
                categoryId = "62457f6b577c4a348361ded3"
                pageName = PAGE_ISLAMIC_PODCAST_LIVE
            }

            model.loadLiveVideos(categoryId)
            model.loadLiveUrl("622f0159803daefd93291ba3", subCategoryId)

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, pageName)
            }

            subscribeObserver()
        }

        periodicHandler.postDelayed(periodicRunnable, 1000)

    }

    private fun subscribeObserver() {
        modelUserTracking.trackUser.observe(this@PodcastActivity) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("trackUser", "LOADING")
                }
                Status.ERROR -> {
                    Log.e("trackUser", "ERROR")
                }

                Status.SUCCESS -> {
                    Log.e("trackUser", "SUCCESS")
                }
            }
        }
        model.livevideosResponse.observe(this@PodcastActivity) {
            when (it.status) {
                Status.LOADING -> {
                    Log.e("YLActivity", ":LOADING ");
                }

                Status.SUCCESS -> {
                    Log.e("YLActivity", ":SUCCESS ");


                    when (it.data?.status) {
                        STATUS_SUCCESS -> {
                            val liveVideos = it.data.data
                            rvLiveVideos.adapter = LiveVideoAdapter(liveVideos, this)
                        }
                        STATUS_NO_DATA -> {
                            val itemNoData = ImageFromOnline("bg_no_data.png")
                            setImageFromUrlNoProgress(imgNoInternet, itemNoData.fullImageUrl)
                            llNoLiveVideos.visibility = View.VISIBLE
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
                            val liveData = it.data.data?.get(0)
                            liveData.let {
                                liveData?.textInArabic.let {
                                    liveMessage =
                                        liveData?.textInArabic ?: "এই মুহূর্তে কোন লাইভ ভিডিও নেই"
                                }

                            }
                            categoryId = liveData?.category!!
                            textContentId = liveData.id!!

                            val item = liveData
                            videoTitle.text = item.title
                            tvSpeaker.text = item.text
                            setImageFromUrl(ivOverlay, item.fullImageUrl, progressBar)

                            if (subCategoryId.equals("6243e1ca807cf5d3bb259b1a")) {
                                showAndHandleCommentSection()
                            }
                            if (liveData.refUrl.isNullOrEmpty()) {
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    cardLiveCollapse.visibility = View.GONE
                                } else {
                                    cardLive.visibility = View.GONE
                                    cardLiveCollapse.visibility = View.VISIBLE
                                }

                                showNoLiveDialog(liveMessage)
                                ivOverlay.visibility = View.VISIBLE

                                liveData.imageUrl.let {
                                    Noor.appContext?.let {
                                        Glide.with(it)
                                            .load(liveData.contentBaseUrl + "/" + liveData.imageUrl)
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
                                            .into(ivOverlay)
                                    }

                                }

                            } else {
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    cardLiveCollapse.visibility = View.GONE
                                } else {
                                    cardLive.visibility = View.VISIBLE
                                    comment_root.visibility = View.VISIBLE
                                    rvLiveVideos.visibility = View.GONE
                                    tvLiveVideos.visibility = View.GONE
                                    cardLiveCollapse.visibility = View.GONE
                                }

                                ivOverlay.visibility = View.GONE
                                if (subCategoryId.equals("6243e1b0807cf5d3bb259b19")) {
                                    setLiveData(liveData = liveData, false)
                                } else {
                                    setLiveData(liveData = liveData, true)
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

                    tvCount.text = it.data?.totalRecords.toString()
                    tvCountCollapse.text = it.data?.totalRecords.toString()
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
                    comment_ed.setText("")
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
        if (ivOverlay.isVisible) {
            ivOverlay.visibility = View.GONE
        }
        val item = liveData
        videoTitle.text = item.title
        tvSpeaker.text = item.text
        setImageFromUrl(ivOverlay, item.fullImageUrl, progressBar)

        val id = liveData.refUrl
        handleYoutubeAndNormalVideo(id!!, isYoutube)
        categoryId = "622f0159803daefd93291ba3"
        textContentId = liveData.id!!
    }

    private fun setupUi() {

        cardLiveCollapse = findViewById(R.id.cardLiveCollapse)
        tvLiveVideos = findViewById(R.id.tvLiveVideos)
        imgNoInternet = findViewById(R.id.imgNoInternet)
        llNoLiveVideos = findViewById(R.id.llNoLiveVideos)
        cardLive = findViewById(R.id.cardLive)
        ivOverlay = findViewById(R.id.ivOverlay)
        rvLiveVideos = findViewById(R.id.rvLiveVideos)
        tvCount = findViewById(R.id.tvCount)
        tvCountCollapse = findViewById(R.id.tvCountCollapse)
        comment_root = findViewById(R.id.comment_root)
        commentLayout = findViewById(R.id.commentLayout)
        comment_ed = findViewById(R.id.comment_ed)
        ivComment = findViewById(R.id.ivComment)
        playerLayout = findViewById(R.id.playerLayout)
        playerView = findViewById(R.id.playerView)
        videoView = findViewById(R.id.videoView)
        bufferProgress = findViewById(R.id.bufferProgress)
        videoTitle = findViewById(R.id.videoTitle)
        tvSpeaker = findViewById(R.id.tvSpeaker)
        progressBar = findViewById(R.id.progressBar)
        comment_rv = findViewById(R.id.comment_rv)


        fullscreenToggleButton = findViewById(R.id.toggleOrientationButton)
        backButton = findViewById(R.id.backButton)
        fullscreenToggleButton.setOnClickListener({ view: View? -> toggleOrientation() })
        backButton.setOnClickListener({ view: View? -> onBackPressed() })
        configOrientation(resources.configuration.orientation)

        cardLiveCollapse.handleClickEvent {
            cardLive.visibility = View.VISIBLE
            comment_root.visibility = View.VISIBLE
            rvLiveVideos.visibility = View.GONE
            tvLiveVideos.visibility = View.GONE
            cardLiveCollapse.visibility = View.GONE

            if (llNoLiveVideos.isVisible) {
                llNoLiveVideos.visibility = View.GONE
            }
        }

        cardLive.handleClickEvent {
            cardLive.visibility = View.GONE
            comment_root.visibility = View.GONE
            rvLiveVideos.visibility = View.VISIBLE
            tvLiveVideos.visibility = View.VISIBLE
            cardLiveCollapse.visibility = View.VISIBLE
        }
    }

    private fun showAndHandleCommentSection() {
        comment_rv.invalidate()
        commentBaseAdapter = null
        linearLayoutManager = null

        ivComment.handleClickEvent {
            val message = comment_ed.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this, "Please write your question!", Toast.LENGTH_LONG).show()
            } else {
                model.addComment(categoryId, textContentId, message)
            }

        }

        commentBaseAdapter = CommentPagingAdapter(this, this)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        commentBaseAdapter!!.clearAll()
        comment_rv.layoutManager = linearLayoutManager
        comment_rv.adapter = commentBaseAdapter
        comment_rv.isNestedScrollingEnabled = true


        model.loadCommentList(categoryId, textContentId, 1)
    }

    private fun setCommentAdapter(data: CommentListResponse?) {

        if (currentPage == 1) {
            if (data != null) {
                val commentDataList: List<CommentListResponse.Data?>? = data.data
                if (data.totalRecords!! < 20) {
                    val params = comment_rv.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    comment_rv.layoutParams = params
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
        playerLayout.layoutParams = layoutParams
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
        playerLayout.layoutParams = layoutParams
    }


    private fun handleYoutubeAndNormalVideo(url: String, isYoutube: Boolean) {

        val exoProgressBar = findViewById<DefaultTimeBar>(R.id.exo_progress)
        val liveIcon = findViewById<AppCompatImageView>(R.id.noor_live)
        val liveText = findViewById<AppCompatTextView>(R.id.tvLive)
        val videoDurationText = findViewById<AppCompatTextView>(R.id.exo_position)
        val totalDurationText =
            findViewById<AppCompatTextView>(R.id.exo_duration)

        if (isYoutube) {
            model.fetchYoutubeVideo(url, true)?.observe(
                this
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
        playerView.player = player
        player?.setPlayWhenReady(true)
        player?.addListener(playbackStatus)
        videoView.setOnClickListener {
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
        bufferProgress.visibility = View.VISIBLE
        playerView.useController = false
    }

    private fun hideBuffering() {
        bufferProgress.visibility = View.GONE
        playerView.useController = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        configOrientation(newConfig.orientation)
    }


    private fun gestureSetup() {
        playerOnScaleGestureListener = PlayerOnScaleGestureListener(playerView, this)
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
        val binding: View = LayoutInflater.from(this).inflate(
            R.layout.dialog_no_live,
            null,
            false
        )


        val dialogView: View = binding
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )


        binding.findViewById<TextView>(R.id.tvTitleExit).setText(liveMessage)
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()


        binding.findViewById<View>(R.id.btnComplete).handleClickEvent {
            alertDialog.dismiss()
        }

    }
}

interface ItemClickListener {
    fun onLikeButtonClicked(id: String, isRefresh: Boolean, page: Int)
    fun setData(item: LiveVideosResponse.Data)
    fun onMoreButtonClicked()
}