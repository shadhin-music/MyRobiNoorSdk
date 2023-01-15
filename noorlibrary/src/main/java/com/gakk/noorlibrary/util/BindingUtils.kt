package com.gakk.noorlibrary.util


import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import java.util.*

@BindingAdapter("android:imageResId")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("android:fullImageUrl", "app:attachedProgressBar", "app:placeHolderDimension")
fun setImageFromUrl(
    imageView: ImageView,
    url: String? = null,
    progressBar: ProgressBar,
    dimen: String
) {
    url?.let {
        progressBar.visibility = VISIBLE
        val placeHolder = getPlaceHolder(dimen)
        Glide.with(BaseApplication.getAppContext())
            .load(url.replace("<size>", "1280"))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE
                    return false
                }

            })
            .error(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }
}


@BindingAdapter("android:squarefullImageUrl", "app:attachedProgressBar", "app:placeHolderDimension")
fun setSqureImageFromUrl(
    imageView: ImageView,
    url: String? = null,
    progressBar: ProgressBar,
    dimen: String
) {
    url?.let {
        progressBar.visibility = VISIBLE
        val placeHolder = getPlaceHolder(dimen)
        Glide.with(BaseApplication.getAppContext())
            .load(url.replace("<size>", "400"))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE
                    return false
                }

            })
            .error(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }
}

@BindingAdapter("android:imageUrl", "app:attachedProgressBar")
fun setImageFromUrl(imageView: ImageView, url: String?, progressBar: ProgressBar) {

    if (!url.isNullOrEmpty()) {
        progressBar.visibility = VISIBLE
        Glide.with(BaseApplication.getAppContext())
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = GONE
                    return false
                }

            })
            .error(R.drawable.place_holder_16_9_ratio)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imageView)
    }
}

@BindingAdapter("android:imageUrlNoProgress")
fun setImageFromUrlNoProgress(imageView: ImageView, url: String?) {

    if (!url.isNullOrEmpty()) {
        Glide.with(BaseApplication.getAppContext())
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("Imageload", "onLoadFailed")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("Imageload", "onResourceReady")

                    return false
                }

            })
            .error(R.drawable.place_holder_16_9_ratio)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }
}

@BindingAdapter("android:patchImageFullUrl")
fun setPatchImageFromUrl(imageView: ImageView, url: String?) {

    if (!url.isNullOrEmpty()) {

        Glide.with(BaseApplication.getAppContext())
            .load(url)
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
            .error(R.drawable.ic_quran_verse)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imageView)
    }
}

private fun getPlaceHolder(dimen: String): Int {
    when (dimen) {
        PLACE_HOLDER_16_9 -> return R.drawable.place_holder_16_9_ratio
        PLACE_HOLDER_1_1 -> return R.drawable.place_holder_1_1_ratio
        PLACE_HOLDER_2_3 -> return R.drawable.place_holder_2_3_ratio
        else -> return R.drawable.place_holder_4_3_ratio
    }


}

@BindingAdapter("imageBackground")
fun AppCompatButton.imageBackground(data: com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse.Data?) {
    if (data != null) {
        this.setBackgroundResource(data.imageResource)
    }
}
