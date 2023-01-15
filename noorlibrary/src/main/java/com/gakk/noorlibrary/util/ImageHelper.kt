package com.gakk.noorlibrary.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import coil.request.ImageRequest
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.data.prefs.AppPreference
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ImageHelper {

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getFileFromBitmap(mBitmap: Bitmap): File {
        //create a file to write bitmap data
        var f = File(
            BaseApplication.getAppContext().getCacheDir(),
            ""
            /*AppPreference.cachedUser?.userId!!*/
        );
        f.createNewFile()

        //Convert bitmap to byte array
        var bitmap: Bitmap = mBitmap
        var bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        var bitmapdata = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitmapdata)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f
    }

    fun getMultiPartImage(f: File): MultipartBody.Part {
        val JSON = "image/*".toMediaType()
        var reqFile: RequestBody = RequestBody.create(JSON, f)
        //f.absolutePath
        // Log.i("ABSP",f.absolutePath.toString())
        var body: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            f.getName() + ".jpeg",
            reqFile
        )
        return body
    }

    fun getResizedBitmap(
        image: Bitmap,
        maxSize: Int
    ): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap()
            }
        }
        bitmap = if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }
}