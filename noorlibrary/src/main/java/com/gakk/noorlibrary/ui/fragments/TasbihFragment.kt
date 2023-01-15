package com.gakk.noorlibrary.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.DialogTasbihResetBinding
import com.gakk.noorlibrary.databinding.FragmentTasbihBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.tasbih.TasbihModel
import com.gakk.noorlibrary.ui.adapter.TasbihAdapter
import com.gakk.noorlibrary.ui.adapter.TasbihHistoryAdapter
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

private const val ARG_VIEW_CLICKED_INDEX = "viewClickedIndex"
private const val ARG_BUTTON_CLICKED_INDEX = "buttonClickedIndex"
private const val ARG_ITEM_COUNT = "itemCount"
private const val ARG_TIMES = "items"

class TasbihFragment : Fragment(), CountControl, PressListener {

    private lateinit var binding: FragmentTasbihBinding
    private var mCallback: DetailsCallBack? = null
    private var localcount = 0
    private var totalCount = 0
    private var round = 0
    private var userSelectCount = 33
    private var sound: Boolean = true
    private var mFlag1 = 0
    private lateinit var mp: MediaPlayer
    private lateinit var dua: Array<String>
    lateinit var model: List<TasbihModel>
    var selectedItem = "0"
    private lateinit var historyAdapter: TasbihHistoryAdapter
    private var duaIndex: Array<String> = arrayOf("0", "1", "2", "3", "4", "5", "6", "7")
    private var viewClickedIndex: Int = 0
    private var buttonClickedIndex: Int = 0

    companion object {

        @JvmStatic
        fun newInstance(
            viewClickedIndex: Int = 0,
            buttonClickedIndex: Int = 0,
            itemCount: Int = 0,
            times: Int = 33
        ) =
            TasbihFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_VIEW_CLICKED_INDEX, viewClickedIndex)
                    putInt(ARG_BUTTON_CLICKED_INDEX, buttonClickedIndex)
                    putInt(ARG_ITEM_COUNT, itemCount)
                    putInt(ARG_TIMES, times)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewClickedIndex = it.getInt(ARG_VIEW_CLICKED_INDEX)
            buttonClickedIndex = it.getInt(ARG_BUTTON_CLICKED_INDEX)
            localcount = it.getInt(ARG_ITEM_COUNT)
            userSelectCount = it.getInt(ARG_TIMES)
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tasbih, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        binding.item = ImageFromOnline("ic_tasbeeh_background.png")

        binding.tasbihCountIV.handleClickEvent {
            localcount++

            if (localcount > userSelectCount) {
                if (localcount < userSelectCount + 2) {
                    localcount = 0
                    round++
                    binding.tvCountRound.setText(
                        TimeFormtter.getNumberByLocale(
                            TimeFormtter.getNumber(
                                round
                            )!!
                        )
                    )
                    Toast.makeText(context, getString(R.string.count_complete), Toast.LENGTH_LONG)
                        .show()
                }
            } else {

                onPressed()
                binding.tvCount.setText(
                    TimeFormtter.getNumberByLocale(
                        TimeFormtter.getNumber(
                            localcount
                        )!!
                    )
                )
                binding.progressBarCircle.max = userSelectCount
                binding.progressBarCircle.progress = localcount

                if (totalCount >= 0) {
                    totalCount++
                } else {
                    totalCount = 1
                }
            }


            if (totalCount >= 0) {
                binding.tvCountTotal.setText(
                    TimeFormtter.getNumberByLocale(
                        TimeFormtter.getNumber(
                            totalCount
                        )!!
                    )
                )
            } else {
                binding.tvCountTotal.setText(getString(R.string.text_zero))
            }

            AppPreference.totalCount = totalCount

            handleSound()
        }

        binding.onOffSoundIV.handleClickEvent {
            soundButtonClick()
        }

        binding.resetAllBtn.handleClickEvent {
            showResetDialog()

        }
    }

    fun initUI() {

        mCallback?.setToolBarTitle(getString(R.string.cat_tasbih))

        dua =
            context?.resources?.getStringArray(R.array.tasbih_duas) as Array<String>

        binding.rvTasbihItem.adapter =
            TasbihAdapter(dua, this, viewClickedIndex, buttonClickedIndex)
        selectedItem = viewClickedIndex.toString()

        binding.rvTasbihItem.scrollToPosition(viewClickedIndex)

        model = getModels(duaIndex, dua)

        historyAdapter = TasbihHistoryAdapter(model)
        binding.rvHistory.adapter = historyAdapter

        mp = MediaPlayer.create(context, R.raw.second)
        sound = AppPreference.soundflag
        totalCount = AppPreference.totalCount

        binding.progressBarCircle.progress = 0

        binding.tvCount.setText(
            TimeFormtter.getNumberByLocale(
                TimeFormtter.getNumber(
                    localcount
                )!!
            )
        )
        Log.e("Times", "ss" + userSelectCount.toString())
        binding.tvTimes.text =
            "/" + TimeFormtter.getNumberByLocale(userSelectCount.toString()) + " " + getString(R.string.txt_times)
        binding.tvCountRound.text = getString(R.string.text_zero)

        when (sound) {
            true -> {
                binding.onOffSoundIV.setImageResource(R.drawable.ic_btn_sound)
            }
            false -> {
                binding.onOffSoundIV.setImageResource(R.drawable.ic_btn_sound_off)
            }
        }

        if (totalCount > 0) {
            binding.tvCountTotal.setText(
                TimeFormtter.getNumberByLocale(
                    TimeFormtter.getNumber(
                        totalCount
                    )!!
                )
            )
        } else {
            binding.tvCountTotal.setText(getString(R.string.text_zero))
        }
    }

    fun handleSound() {
        if (sound) {
            if (mFlag1 != 0) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop()
                        mp.release()
                        mp = MediaPlayer.create(context, R.raw.second)
                    }
                    mp.start()
                    if (sound) {
                        mp.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                mp.start()
            }
        }
        mFlag1 = 1
    }

    fun soundButtonClick() {
        sound = AppPreference.soundflag
        if (sound) {
            sound = false
            binding.onOffSoundIV.setImageResource(R.drawable.ic_btn_sound_off)
            AppPreference.soundflag = false
        } else {
            sound = true
            binding.onOffSoundIV.setImageResource(R.drawable.ic_btn_sound)
            AppPreference.soundflag = true
        }
    }

    override fun getUserCount(count: Int) {
        localcount = 0
        userSelectCount = count
        binding.tvTimes.text =
            "/" + TimeFormtter.getNumberByLocale(count.toString()) + " " + getString(R.string.txt_times)
        binding.tvCount.text = getString(R.string.text_zero)
        binding.progressBarCircle.progress = 0
    }

    override fun getSelectedItem(name: String) {
        selectedItem = name
    }

    fun showResetDialog() {
        val customDialog =
            MaterialAlertDialogBuilder(
                requireActivity(),
                R.style.MaterialAlertDialog_rounded
            )
        val binding: DialogTasbihResetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.dialog_tasbih_reset,
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

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        binding.imgClose.handleClickEvent {
            alertDialog.dismiss()
        }

        binding.btnTotalCount.handleClickEvent {
            resetTotalCount()
            alertDialog.dismiss()
        }

        binding.btnCurrentCount.handleClickEvent {
            resetCurrentCount()
            alertDialog.dismiss()
        }
    }

    fun resetTotalCount() {
        AppPreference.cleartotalCount()
        clearHistory(duaIndex)
        binding.tvCountTotal.setText(getString(R.string.text_zero))
        binding.tvCount.text = getString(R.string.text_zero)
        binding.tvCountRound.text = getString(R.string.text_zero)
        binding.progressBarCircle.progress = 0
        localcount = 0
        totalCount = 0
    }

    fun resetCurrentCount() {
        binding.tvCount.text = getString(R.string.text_zero)
        binding.progressBarCircle.progress = 0
        localcount = 0
    }

    override fun onPressed() {

        AppPreference.saveTashbihCount(
            AppPreference.loadTashbihCount(selectedItem) + 1,
            selectedItem
        )
        model = getModels(duaIndex, dua)
        binding.rvHistory.adapter = model?.let { TasbihHistoryAdapter(it) }
    }

    private fun getModels(index: Array<String>, ars: Array<String>): List<TasbihModel> {
        val models: MutableList<TasbihModel> = ArrayList()

        for (i in 0..index.size - 1) {
            models.add(
                TasbihModel(
                    i,
                    ars[i],
                    AppPreference.loadTashbihCount(i.toString())
                )
            )
        }
        return models
    }

    private fun clearHistory(ars: Array<String>) {
        for (ar in ars) {
            AppPreference.clearHistoryCount(ar)
        }

        model = getModels(duaIndex, dua)
        binding.rvHistory.adapter = model?.let { TasbihHistoryAdapter(it) }
    }
}


interface PressListener {
    fun onPressed()
}

interface CountControl {
    fun getUserCount(count: Int)
    fun getSelectedItem(name: String)
}