package com.gakk.noorlibrary.ui.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.R

import com.gakk.noorlibrary.model.quranSchool.QuranSchoolModel
import com.gakk.noorlibrary.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/1/2021, Thu
 */
internal class QuranSchoolChildAdapter(
    private val listener: OnItemClickListener
) :
    ListAdapter<QuranSchoolModel, RecyclerView.ViewHolder>(diffUtil) {

    private val VIEW_TYPE_LIVE_CONTENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: View =
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_quran_school_old_child_item,
                parent,
                false
            )

        val viewHolder = QuranSchoolChildOldViewHolder(binding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: QuranSchoolChildOldViewHolder =
            holder as QuranSchoolChildOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class QuranSchoolChildOldViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {
        init {
            binding.handleClickEvent {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onItemClick(position, currentList)
                }
            }
        }

        fun bind(quranSchoolModel: QuranSchoolModel) {
            val scholars_img: ImageView = binding.findViewById(R.id.scholars_img)
            val progressBar = binding.findViewById<ProgressBar>(R.id.progressBar)

            val image = quranSchoolModel.contentBaseUrl + "/" + quranSchoolModel.imageUrl

            setImageFromUrl(scholars_img, image, progressBar, PLACE_HOLDER_16_9)

            val dateTv = binding.findViewById<AppCompatTextView>(R.id.date_tv)
            dateTv.text = quranSchoolModel.liveOn
            val title = binding.findViewById<AppCompatTextView>(R.id.title)
            title.text = quranSchoolModel.title
            val iconLive: AppCompatImageView = binding.findViewById(R.id.icon_live)
            val description = binding.findViewById<AppCompatTextView>(R.id.description)
            description.text = quranSchoolModel.about ?: quranSchoolModel.scholarName
            progressBar.visibility = View.GONE
            if (quranSchoolModel.isLive == true) {
                iconLive.show()
                dateTv.hide()
                description.hide()
            } else {
                iconLive.hide()
                dateTv.show()
                description.show()
            }
            //binding.item = quranSchoolModel
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuranSchoolModel>() {
            override fun areItemsTheSame(
                oldItem: QuranSchoolModel,
                newItem: QuranSchoolModel
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: QuranSchoolModel,
                newItem: QuranSchoolModel
            ): Boolean = oldItem.hashCode() == newItem.hashCode()

        }
    }


    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_LIVE_CONTENT
    }

    interface OnItemClickListener {
        fun onItemClick(postion: Int, currentList: List<QuranSchoolModel>)
    }
}