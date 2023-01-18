package com.gakk.noorlibrary.util

import android.graphics.Bitmap
import com.gakk.noorlibrary.Noor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*

object ImageHelper {

    fun getFileFromBitmap(mBitmap: Bitmap): File {
        //create a file to write bitmap data
        var f = File(
            Noor.appContext?.getCacheDir(),
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
}