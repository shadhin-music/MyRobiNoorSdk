package com.gakk.noorlibrary.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutFullPlayerContentBinding
import com.gakk.noorlibrary.databinding.LayoutFullPlayerHeaderBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl


const val HEADER = 0
const val CONTENT = 1

internal class FullPlayerAdapter(surahList: MutableList<Data>, clickAction: (Int, String) -> Unit) :
    RecyclerView.Adapter<FullPlayerAdapter.FullPlayerViewHolder>() {


    var mSurahList: MutableList<Data> = surahList
    var mClickAction = clickAction

    inner class FullPlayerViewHolder(layoutId: Int, layoutView: View) : RecyclerView.ViewHolder(layoutView) {

        var view: View = layoutView
        val layoutTag = layoutId


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullPlayerViewHolder {
        lateinit var view:View


        return when (viewType) {
            HEADER -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_full_player_header,parent,false)

                FullPlayerViewHolder(HEADER,view)
            }

            else -> {

                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_full_player_content,parent,false)

                FullPlayerViewHolder(3,view)
            }
        }

    }

    override fun onBindViewHolder(holder: FullPlayerViewHolder, position: Int) {

        when(holder.layoutTag)
        {
            HEADER ->
            {
                val surah = mSurahList[0]
                val item = ImageFromOnline("bg_quran.png")
                val ivTop = holder.view.findViewById<ImageView>(R.id.ivTop)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                val tvSurahTitle = holder.view.findViewById<AppCompatTextView>(R.id.tvSurahTitle)
                val  tvAyahTxt = holder.view.findViewById<AppCompatTextView>(R.id.tvAyahTxt)

                setImageFromUrl(ivTop,item.fullImageUrl,progressBar)
                tvSurahTitle.text = surah.name
                tvAyahTxt.text = surah.ayahCountWithPrefix
            }

            else ->
            {
                val number = holder.view.findViewById<AppCompatTextView>(R.id.number)
                val title = holder.view.findViewById<AppCompatTextView>(R.id.title)
                val nameAyasLoc = holder.view.findViewById<AppCompatTextView>(R.id.nameAyasLoc)
                val favourite = holder.view.findViewById<AppCompatTextView>(R.id.favourite)
                val scrim = holder.view.findViewById<View>(R.id.scrim)

                var pos = position - 1
                val surah = mSurahList.get(pos)

                holder.view?.handleClickEvent {
                    mClickAction(pos, mSurahList!!.get(pos)!!.id!!)
                }

                title.text = surah.name
                nameAyasLoc.text = surah.surahBasicInfo
                favourite.text = surah.durationLocalised


                holder.view?.let {
                    when (pos) {
                        0 -> {
                            scrim.visibility = VISIBLE
                            number.setTextColor(holder.view.context.resources.getColor(R.color.colorPrimary))
                            title.setTextColor(holder.view.context.resources.getColor(R.color.colorPrimary))
                        }
                        else -> {
                            scrim.visibility = GONE
                            number.setTextColor(holder.view.context.resources.getColor(R.color.txt_color_black))
                            title.setTextColor(holder.view.context.resources.getColor(R.color.txt_color_black))
                        }
                    }
                }


            }
        }

    }

    override fun getItemCount(): Int {
        return mSurahList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> CONTENT
        }
    }

    fun updateSurahList(list: MutableList<Data>) {
        mSurahList = list
    }
}