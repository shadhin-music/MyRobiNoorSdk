package com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.DialogHajjTrackerHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.util.handleClickEvent

class HajjTrackerHomeDialogFragment : Fragment() {
    private lateinit var binding: DialogHajjTrackerHomeBinding
    private lateinit var bottomSheetDisplayCallback: BottomSheetDisplay

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomSheetDisplayCallback = context as BottomSheetDisplay
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_hajj_tracker_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.item = ImageFromOnline("ic_tracker_hajj.png")

        binding.ivShare.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(0)
        }

        binding.ivTrack.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(1)
        }
    }
}