package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutItemInstructiveVideoBinding
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.util.ViewDimension
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.resizeView

internal class InstructiveVideoAdapter(
    private val listener: OnItemClickListener,
    detailsCallBack: DetailsCallBack,
) :
    ListAdapter<Data, RecyclerView.ViewHolder>(diffUtil) {
    val mDetailsCallBack = detailsCallBack
    inner class ViewHolder(layoutView:View) :
        RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val viewHolder(layoutView:View): RecyclerView.ViewHolder(lay)
        val binding: View =
            LayoutInflater.from(parent.context).inflate(
            R.layout.layout_item_instructive_video,
            parent,
            false
        )

      val  viewHolder = InstructiveVideoOldViewHolder(binding)


        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val oldViewHolder: InstructiveVideoOldViewHolder =
            holder as InstructiveVideoOldViewHolder

        oldViewHolder.bind(data)
    }


    inner class InstructiveVideoOldViewHolder(layoutView:View) :
        RecyclerView.ViewHolder(layoutView) {
        init {
            layoutView.resizeView(
                ViewDimension.HalfScreenWidthMargin,
                mDetailsCallBack.getScreenWith(),
                layoutView.context
            )

           layoutView.handleClickEvent {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)
                    listener.onItemClickVideo(position, currentList)
                }
            }
        }

        fun bind(listLiterature: Data) {
            val textView: AppCompatTextView = itemView.findViewById(R.id.duration)
            textView.text = listLiterature.durationFormatted
            val textView2: AppCompatTextView = itemView.findViewById(R.id.title)
            textView2.text = listLiterature.contenTtitle
            val imgBg = itemView.findViewById<AppCompatImageView>(R.id.img)
            Glide.with(itemView.context).load(listLiterature.fullImageUrl.replace("<size>", "400")).into(imgBg)
            Log.e("TAG","DATA: "+  listLiterature.fullImageUrl)
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.GONE
            //binding.video = listLiterature
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