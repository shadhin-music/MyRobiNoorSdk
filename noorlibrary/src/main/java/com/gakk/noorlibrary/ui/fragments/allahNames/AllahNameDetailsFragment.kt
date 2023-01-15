package com.gakk.noorlibrary.ui.fragments.allahNames

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.SHARE
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentAllahNameDetailsBinding
import com.gakk.noorlibrary.extralib.cardstackview.*
import com.gakk.noorlibrary.model.names.Data
import com.gakk.noorlibrary.ui.adapter.CardStackAdapter
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.util.toArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ARG_ALLAH_NAME_LIST = "allahNameList"
private const val ARG_SELECTED_INDEX = "selectedIndex"


class AllahNameDetailsFragment : Fragment(), CardStackListener {

    private var mDetailsCallBack: DetailsCallBack? = null
    private var mSelectedIndex: Int = 0
    private lateinit var binding: FragmentAllahNameDetailsBinding
    private lateinit var mNameInfo: Data
    private lateinit var mList: List<Data>
    private lateinit var playerControl: MediaPlayerControl
    private var soundOff = false
    private lateinit var cardAdapter: CardStackAdapter
    private val manager by lazy { CardStackLayoutManager(context, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedIndex = it.getInt(ARG_SELECTED_INDEX)
            mList = it.getParcelableArrayList<Data>(ARG_ALLAH_NAME_LIST) as List<Data>
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_allah_name_details,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCardStackView()
        loadData()
        binding.ivPlay.handleClickEvent {
            playerControl.handlePlayPauseClick()
        }

        binding.ivSound.handleClickEvent {
            when (soundOff) {
                true -> {
                    soundOff = false
                    binding.ivSound.setImageResource(R.drawable.ic_volume)
                }
                false -> {
                    soundOff = true
                    binding.ivSound.setImageResource(R.drawable.ic_volume_off)
                }
            }
        }

        binding.ivReload.handleClickEvent {
            mSelectedIndex = -1
            cardAdapter.notifyDataSetChanged()
        }

        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
        mDetailsCallBack?.setOrUpdateActionButtonTag(SHARE, ActionButtonType.TypeTwo)
    }

    fun loadData() {
        lifecycleScope.launch {

            if (mSelectedIndex == 99) {
                mSelectedIndex = 0
            }

            mNameInfo = mList.get(mSelectedIndex)
            playerControl = MediaPlayerControl()
            binding.tvContent.setText(mNameInfo.fazilat)

            launch {
                delay(50)
                Log.d("timerHasFinished", "loadData: called")
                playerControl.handlePlayPauseClick()
            }

        }
    }

    private fun setupCardStackView() {

        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(20.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(70.0f)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        val setting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Left)
            .setDuration(Duration.Slow.duration)
            .setInterpolator(AccelerateInterpolator())
            .build()
        manager.setSwipeAnimationSetting(setting)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.cardStackView.layoutManager = manager
        cardAdapter = CardStackAdapter(mList)
        binding.cardStackView.adapter = cardAdapter
        binding.cardStackView.scrollToPosition(mSelectedIndex)
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
        playerControl.killMediaPlayer()
    }

    override fun onPause() {
        super.onPause()

        playerControl.pause()
    }

    inner class MediaPlayerControl {
        private var mPlayer: MediaPlayer? = null
        private var isPlaying = false
        private var currentDuration: Int? = null

        init {
            isPlaying = false
            // currentDuration = 0
        }

        fun handlePlayPauseClick() {
            when (isPlaying) {
                true -> pause()
                else -> play()
            }
        }

        fun play() {
            isPlaying = true
            mPlayer = MediaPlayer()
            binding.ivPlay.setImageResource(R.drawable.ic_pause_round)
            mPlayer?.setOnCompletionListener {
                mSelectedIndex++
                playerControl.killMediaPlayer()
                loadData()
                if (mSelectedIndex == 0) {
                    cardAdapter.notifyDataSetChanged()
                } else {
                    binding.cardStackView.swipe()
                }
            }
            mPlayer?.setDataSource(mNameInfo.contentBaseUrl + "/" + mNameInfo.contentUrl)
            mPlayer?.prepare()
            //mPlayer?.seekTo(currentDuration ?: 0)
            mPlayer?.start()

            when (soundOff) {
                true -> {
                    mPlayer?.setVolume(0F, 0F)
                }

                false -> {
                    mPlayer?.setVolume(1F, 1F)
                }
            }
        }

        fun pause() {
            isPlaying = false
            //currentDuration = mPlayer?.currentPosition
            binding.ivPlay.setImageResource(R.drawable.ic_play_round)
            mPlayer?.stop()
            killMediaPlayer()
        }

        fun killMediaPlayer() {
            try {
                mPlayer?.reset()
                mPlayer?.release()
                mPlayer = null
            } catch (e: Exception) {

            }

        }

    }

    companion object {

        @JvmStatic
        fun newInstance(
            allahNameCallBack: List<Data>,
            selectedIndex: Int
        ) =
            AllahNameDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        ARG_ALLAH_NAME_LIST,
                        allahNameCallBack.toArrayList()
                    )
                    putInt(ARG_SELECTED_INDEX, selectedIndex)
                }
            }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction?.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.e("CardStackView", "onCardSwiped")
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled:")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardAppeared: ($position)")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardDisappeared: ($position)")
    }
}



