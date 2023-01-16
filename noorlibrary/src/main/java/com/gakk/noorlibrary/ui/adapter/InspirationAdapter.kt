package com.gakk.noorlibrary.ui.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.databinding.LayoutItemInspirationBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.handleClickEvent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InspirationAdapter(
    val imageList: MutableList<Literature>
) :
    RecyclerView.Adapter<InspirationAdapter.ViewHolder>() {


    inner class ViewHolder(binding: LayoutItemInspirationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var inspirationBinding: LayoutItemInspirationBinding? = binding

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemInspirationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_inspiration,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = imageList[position]
        holder.inspirationBinding?.item = list

        holder.inspirationBinding?.rlShare?.handleClickEvent {
          Glide.with(BaseApplication.getAppContext()).asBitmap().load(list.fullImageUrl)
              .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
              .listener(object :RequestListener<Bitmap>{
                  override fun onLoadFailed(
                      e: GlideException?,
                      model: Any?,
                      target: Target<Bitmap>?,
                      isFirstResource: Boolean
                  ): Boolean {
                     return false
                  }

                  override fun onResourceReady(
                      resource: Bitmap?,
                      model: Any?,
                      target: Target<Bitmap>?,
                      dataSource: DataSource?,
                      isFirstResource: Boolean
                  ): Boolean {
                      val i = Intent(Intent.ACTION_SEND)
                      i.type = "image/*"
                      i.putExtra(Intent.EXTRA_STREAM, resource?.let { getLocalBitmapUri(it) })
                      holder.inspirationBinding?.rlShare?.context!!.startActivity(Intent.createChooser(i, "Share Image"))
                      return false
                  }

              }).submit()
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                BaseApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(
                BaseApplication.getAppContext(),
                "com.gakk.noorlibrary" + ".provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}