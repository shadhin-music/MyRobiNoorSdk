package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentSurahFullPlayerBinding
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.adapter.FullPlayerAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.QuranViewModel
import kotlinx.coroutines.launch


internal class SurahFullPlayerFragment : Fragment(), SurahFullPlayerAudioPlayerCallBack {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentSurahFullPlayerBinding
    private lateinit var surahList: MutableList<Data>
    private var adapter: FullPlayerAdapter? = null

    private lateinit var model: QuranViewModel
    private lateinit var repository: RestRepository

    private var favAction: () -> Unit =
        {
            SurahListControl.getSelectedSurahId()?.let {
                Log.i("MFAV", "FAV-$it")
                model.favouriteSurah(it)
            }
        }
    private var unFavAction: () -> Unit =
        {
            SurahListControl.getSelectedSurahId()?.let {
                Log.i("MFAV", "UNFAV-$it")
                model.unFavouriteSurah(it)
            }
        }
    private var itemClickAction: (Int, String) -> Unit = { index: Int, id: String ->
        SurahListControl.updateSelectedIndex(id)
        com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.setCurrentIndex(
            SurahListControl?.curIndex ?: 0
        )
        hideUI()
        populateUI()
        model.getIsSurahFavourtie(id)
        handlePlayPauseUponItemClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_surah_full_player, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AudioPlayerService.attatchSurahFullPlayerCallBack(this)

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@SurahFullPlayerFragment,
                QuranViewModel.FACTORY(repository)
            ).get(QuranViewModel::class.java)

            hideUI()
            populateUI()
            model.getIsSurahFavourtie(SurahListControl.getSelectedSurahId()!!)
            model.favouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        SurahListControl.getSelectedSurah().isSurahFavByThisUser = true
                        binding.progressLayout.root.visibility = View.GONE
                        updateToolbarForThisFragment()
                        var surah = Data(
                            id = SurahListControl.getSelectedSurah()!!.id,
                            order = SurahListControl.getSelectedSurah()!!.order,
                            name = SurahListControl.getSelectedSurah()!!.name
                        )
                        FavouriteSurahObserver.postFavouriteNotification(surah)
                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                }
            })

            model.unFavouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        SurahListControl.getSelectedSurah().isSurahFavByThisUser = false
                        binding.progressLayout.root.visibility = View.GONE
                        updateToolbarForThisFragment()
                        var surah = Data(
                            id = SurahListControl.getSelectedSurah()!!.id,
                            order = SurahListControl.getSelectedSurah()!!.order,
                            name = SurahListControl.getSelectedSurah()!!.name
                        )
                        FavouriteSurahObserver.postUnFavouriteNotification(surah)

                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                }
            })


            binding.playerControl.let {

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
                        com.gakk.noorlibrary.audioPlayer.AudioManager.getAudioPlayer()
                            ?.seekTo(seekPos ?: 0)

                    }

                })

                it.btnPrev.handleClickEvent {
                    decrementSelectedIndex()
                    loadUIWithUpdatedIndex()
                    AudioPlayerService.executePlayerCommand(PREV_COMMAND)
                }

                it.btnNext.handleClickEvent {
                    incrementSelectedIndex()
                    loadUIWithUpdatedIndex()
                    AudioPlayerService.executePlayerCommand(NEXT_COMMAND)
                }
                it.btnPlayPause.handleClickEvent {
                    handlePlayPause()
                }
            }

            model.isSurahFavouriteResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {

                        binding.progressLayout.root.visibility = View.GONE
                        SurahListControl.getSelectedSurah().isSurahFavByThisUser = it.data!!.data
                        updateToolbarForThisFragment()
                        showUI()
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                        updateToolbarForThisFragment()
                        showUI()
                    }
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE

                    }
                }
            })

        }
    }

    override fun onDestroy() {
        AudioPlayerService.detachSurahFullPlayerCallBack()
        super.onDestroy()
    }

    fun populateUI() {
        surahList = getPlayableSurahList()

        if (adapter == null) {
            adapter = FullPlayerAdapter(surahList, itemClickAction)
            binding.rvFullPlayer.adapter = adapter
        } else {
            adapter?.updateSurahList(surahList)
            adapter?.notifyDataSetChanged()
        }
        inflateplayerControl()
        if (AudioPlayerService?.isServiceRunning == true && com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getPlayListType() == SURAH_LIST_TYPE && com.gakk.noorlibrary.audioPlayer.AudioManager.PlayerControl.getIsNotPaused()) {
            updatePlayerControlPlayPauseButton(true)
        } else {
            updatePlayerControlPlayPauseButton(false)
        }

        if (AudioPlayerService?.isServiceRunning == true && com.gakk.noorlibrary.audioPlayer.AudioManager.PlayListControl.getPlayListType() == SURAH_LIST_TYPE) {
            togglePlayerControlVisibility(true)
        } else {
            togglePlayerControlVisibility(false)
        }

    }


    fun getPlayableSurahList(): MutableList<Data> {
        var list: MutableList<Data> = mutableListOf()
        SurahListControl.surahList?.let {
            for (i in SurahListControl.curIndex!! until it.size) {
                list.add(it[i])
            }
        }
        return list
    }


    fun inflateplayerControl() {
        var selectedSurah = SurahListControl.getSelectedSurah()
        binding.playerControl?.let {
            it.tvCurrentTime.setText("00".getLocalisedDuration())
            it.tvDuration.setText(selectedSurah.durationLocalised)
            it.btnPrev.isEnabled = SurahListControl.curIndex!! > 0
            it.btnNext.isEnabled = SurahListControl.curIndex!! < surahList.size - 1
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SurahFullPlayerFragment()
    }

    override fun incrementSelectedIndex() {
        SurahListControl.curIndex = SurahListControl.curIndex!! + 1
        Log.i("SELECTED_INDEX_", SurahListControl.curIndex.toString())
    }

    override fun decrementSelectedIndex() {
        SurahListControl.curIndex = SurahListControl.curIndex!! - 1
        Log.i("SELECTED_INDEX", SurahListControl.curIndex.toString())
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_quran))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true)

        setFavButtonIcon(SurahListControl.getSelectedSurah()!!.isSurahFavByThisUser)
        setFavButtonAction(SurahListControl.getSelectedSurah()!!.isSurahFavByThisUser)
        mDetailsCallBack?.setOrUpdateActionButtonTag(SHARE, ActionButtonType.TypeTwo)

    }

    private fun setFavButtonIcon(isFav: Boolean) {
        when (isFav) {
            true -> mDetailsCallBack?.setOrUpdateActionButtonTag(
                FAV_FILLED,
                ActionButtonType.TypeOne
            )
            false -> mDetailsCallBack?.setOrUpdateActionButtonTag(FAV, ActionButtonType.TypeOne)
        }

    }

    private fun setFavButtonAction(isFav: Boolean) {
        when (isFav) {
            true -> mDetailsCallBack?.setActionOfActionButton(unFavAction, ActionButtonType.TypeOne)
            false -> mDetailsCallBack?.setActionOfActionButton(favAction, ActionButtonType.TypeOne)
        }
    }

    private fun showUI() {
        binding.playerControl.root.visibility = VISIBLE
        binding.rvFullPlayer.visibility = VISIBLE
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true)

    }

    private fun hideUI() {
        binding.playerControl.root.visibility = INVISIBLE
        binding.rvFullPlayer.visibility = INVISIBLE
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
    }

    override fun loadUIWithUpdatedIndex() {
        hideUI()
        populateUI()
        model.getIsSurahFavourtie(SurahListControl.getSelectedSurahId())
    }

    fun handlePlayPause() {
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

    fun handlePlayPauseUponItemClick() {
        if (SurahListControl.surahList != null) {
            if (SurahListControl.surahList!!.size > 0) {
                AudioPlayerServiceInstanceControl.startService(
                    requireContext(),
                    SURAH_LIST_TYPE, SurahListControl.curIndex!!, SurahListControl.surahList!!
                )
            }
        }


    }

    override fun updatePlayerControlPlayPauseButton(isPlaying: Boolean) {
        binding.playerControl?.let {
            when (isPlaying) {
                true -> it.btnPlayPause.setImageResource(R.drawable.ic_pause_filled_enabled)
                false -> it.btnPlayPause.setImageResource(R.drawable.ic_play_filled_enabled)
            }
        }
    }

    override fun updatePlayerControlTotalDuration(ms: Long) {
        binding.playerControl.exoProgressMini.max = ms.toInt()
        binding.playerControl.tvDuration.setText(TimeFormtter.getDurationFromMsByLocale(ms))
    }

    override fun updatePlayerControlCurrentDuration(ms: Long) {
        binding.playerControl.exoProgressMini.progress = ms.toInt()
        binding.playerControl.tvCurrentTime.setText(TimeFormtter.getDurationFromMsByLocale(ms))
    }


    override fun cleanUpUI() {
        updatePlayerControlCurrentDuration(0)
        updatePlayerControlPlayPauseButton(false)
        togglePlayerControlVisibility(false)
    }

    override fun togglePlayerControlVisibility(show: Boolean) {
        Log.i("VISIBILITY", "$show")
        when (show) {
            true -> binding.playerControl.root.visibility = VISIBLE
            false -> binding.playerControl.root.visibility = View.GONE
        }
    }


}

interface SurahFullPlayerAudioPlayerCallBack {
    fun loadUIWithUpdatedIndex()
    fun incrementSelectedIndex()
    fun decrementSelectedIndex()
    fun updatePlayerControlPlayPauseButton(isPlaying: Boolean)
    fun updatePlayerControlTotalDuration(ms: Long)
    fun updatePlayerControlCurrentDuration(ms: Long)
    fun cleanUpUI()
    fun togglePlayerControlVisibility(show: Boolean)

}