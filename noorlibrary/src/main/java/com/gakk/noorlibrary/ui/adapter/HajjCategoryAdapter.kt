package com.gakk.noorlibrary.ui.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.util.handleClickEvent

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/26/2021, Mon
 */

typealias SubCategoryListener = ((Data) -> Unit)?

internal class HajjCategoryAdapter : ListAdapter<Data, HajjCategoryAdapter.HajjCatViewHolder>(diffUtil) {

    private var onItemClick: SubCategoryListener = null
    fun setOnItemClickListener(listener: SubCategoryListener) {
        onItemClick = listener
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

    inner class HajjCatViewHolder(ItemView: View) :
        RecyclerView.ViewHolder(ItemView) {

        val textView: TextView = itemView.findViewById(R.id.tvTitleHajjCat)
        val imgContent: ImageView = itemView.findViewById(R.id.imgContent)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(data: Data, onItemClick: SubCategoryListener) {
            textView.text = data.name

            Noor.appContext?.let {
                Glide.with(it)
                    .load(data.fullImageUrl?.replace("<size>", "1280"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                    })
                    .error(R.drawable.place_holder_1_1_ratio)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imgContent)
            }
            itemView.handleClickEvent {
                onItemClick.let { click ->
                    click?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HajjCatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hajj_cat, parent, false)
        return HajjCatViewHolder(view)
    }

    override fun onBindViewHolder(holder: HajjCatViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick)
    }

}