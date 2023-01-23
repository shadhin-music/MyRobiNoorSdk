package com.gakk.noorlibrary.ui.fragments.hajj.hajjtracker

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.DialogHajjTrackerHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.util.handleClickEvent

internal class HajjTrackerHomeDialogFragment : Fragment() {

    private lateinit var bottomSheetDisplayCallback: BottomSheetDisplay
    private lateinit var ivShare: AppCompatImageView
    private lateinit var ivTrack: AppCompatImageView
    private lateinit var appCompatImageView16: AppCompatImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomSheetDisplayCallback = context as BottomSheetDisplay
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.dialog_hajj_tracker_home,
            container, false
        )

        ivShare = view.findViewById(R.id.ivShare)
        ivTrack = view.findViewById(R.id.ivTrack)
        appCompatImageView16 = view.findViewById(R.id.appCompatImageView16)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = ImageFromOnline("ic_tracker_hajj.png")

        Noor.appContext?.let {
            Glide.with(it)
                .load(item.fullImageUrl)
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
                .error(R.drawable.place_holder_2_3_ratio)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(appCompatImageView16)
        }

        ivShare.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(0)
        }

        ivTrack.handleClickEvent {
            bottomSheetDisplayCallback.showBottomSheet(1)
        }
    }
}