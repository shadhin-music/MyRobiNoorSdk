package com.gakk.noorlibrary.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.audioPlayer.AudioManager
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quran.surahDetail.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.adapter.SurahDetailsAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.QuranViewModel
import kotlinx.coroutines.launch
import java.io.Serializable


private const val ARG_SURAH_ID = "surahId"
private const val MAX_ZOOM_LEVEL = 10
private const val ARG_SURAH_LIST = "surahList"

internal class SurahDetailsFragment : Fragment(), SurahDetailsCallBack, PagingViewCallBack,
    PageReloadCallBack, SurahDetailsAudioPlayerCallBack, PlayPauseFavControl {

    private var mSurahId: String? = null
    private var mDetailsCallBack: DetailsCallBack? = null
    private var adapter: SurahDetailsAdapter? = null

    private lateinit var model: QuranViewModel
    private lateinit var repository: RestRepository

    private var mPageNo: Int = 0
    private var mHasMoreData = true

    private var surahLoadingComplete = false
    private var ayahLoadingComplete = false

    private var surahDetails: Data? = null
    private var ayatList: MutableList<com.gakk.noorlibrary.model.quran.ayah.Data>? = null
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var layoutZoomControl: ConstraintLayout
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnZoomIn: ImageButton
    private lateinit var rvSurahDetails: RecyclerView
    private lateinit var layoutMiniPlayer: ConstraintLayout
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var tvDuration: AppCompatTextView
    private lateinit var tvCurrentTime: AppCompatTextView
    private lateinit var tvSurahName: AppCompatTextView
    private lateinit var tvAyahNum: AppCompatTextView
    private lateinit var imgQuranMini:ImageView

    private lateinit var exoProgressMini: AppCompatSeekBar
    private lateinit var modelUserTracking: AddUserTrackigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSurahId = it.getString(ARG_SURAH_ID)
            SurahListControl.copySurahList((it.getSerializable(ARG_SURAH_LIST) as MutableList<com.gakk.noorlibrary.model.quran.surah.Data>?))
            SurahListControl.updateSelectedIndex(mSurahId!!)
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_surah_details,
            container, false
        )

        progressLayout = view.findViewById(R.id.progressLayout)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)
        layoutZoomControl = view.findViewById(R.id.layoutZoomControl)
        rvSurahDetails = view.findViewById(R.id.rvSurahDetails)
        layoutMiniPlayer = view.findViewById(R.id.layoutMiniPlayer)
        btnZoomOut = layoutZoomControl.findViewById(R.id.btnZoomOut)
        btnZoomIn = layoutZoomControl.findViewById(R.id.btnZoomIn)
        btnPrev = layoutMiniPlayer.findViewById(R.id.btnPrev)
        btnNext = layoutMiniPlayer.findViewById(R.id.btnNext)
        btnPlayPause = layoutMiniPlayer.findViewById(R.id.btnPlayPause)
        exoProgressMini = layoutMiniPlayer.findViewById(R.id.exo_progress_mini)
        tvDuration = layoutMiniPlayer.findViewById(R.id.tvDuration)
        tvCurrentTime = layoutMiniPlayer.findViewById(R.id.tvCurrentTime)
        tvSurahName = layoutMiniPlayer.findViewById(R.id.tvSurahName)
        tvAyahNum = layoutMiniPlayer.findViewById(R.id.tvAyahNum)
        imgQuranMini = layoutMiniPlayer.findViewById(R.id.imgQuranMini)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surahLoadingComplete = false
        ayahLoadingComplete = false

        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        mDetailsCallBack?.setToolBarTitle("")


        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@SurahDetailsFragment,
                QuranViewModel.FACTORY(repository)
            ).get(QuranViewModel::class.java)

            modelUserTracking = ViewModelProvider(
                this@SurahDetailsFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            initPagingProperties()
            loadData()

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, PAGE_QURAN_LIST)
            }

            model.surahDetailsResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        surahDetails = it.data?.data
                        surahLoadingComplete = true
                        updateUI()


                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        noInternetLayout.visibility = VISIBLE
                    }
                    Status.LOADING -> {
                        if (mPageNo == 1) {
                            progressLayout.visibility = VISIBLE
                        }
                        noInternetLayout.visibility = GONE
                    }
                }
            })

            model.isSurahFavouriteResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        progressLayout.visibility = GONE
                        surahDetails?.isSurahFavByThisUser = it.data?.data!!
                        adapter?.updateSurahDetails(surahDetails)
                        adapter?.notifyItemChanged(0)
                        updateToolbarForThisFragment()
                        showUI()


                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                        updateToolbarForThisFragment()
                        showUI()
                    }
                    Status.LOADING -> {

                    }
                }
            })

            model.ayahBySurahId.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        ayahLoadingComplete = true
                        ayatList = it.data?.data
                        updateUI()

                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        noInternetLayout.visibility = VISIBLE
                    }
                    Status.LOADING -> {
                        if (mPageNo == 1) {
                            progressLayout.visibility = VISIBLE
                        }
                        noInternetLayout.visibility = GONE
                    }
                }
            })

            model.favouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        surahDetails?.isSurahFavByThisUser = true
                        adapter?.updateSurahDetails(surahDetails)
                        adapter?.notifyItemChanged(0)
                        progressLayout.visibility = GONE
                        updateToolbarForThisFragment()
                        val surah = com.gakk.noorlibrary.model.quran.surah.Data(
                            id = surahDetails!!.id,
                            order = surahDetails!!.order,
                            name = surahDetails!!.name
                        )
                        FavouriteSurahObserver.postFavouriteNotification(surah)
                    }
                    Status.LOADING -> {
                        progressLayout.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                }
            })

            model.unFavouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        surahDetails?.isSurahFavByThisUser = false
                        adapter?.updateSurahDetails(surahDetails)
                        adapter?.notifyItemChanged(0)

                        progressLayout.visibility = GONE
                        updateToolbarForThisFragment()
                        var surah = com.gakk.noorlibrary.model.quran.surah.Data(
                            id = surahDetails!!.id,
                            order = surahDetails!!.order,
                            name = surahDetails!!.name
                        )
                        FavouriteSurahObserver.postUnFavouriteNotification(surah)

                    }
                    Status.LOADING -> {
                        progressLayout.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                }
            })

            modelUserTracking.trackUser.observe(viewLifecycleOwner) {
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

            btnRetry.handleClickEvent {
                loadData()
            }

        }

        AudioPlayerService.attatchSurahDetailsCallBack(this)
        val bg_quran_mini_player = ImageFromOnline("Drawable/bg_quran_mini_player.webp")
       setImageFromUrlNoProgress(imgQuranMini,bg_quran_mini_player.fullImageUrl)

    }

    override fun onPause() {
        super.onPause()

        Log.i("FLC", "OnPause()")
    }

    override fun onResume() {
        super.onResume()
        val isPlaying = AudioManager.PlayerControl.getIsNotPaused()
        updateMiniPlayerPlayPauseButton(isPlaying)

        Log.i("FLC", "onResume()")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_quran))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
        mDetailsCallBack?.setOrUpdateActionButtonTag(SHARE, ActionButtonType.TypeTwo)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            surahId: String,
            detailsCallBack: DetailsCallBack,
            surahList: MutableList<com.gakk.noorlibrary.model.quran.surah.Data>?
        ) =
            SurahDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SURAH_ID, surahId)
                    putSerializable(ARG_SURAH_LIST, surahList as Serializable)
                }
            }
    }

    private fun prepareZoomControls() {
        setUpZoomBtnClickEvents()
    }

    private fun setUpZoomBtnClickEvents() {
        btnZoomOut.handleClickEvent {
            adapter?.getFontControl()!!.decrementCurrentAyaOffset()
            toggleZoomButtonsStatus()
            adapter?.getFontControl()!!.updateAllLayouts()
        }

        btnZoomIn.handleClickEvent {

            adapter?.getFontControl()?.incrementCurrentAyaOffset()
            toggleZoomButtonsStatus()
            adapter?.getFontControl()?.updateAllLayouts()

        }
    }

    private fun toggleZoomButtonsStatus() {
        toggleZoomInButtonStatus()
        toggleZoomOutButtonStatus()
    }

    private fun toggleZoomInButtonStatus() {
        when (adapter?.getFontControl()!!.getCurrentAyaOffset() < MAX_ZOOM_LEVEL) {
            true -> btnZoomIn.isEnabled = true
            false -> btnZoomIn.isEnabled = false
        }
    }

    private fun toggleZoomOutButtonStatus() {
        when (adapter!!.getFontControl().getCurrentAyaOffset() > 0) {
            true -> btnZoomOut.isEnabled = true
            false -> btnZoomOut.isEnabled = false
        }
    }

    override fun updateSelection(id: String) {
        val selectionControl =
            adapter!!.getSurahListAdapterProvider().getAdapter().getViewHolderSelectionControl()
        selectionControl.setSelectedId(id)
        selectionControl.toggleSelectionVisibilityForAll()
    }

    override fun initPagingProperties() {
        mHasMoreData = true
        mPageNo = 1
    }

    override fun loadNextPage() {
        mPageNo++
        loadData()
    }

    fun loadData() {
        if (mPageNo == 1) {
            hideExistingUI()
            model.getSurahDetailsById(mSurahId!!)
            model.getAyahBySurahId(mSurahId!!, "$mPageNo")
        } else {
            model.getAyahBySurahId(mSurahId!!, "$mPageNo")
        }
    }

    override fun hasMoreData() = mHasMoreData

    fun updateUI() {
        if (surahLoadingComplete && ayahLoadingComplete) {

            checkIsCurrentSurahFavByUser()
            if (mPageNo == 1) {
                inflateMiniPlayerWithSelectedSurah()
                prepareMiniPlayerControls()
                if (adapter == null) {
                    rvSurahDetails.itemAnimator = null
                    rvSurahDetails.layoutManager = LinearLayoutManager(context)
                    adapter = SurahDetailsAdapter(
                        mDetailsCallBack,
                        this,
                        surahDetails = surahDetails,
                        ayatList = ayatList!!,
                        pagingViewCallBack = this@SurahDetailsFragment,
                        pageReloadCallBack = this@SurahDetailsFragment,
                        playPauseFavControl = this
                    )
                    rvSurahDetails.adapter = adapter
                } else {
                    adapter?.updateSurahDetails(surahDetails)
                    ayatList?.let {
                        adapter?.updateAyahList(it)
                    }
                }

                prepareZoomControls()
                toggleZoomButtonsStatus()


                layoutMiniPlayer.handleClickEvent {

                    mDetailsCallBack?.addFragmentToStackAndShow(
                        FragmentProvider.getFragmentByName(
                            name = PAGE_FULL_PLAYER,
                            detailsActivityCallBack = mDetailsCallBack
                        )!!
                    )
                }

            } else {
                if (ayatList == null) {
                    mHasMoreData = false
                    adapter?.hideFooter()

                } else {
                    adapter?.addItemToList(ayatList!!)
                }
            }

        }

    }

    fun hideExistingUI() {
        layoutZoomControl.visibility = GONE
        layoutMiniPlayer.visibility = GONE
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        rvSurahDetails.visibility = INVISIBLE
    }

    fun showUI() {
        layoutZoomControl.visibility = VISIBLE
        rvSurahDetails.visibility = VISIBLE
    }

    fun checkIsCurrentSurahFavByUser() {
        model.getIsSurahFavourtie(SurahListControl.getSelectedSurahId()!!)
    }

    private fun prepareMiniPlayerControls() {
        layoutMiniPlayer?.let {

            btnPrev.handleClickEvent {
                SurahListControl.decrementCurrentIndex()
                reloadPage()
                AudioPlayerService.executePlayerCommand(PREV_COMMAND)

            }
            btnNext.handleClickEvent {
                SurahListControl.incrementCurrentIndex()
                reloadPage()
                AudioPlayerService.executePlayerCommand(NEXT_COMMAND)

            }
            btnPlayPause.handleClickEvent {

                if (SurahListControl.surahList != null) {

                    if (SurahListControl.surahList!!.size > 0) {
                        when (AudioPlayerService.isServiceRunning) {
                            null, false -> {
                                AudioPlayerServiceInstanceControl.startService(
                                    requireContext(),
                                    SURAH_LIST_TYPE,
                                    SurahListControl.curIndex!!,
                                    SurahListControl.surahList!!
                                )
                            }
                            true -> {
                                when (AudioManager.PlayerControl.getIsNotPaused()) {
                                    true -> {
                                        AudioPlayerService.executePlayerCommand(PAUSE_COMMAND)

                                    }
                                    false -> {
                                        AudioPlayerService.executePlayerCommand(RESUME_COMMAND)

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    override fun inflateMiniPlayerWithSelectedSurah() {

        if (AudioPlayerService?.isServiceRunning ?: false && AudioManager.PlayListControl?.getPlayListType() == SURAH_LIST_TYPE) {
            AudioManager.PlayListControl?.getCurrentSurah()?.let {

                val surah = it
                tvSurahName.text = surah.name
                tvAyahNum.text = surah.ayahCountWithPrefix

                layoutMiniPlayer?.let {
                    btnPrev.isEnabled = SurahListControl.curIndex!! > 0
                    btnNext.isEnabled =
                        SurahListControl!!.curIndex!! < SurahListControl.surahList!!.size - 1

                    exoProgressMini.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {

                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            var seekPos = seekBar?.progress?.toLong()
                            AudioManager.getAudioPlayer()
                                ?.seekTo(seekPos ?: 0)

                        }

                    })
                }
            }
        }


    }

    /* Reloads page only if Selected Surah is different from current surah
    * otherwise just check for favourite status update*/
    override fun reloadPage() {
        var selectedSurahId = SurahListControl.getSelectedSurahId()
        if (selectedSurahId != mSurahId) {
            mHasMoreData = true
            mPageNo = 1
            mSurahId = selectedSurahId
            loadData()
        } else {
            checkIsCurrentSurahFavByUser()
        }

    }

    override fun reloadDetailsWithUpdatedSurahIndex(id: String) {
        SurahListControl.updateSelectedIndex(id)
        reloadPage()
    }

    override fun updateMiniPlayerPlayPauseButton(isPlaying: Boolean) {
        layoutMiniPlayer?.let {
            when (isPlaying) {
                true -> btnPlayPause.setImageResource(R.drawable.ic_pause_filled_enabled)
                false -> btnPlayPause.setImageResource(R.drawable.ic_play_filled_enabled)
            }
        }
    }

    override fun updateMiniPlayerTotalDuration(ms: Long) {
        layoutMiniPlayer?.let {
            exoProgressMini.max = ms.toInt()
            tvDuration.setText(TimeFormtter.getDurationFromMsByLocale(ms))
        }
    }

    override fun updateMiniPlayerCurrentDuration(ms: Long) {
        Log.i("CurTIMEMS", "Current Time ms:$ms -${TimeFormtter.getDurationFromMsByLocale(ms)}")
        layoutMiniPlayer?.let {
           exoProgressMini.progress = ms.toInt()
            tvCurrentTime.setText(TimeFormtter.getDurationFromMsByLocale(ms))
        }
    }

    override fun cleanUpUI() {
        updateMiniPlayerCurrentDuration(0L)
        updateMiniPlayerPlayPauseButton(false)
        toggleMiniPlayerVisibility(false)
        SurahDetailsHeaderPlayStatControl?.updatePlayStat()

    }

    override fun toggleMiniPlayerVisibility(show: Boolean) {
        Log.i("VISIBILITY", "$show")
        when (show) {
            true -> layoutMiniPlayer.visibility = VISIBLE
            false -> layoutMiniPlayer.visibility = GONE
        }

    }


    override fun handlePlayPuase(id: String) {
        //visible surah is playing or paused
        if (SurahListControl.surahList != null) {
            if (SurahListControl.surahList!!.size > 0) {
                var currentSurahId = "-1"
                try {
                    currentSurahId =
                        AudioManager.PlayListControl.getCurrentSurah()?.id
                            ?: "-1"
                } catch (e: Exception) {

                }
                if (currentSurahId == id) {
                    when (AudioPlayerService.isServiceRunning) {
                        null, false -> {
                            AudioPlayerServiceInstanceControl.startService(
                                Noor.appContext,
                                SURAH_LIST_TYPE,
                                SurahListControl.curIndex!!,
                                SurahListControl.surahList!!
                            )
                        }
                        true -> {
                            when (AudioManager.PlayerControl.getIsNotPaused()) {
                                true -> {
                                    AudioPlayerService.executePlayerCommand(PAUSE_COMMAND)
                                }
                                false -> {
                                    AudioPlayerService.executePlayerCommand(RESUME_COMMAND)
                                }
                            }
                        }

                    }
                }//different surah
                else {
                    AudioPlayerServiceInstanceControl.startService(
                        requireContext(),
                        SURAH_LIST_TYPE,
                        SurahListControl.curIndex!!,
                        SurahListControl.surahList!!
                    )
                }
            }

        }

    }

    override fun handleFavAction() {
        surahDetails?.let {
            when (it.isSurahFavByThisUser) {
                true -> model.unFavouriteSurah(it.id)
                false -> model.favouriteSurah(it.id)
            }
        }
    }


}

interface PageReloadCallBack {
    fun reloadPage()
}

interface SurahDetailsAudioPlayerCallBack {
    fun reloadDetailsWithUpdatedSurahIndex(id: String)
    fun updateMiniPlayerPlayPauseButton(isPlaying: Boolean)
    fun updateMiniPlayerTotalDuration(ms: Long)
    fun updateMiniPlayerCurrentDuration(ms: Long)
    fun cleanUpUI()
    fun toggleMiniPlayerVisibility(show: Boolean)
    fun inflateMiniPlayerWithSelectedSurah()
}

interface PlayPauseFavControl {
    fun handlePlayPuase(id: String)
    fun handleFavAction()
}

@SuppressLint("StaticFieldLeak")
object SurahDetailsHeaderPlayStatControl {
    var surahDetailsBinding: View? = null
    fun attatchHeaderLayout(binding: View) {
        surahDetailsBinding = binding
    }

    fun detatchLayout() {
        surahDetailsBinding = null
    }

    @SuppressLint("StaticFieldLeak")
    fun updatePlayStat() {
        surahDetailsBinding?.let {
            when (AudioPlayerService.isCurrentSurahPlaying(it.tag.toString())) {
                true -> {
                    it?.findViewById<ImageView>(R.id.btnPlayPause)?.setImageResource(R.drawable.ic_pause_filled_enabled)
                    it?.findViewById<TextView>(R.id.textViewNormal4)?.setText(it?.context?.resources?.getText(R.string.pause_it))
                }
                false -> {
                    it?.findViewById<ImageView>(R.id.btnPlayPause)?.setImageResource(R.drawable.ic_play_filled_enabled)
                    it?.findViewById<TextView>(R.id.textViewNormal4)?.setText(
                        surahDetailsBinding?.context?.resources?.getText(
                            R.string.play_it
                        )
                    )
                }
            }
        }

    }


}
