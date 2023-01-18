package com.gakk.noorlibrary.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.audioPlayer.AudioManager
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentSurahDetailsBinding
import com.gakk.noorlibrary.databinding.LayoutSurahDetailsHeaderBinding
import com.gakk.noorlibrary.model.quran.surahDetail.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.adapter.SurahDetailsAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.QuranViewModel
import kotlinx.coroutines.*
import java.io.Serializable


private const val ARG_SURAH_ID = "surahId"
private const val MAX_ZOOM_LEVEL = 10
private const val ARG_SURAH_LIST = "surahList"

internal class SurahDetailsFragment : Fragment(), SurahDetailsCallBack, PagingViewCallBack,
    PageReloadCallBack, SurahDetailsAudioPlayerCallBack, PlayPauseFavControl {

    private var mSurahId: String? = null
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentSurahDetailsBinding
    private var adapter: SurahDetailsAdapter? = null

    private lateinit var model: QuranViewModel
    private lateinit var repository: RestRepository

    private var mPageNo: Int = 0
    private var mHasMoreData = true

    private var surahLoadingComplete = false
    private var ayahLoadingComplete = false

    private var surahDetails: Data? = null
    private var ayatList: MutableList<com.gakk.noorlibrary.model.quran.ayah.Data>? = null

    private var favAction: () -> Unit = { mSurahId?.let { model.favouriteSurah(it) } }
    private var unFavAction: () -> Unit = { mSurahId?.let { model.unFavouriteSurah(it) } }


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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_surah_details, container, false)


        return binding.root
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


            initPagingProperties()
            loadData()

            model.surahDetailsResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        surahDetails = it.data?.data
                        surahLoadingComplete = true
                        updateUI()


                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = GONE
                        binding.noInternetLayout.root.visibility = VISIBLE
                    }
                    Status.LOADING -> {
                        if (mPageNo == 1) {
                            binding.progressLayout.root.visibility = VISIBLE
                        }
                        binding.noInternetLayout.root.visibility = GONE
                    }
                }
            })

            model.isSurahFavouriteResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        binding.progressLayout.root.visibility = GONE
                        surahDetails?.isSurahFavByThisUser = it.data?.data!!
                        adapter?.updateSurahDetails(surahDetails)
                        adapter?.notifyItemChanged(0)
                        updateToolbarForThisFragment()
                        showUI()


                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                        updateToolbarForThisFragment()
                        showUI()
                    }
                    Status.LOADING -> {
                        //binding.progressLayout.root.visibility = VISIBLE

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
                        binding.progressLayout.root.visibility = GONE
                        binding.noInternetLayout.root.visibility = VISIBLE
                    }
                    Status.LOADING -> {
                        if (mPageNo == 1) {
                            binding.progressLayout.root.visibility = VISIBLE
                        }
                        binding.noInternetLayout.root.visibility = GONE
                    }
                }
            })

            model.favouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        surahDetails?.isSurahFavByThisUser = true
                        adapter?.updateSurahDetails(surahDetails)
                        adapter?.notifyItemChanged(0)
                        binding.progressLayout.root.visibility = GONE
                        updateToolbarForThisFragment()
                        var surah = com.gakk.noorlibrary.model.quran.surah.Data(
                            id = surahDetails!!.id,
                            order = surahDetails!!.order,
                            name = surahDetails!!.name
                        )
                        FavouriteSurahObserver.postFavouriteNotification(surah)
                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = GONE
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
                        //EMON
                        binding.progressLayout.root.visibility = GONE
                        updateToolbarForThisFragment()
                        var surah = com.gakk.noorlibrary.model.quran.surah.Data(
                            id = surahDetails!!.id,
                            order = surahDetails!!.order,
                            name = surahDetails!!.name
                        )
                        FavouriteSurahObserver.postUnFavouriteNotification(surah)

                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                }
            })

            binding.noInternetLayout.btnRetry.handleClickEvent {
                loadData()
            }


            // AudioPlayerService.configureServiceScope()
        }



        AudioPlayerService.attatchSurahDetailsCallBack(this)
    }

    override fun onPause() {
        super.onPause()
        // AudioPlayerService.detachSurahDetailsCallBack()
        Log.i("FLC", "OnPause()")
    }

    override fun onResume() {
        super.onResume()
        var isPlaying = com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()
        updateMiniPlayerPlayPauseButton(isPlaying)

        // AudioPlayerService.attatchSurahDetailsCallBack(this)
        Log.i("FLC", "onResume()")
    }

    override fun onDestroy() {
        // AudioPlayerService.detachSurahDetailsCallBack()
        super.onDestroy()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_quran))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
//        setFavButtonIcon(surahDetails!!.isSurahFavByThisUser)
//        setFavButtonAction(surahDetails!!.isSurahFavByThisUser)
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
                   // putSerializable(ARG_DETAILS_CALL_BACK, detailsCallBack)
                    putSerializable(ARG_SURAH_LIST, surahList as Serializable)
                }
            }
    }

    private fun prepareZoomControls() {
        setUpZoomBtnClickEvents()
    }

    private fun setUpZoomBtnClickEvents() {
        binding.layoutZoomControl.btnZoomOut.handleClickEvent {

            adapter?.getFontControl()!!.decrementCurrentAyaOffset()
            toggleZoomButtonsStatus()
            adapter?.getFontControl()!!.updateAllLayouts()
        }
        binding.layoutZoomControl.btnZoomIn.handleClickEvent {

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
            true -> binding.layoutZoomControl.btnZoomIn.isEnabled = true
            false -> binding.layoutZoomControl.btnZoomIn.isEnabled = false
        }
    }

    private fun toggleZoomOutButtonStatus() {
        when (adapter!!.getFontControl().getCurrentAyaOffset() > 0) {
            true -> binding.layoutZoomControl.btnZoomOut.isEnabled = true
            false -> binding.layoutZoomControl.btnZoomOut.isEnabled = false
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
                    binding.rvSurahDetails.itemAnimator = null
                    binding.rvSurahDetails.layoutManager = LinearLayoutManager(context)
                    adapter = SurahDetailsAdapter(
                        mDetailsCallBack,
                        this,
                        surahDetails = surahDetails,
                        ayatList = ayatList!!,
                        pagingViewCallBack = this@SurahDetailsFragment,
                        pageReloadCallBack = this@SurahDetailsFragment,
                        playPauseFavControl = this
                    )
                    binding.rvSurahDetails.adapter = adapter
                } else {
                    adapter?.updateSurahDetails(surahDetails)
                    ayatList?.let {
                        adapter?.updateAyahList(it)
                    }
                }

                prepareZoomControls()
                toggleZoomButtonsStatus()



                binding.layoutMiniPlayer.root.handleClickEvent {

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
        binding.layoutZoomControl.root.visibility = GONE
        binding.layoutMiniPlayer.root.visibility = GONE
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        binding.rvSurahDetails.visibility = INVISIBLE
    }

    fun showUI() {
        binding.layoutZoomControl.root.visibility = VISIBLE
        // binding.layoutMiniPlayer.root.visibility= VISIBLE
        binding.rvSurahDetails.visibility = VISIBLE
    }

    fun checkIsCurrentSurahFavByUser() {
        model.getIsSurahFavourtie(SurahListControl.getSelectedSurahId()!!)
    }

    private fun prepareMiniPlayerControls() {
        binding.layoutMiniPlayer?.let {

            it.btnPrev.handleClickEvent {
                SurahListControl.decrementCurrentIndex()
                reloadPage()
                AudioPlayerService.executePlayerCommand(PREV_COMMAND)

            }
            it.btnNext.handleClickEvent {
                SurahListControl.incrementCurrentIndex()
                reloadPage()
                AudioPlayerService.executePlayerCommand(NEXT_COMMAND)

            }
            it.btnPlayPause.handleClickEvent {

                if (SurahListControl.surahList != null) {

                    if (SurahListControl.surahList!!.size > 0) {
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
                                when (com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
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

        // binding.layoutMiniPlayer.root.visibility= GONE
        if (AudioPlayerService?.isServiceRunning ?: false && com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl?.getPlayListType() == SURAH_LIST_TYPE) {
            com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl?.getCurrentSurah()?.let {
                //  binding.layoutMiniPlayer.root.visibility= VISIBLE
                binding.layoutMiniPlayer.surah = it

                binding.layoutMiniPlayer?.let {
                    it.btnPrev.isEnabled = SurahListControl.curIndex!! > 0
                    it.btnNext.isEnabled =
                        SurahListControl!!.curIndex!! < SurahListControl.surahList!!.size - 1
                    //  it.tvDuration.setText(surahDetails?.duration?.getLocalisedDuration())
                    // it.tvCurrentTime.setText("0:00".getLocalisedDuration())
                    it.exoProgressMini.setOnSeekBarChangeListener(object :
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
                            com.gakk.noorlibrary.audioPlayer.AudioManager.getAudioPlayer()?.seekTo(seekPos ?: 0)

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
        binding.layoutMiniPlayer?.let {
            when (isPlaying) {
                true -> it.btnPlayPause.setImageResource(R.drawable.ic_pause_filled_enabled)
                false -> it.btnPlayPause.setImageResource(R.drawable.ic_play_filled_enabled)
            }
        }
    }

    override fun updateMiniPlayerTotalDuration(ms: Long) {
        binding.layoutMiniPlayer?.let {
            it.exoProgressMini.max = ms.toInt()
            it.tvDuration.setText(TimeFormtter.getDurationFromMsByLocale(ms))
        }
    }

    override fun updateMiniPlayerCurrentDuration(ms: Long) {
        Log.i("CurTIMEMS", "Current Time ms:$ms -${TimeFormtter.getDurationFromMsByLocale(ms)}")
        binding.layoutMiniPlayer?.let {
            it.exoProgressMini.progress = ms.toInt()
            it.tvCurrentTime.setText(TimeFormtter.getDurationFromMsByLocale(ms))
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
            true -> binding.layoutMiniPlayer.root.visibility = VISIBLE
            false -> binding.layoutMiniPlayer.root.visibility = GONE
        }

    }


    override fun handlePlayPuase(id: String) {
        //visible surah is playing or paused
        if (SurahListControl.surahList != null) {
            if (SurahListControl.surahList!!.size > 0) {
                var currentSurahId = "-1"
                try {
                    currentSurahId = com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getCurrentSurah()?.id?:"-1"
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
                            when (com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
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
                        Noor.appContext,
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

object SurahDetailsHeaderPlayStatControl {
    var surahDetailsBinding: LayoutSurahDetailsHeaderBinding? = null
    fun attatchHeaderLayout(binding: LayoutSurahDetailsHeaderBinding) {
        surahDetailsBinding = binding
    }

    fun detatchLayout() {
        surahDetailsBinding = null
    }

    fun updatePlayStat() {
        surahDetailsBinding?.let {
            when (AudioPlayerService.isCurrentSurahPlaying(it.root.tag.toString())) {
                true -> {
                    it?.btnPlayPause?.setImageResource(R.drawable.ic_pause_filled_enabled)
                    it?.textViewNormal4?.setText(it?.root?.context?.resources?.getText(R.string.pause_it))
                }
                false -> {
                    it?.btnPlayPause?.setImageResource(R.drawable.ic_play_filled_enabled)
                    it?.textViewNormal4?.setText(
                        surahDetailsBinding?.root?.context?.resources?.getText(
                            R.string.play_it
                        )
                    )
                }
            }
        }

    }


}
