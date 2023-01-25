package com.gakk.noorlibrary.ui.adapter

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
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.util.handleClickEvent

internal class QuranSchoolAdapter(
    private val listener: OnItemClickListener
) :
    ListAdapter<Data, RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(layoutView:View) :
        RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.layout_quran_school,
            parent,
            false
        )

         val viewHolder = QuranSchoolOldViewHolder(binding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: QuranSchoolOldViewHolder =
            holder as QuranSchoolOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class QuranSchoolOldViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {
        init {
            binding.handleClickEvent {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)
                    listener.onItemClickVideo(position, currentList)
                }
            }
        }

        fun bind(listLiterature: Data) {
           val  title :AppCompatTextView = itemView.findViewById(R.id.title)
            title.text = listLiterature.contenTtitle
            val scholars_img: ImageView = itemView.findViewById(R.id.scholars_img)
            Glide.with(itemView.context).load(listLiterature.fullImageUrl.replace("<size>", "1280")).into(scholars_img)
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.GONE
            //binding = listLiterature
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean = oldItem.hashCode() == newItem.hashCode()

        }
    }

    interface OnItemClickListener {
        fun onItemClickVideo(postion: Int, currentList: List<Data>)
    }
}