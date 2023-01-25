package com.gakk.noorlibrary.ui.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.callbacks.SurahDetailsCallBack

import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quran.surahDetail.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.ui.fragments.PageReloadCallBack
import com.gakk.noorlibrary.ui.fragments.PlayPauseFavControl
import com.gakk.noorlibrary.ui.fragments.SurahDetailsHeaderPlayStatControl
import com.gakk.noorlibrary.util.handleClickEvent

const val CELL_HEADER = 0
const val CELL_AYAH = 1
const val CELL_AYAH_FOOTER = 2

internal class SurahDetailsAdapter(
    detailsCallBack: DetailsCallBack?,
    surahDetailsCallBack: SurahDetailsCallBack,
    hideShowSurahListBtn: Boolean = false,
    surahDetails: Data? = null,
    ayatList: MutableList<com.gakk.noorlibrary.model.quran.ayah.Data>? = null,
    pagingViewCallBack: PagingViewCallBack? = null,
    pageReloadCallBack: PageReloadCallBack,
    playPauseFavControl: PlayPauseFavControl
) :
    RecyclerView.Adapter<SurahDetailsAdapter.SurahDetailsViewHolder>() {

    private val surahListAdapterProvider: SurahListAdapterProvider
    private val fontControlSurahDetail: SurahDetailAyahLayoutFontControl
    private val mDetailsCallBack = detailsCallBack
    private val mSurahDetailsCallBack = surahDetailsCallBack


    private var mSurahDetails = surahDetails
    private var mAyahList = ayatList
    private val mPagingViewCallBack = pagingViewCallBack
    private val mPageReloadCallBack = pageReloadCallBack
    private val mPlayPauseFavControl = playPauseFavControl


    init {
        fontControlSurahDetail = SurahDetailAyahLayoutFontControl()
        surahListAdapterProvider =
            SurahListAdapterProvider(mSurahDetailsCallBack, mPageReloadCallBack)

    }

    fun getFontControl() = fontControlSurahDetail
    fun getSurahListAdapterProvider() = surahListAdapterProvider

    inner class SurahDetailsViewHolder : RecyclerView.ViewHolder {

        var headerBinding: View? = null

        constructor(binding: View , layoutSurahDetailsHeaderBinding:String = "") : super(binding) {
            headerBinding = binding

            headerBinding?.findViewById<View>(R.id.surahBasicInfoContainer)?.let {

                it.handleClickEvent {
                    mDetailsCallBack?.showDialogWithActionAndParam(
                        dialogType = DialogType.SurahListDialog,
                        pageReloadCallBack = mPageReloadCallBack,
                        surahListAdapter = surahListAdapterProvider.getAdapter()
                    )

                }
            }

            headerBinding?.findViewById<View>(R.id.CLPlay)?.handleClickEvent {
                mPlayPauseFavControl.handlePlayPuase(mSurahDetails!!.id)

            }

            headerBinding?.findViewById<View>(R.id.CLFavourite)?.handleClickEvent {
                mPlayPauseFavControl.handleFavAction()
            }


        }


        var ayahBinding: View? = null
        var tag: Int? = null

        constructor(binding: View, LayoutSurahDetailsAyahBinding:Int = 0) : super(binding) {
            ayahBinding = binding
            ayahBinding?.findViewById<View>(R.id.btnMore)?.let {

                it.handleClickEvent {
                    mDetailsCallBack?.showDialogWithActionAndParam(
                        dialogType = DialogType.AyahActionListDialog,
                        numberAyah = binding.findViewById<TextView>(R.id.tvAyahNum).text.toString(),
                        textAyah = binding.findViewById<TextView>(R.id.tvAyaNative).text.toString()
                    )
                }
            }
        }

        fun setLayoutTag(layoutTag: Int?) {
            tag?.let {
                fontControlSurahDetail.getAyaLayoutMap().remove(it)
            }
            tag = layoutTag
            tag?.let {
                fontControlSurahDetail.getAyaLayoutMap()[it] = ayahBinding
            }
        }

        var footerBinding: View? = null

        constructor(binding: View, layoutFooterBinding: Float) : super(binding) {
            footerBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahDetailsViewHolder {
        return when (viewType) {
            CELL_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_surah_details_header,
                    parent,
                    false
                )

                SurahDetailsViewHolder(view,"")
            }
            CELL_AYAH_FOOTER -> {

               val viewx =  LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,
                    parent,
                    false)
                SurahDetailsViewHolder(viewx,0.0f)
            }
            else -> {
                val binding: View =  LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_surah_details_ayah,
                    parent,
                    false
                )
                SurahDetailsViewHolder(binding,0)
            }
        }
    }

    override fun onBindViewHolder(holder: SurahDetailsViewHolder, position: Int) {

        holder.headerBinding?.let { layout ->
            //layout.surah = mSurahDetails

            layout.findViewById<TextView>(R.id.tvSurahName).text = mSurahDetails?.name
            layout.findViewById<TextView>(R.id.tvBanglaNamePlusAyaCountPlusLocation).text = mSurahDetails?.surahBasicInfo
            when (mSurahDetails?.origin?.trim()) {
                "Meccan", "মাক্কী" -> {
                    val item = ImageFromOnline("bg_makkah.png")

                    Noor?.appContext?.let {
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
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(holder.headerBinding?.findViewById(R.id.imgBgLocation)!!)
                    }

                }
                else -> {
                    val item = ImageFromOnline("bg_madinah.png")

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
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(holder?.headerBinding?.findViewById(R.id.imgBgLocation)!!)
                    }

                }
            }

            layout?.let {
                it.tag = mSurahDetails?.id ?: "-1"
                Log.i("TAG_UPDATE", mSurahDetails?.id ?: "-1")
            }

            SurahDetailsHeaderPlayStatControl.attatchHeaderLayout(layout)


            mSurahDetails?.let {
                layout.findViewById<ImageView>(R.id.btnPlayPause).setImageResource(R.drawable.ic_play_filled_enabled)
                layout.findViewById<TextView>(R.id.textViewNormal4).setText(layout.context.resources.getText(R.string.play_it))
                when (AudioPlayerService.isCurrentSurahPlaying(it.id)) {
                    true -> {
                        layout.findViewById<ImageView>(R.id.btnPlayPause).setImageResource(R.drawable.ic_pause_filled_enabled)
                        layout.findViewById<TextView>(R.id.textViewNormal4).setText(layout.context.resources.getText(R.string.pause_it))
                    }
                    false -> {
                        layout.findViewById<ImageView>(R.id.btnPlayPause).setImageResource(R.drawable.ic_play_filled_enabled)
                        layout.findViewById<TextView>(R.id.textViewNormal4).setText(layout.context.resources.getText(R.string.play_it))
                    }
                }

                when (it.isSurahFavByThisUser) {
                    true -> {
                        layout.findViewById<ImageView>(R.id.btnFav).setImageResource(R.drawable.ic_favorite_filled)
                        layout.findViewById<TextView>(R.id.textViewNormal5).setTextColor(layout.context.resources.getColor(R.color.colorPrimary))
                    }
                    false -> {
                        layout.findViewById<ImageView>(R.id.btnFav).setImageResource(R.drawable.ic_favorite)
                        layout.findViewById<TextView>(R.id.textViewNormal5).setTextColor(layout.context.resources.getColor(R.color.txt_color_black))
                    }
                }
            }


        }
        holder?.ayahBinding?.let {
            mAyahList?.let { list ->
                val daa = list.get(position - 1)
                it.findViewById<TextView>(R.id.tvAyahNum).text = daa.ayahNumber
                it.findViewById<TextView>(R.id.tvAyaArabic).text = daa.textInArabic
                it.findViewById<TextView>(R.id.tvAyaNative).text = daa.text
            }
            fontControlSurahDetail.updateFontSizeForAyahBinding(it)
        }
        holder?.footerBinding?.let {
            when (mPagingViewCallBack?.hasMoreData()) {
                true -> {
                    mPagingViewCallBack?.loadNextPage()
                    it.visibility = VISIBLE
                }
                else -> it.visibility = GONE
            }
        }

        holder?.let {
            if (position > 0) {
                holder.setLayoutTag(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> CELL_HEADER
            mAyahList!!.size + 1 -> CELL_AYAH_FOOTER
            else -> CELL_AYAH
        }
    }

    override fun getItemCount(): Int {
        return 2 + (mAyahList?.size ?: 0)
    }

    fun hideFooter() {
        notifyItemChanged(mAyahList!!.size + 1)
    }

    fun addItemToList(list: MutableList<com.gakk.noorlibrary.model.quran.ayah.Data>) {
        var startPos = mAyahList!!.size + 1
        mAyahList?.addAll(list)
        notifyItemChanged(startPos, list.size)
    }

    fun updateSurahDetails(surahDetails: Data?) {
        this.mSurahDetails = surahDetails
    }

    fun updateAyahList(list: MutableList<com.gakk.noorlibrary.model.quran.ayah.Data>) {
        mAyahList = list
        notifyDataSetChanged()
    }


}

class SurahDetailAyahLayoutFontControl {

    private var ayahLayoutMap: HashMap<Int, View?>
    private var ayaFontSizeOffSet: Int = 0

    init {
        ayahLayoutMap = HashMap()
        ayaFontSizeOffSet = 0
    }

    fun updateAllLayouts() {

        for (layout in ayahLayoutMap) {
            layout.value?.let {
                updateFontSizeForAyahBinding(it)

            }
        }
    }

    fun updateFontSizeForAyahBinding(binding: View) {

        binding?.findViewById<TextView>(R.id.tvAyahNum)?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, (16.0f + getCurrentAyaOffset()))
        }
        binding?.findViewById<TextView>(R.id.tvAyaArabic)?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, (16.0f + getCurrentAyaOffset()))
        }
        binding?.findViewById<TextView>(R.id.tvAyaNative)?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, (14.0f + getCurrentAyaOffset()))
        }
        binding?.findViewById<TextView>(R.id.tvAyaTranslation)?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, (14.0f + getCurrentAyaOffset()))
        }
    }

    fun getAyaLayoutMap() = ayahLayoutMap

    fun getCurrentAyaOffset(): Int {
        return ayaFontSizeOffSet
    }

    fun incrementCurrentAyaOffset() {
        ayaFontSizeOffSet++
    }

    fun decrementCurrentAyaOffset() {
        ayaFontSizeOffSet--
    }

}

class SurahListAdapterProvider(
    surahDetailsCallBack: SurahDetailsCallBack?,
    pageReloadCallBack: PageReloadCallBack
) {
    private val msurahDetailsCallBack: SurahDetailsCallBack
    private val mPageReloadCallBack = pageReloadCallBack

    init {
        msurahDetailsCallBack = surahDetailsCallBack!!
    }

    fun getAdapter() = SurahListAdapter(msurahDetailsCallBack)
}


