package com.gakk.noorlibrary.service

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.download.library.DownloadImpl
import com.download.library.DownloadListenerAdapter
import com.download.library.Extra
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.util.DownloadProgressControl
import kotlinx.coroutines.*
import java.io.*
import java.net.URL
import java.net.URLConnection


const val DOWNLOAD_ID = "downloadId"
const val DOWNLOAD_URL = "downloadUrl"

class DownloadService : Service() {

    private lateinit var downloadScope: CoroutineScope
    private lateinit var downloadableInfo: DownloadableInfo
    private lateinit var mContext: Context
    private lateinit var imageLoader: ImageLoader
    private lateinit var fileExtension: String
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mContext = this

        imageLoader = Coil.imageLoader(this)
        if (!this::downloadableInfo.isInitialized) {
            downloadableInfo = DownloadableInfo()
        }
        if (!this::downloadScope.isInitialized) {
            downloadScope = CoroutineScope(SupervisorJob())
        }
        val id = intent?.getStringExtra(DOWNLOAD_ID)
        val url = intent?.getStringExtra(DOWNLOAD_URL)
        downloadScope.launch {
            id?.let { url?.let { it1 -> startDownload(it, it1) } }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (this::downloadScope.isInitialized) {
            downloadScope.cancel()
        }
        super.onDestroy()
    }

    //"https://speed.hetzner.de/100MB.bin"
    suspend fun startDownload(id: String, url: String) {
        downloadScope.launch {
            val fileSize = getFileSizeFromUrl(url)
            fileExtension = downloadableInfo.getDownloadableExtension(url)


            DownloadImpl.getInstance(mContext!!)
                .with(url)
                .target(File(mContext!!.externalCacheDir, "$id$fileExtension"))
                .setUniquePath(true)
                .setForceDownload(true).enqueue(object : DownloadListenerAdapter() {
                    override fun onProgress(
                        url: String?,
                        downloaded: Long,
                        length: Long,
                        usedTime: Long
                    ) {
                        super.onProgress(url, downloaded, length, usedTime)
                        val progress = (downloaded / fileSize.toFloat()) * 100
                        Log.i(
                            "SERVICE-",
                            "downloaded :$downloaded total :$fileSize id:$id progress:$progress"
                        )
                        AppPreference.updateDownloadProgress(id, progress.toInt())

                        downloadScope.launch {
                            withContext(Dispatchers.Main) {
                                DownloadProgressControl.updateDownloadLayouts()
                            }

                        }
                    }

                    override fun onResult(
                        throwable: Throwable?,
                        path: Uri?,
                        url: String?,
                        extra: Extra?
                    ): Boolean {
                        when (fileExtension) {
                            ".jpg" -> {
                                getBitmapFromUrl(url)
                            }

                            else -> {


                                Glide.with(mContext)
                                    .download(url)
                                    .listener(object : RequestListener<File> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<File>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            Log.e("extension", "chk" + e)
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: File?,
                                            model: Any?,
                                            target: Target<File>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {

                                            saveGifImage(mContext,
                                                resource?.let { getBytesFromFile(it) },
                                                url?.let { createName(it) })
                                            return true
                                        }

                                    }).submit()
                            }
                        }


                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.download_success),
                            Toast.LENGTH_LONG
                        ).show()
                        return super.onResult(throwable, path, url, extra)
                    }
                })
        }

    }

    fun createName(url: String): String? {
        var name = url.substring(url.lastIndexOf('/') + 1, url.length)
        val NoExt = name.substring(0, name.lastIndexOf('.'))
        /*if (!ext.equals(".gif")) {
            name = "$NoExt.jpg"
        }*/

        return name
    }

    @Throws(IOException::class)
    fun getBytesFromFile(file: File): ByteArray? {
        val length = file.length()
        if (length > Int.MAX_VALUE) {
            throw IOException("File is too large!")
        }
        val bytes = ByteArray(length.toInt())
        var offset = 0
        var numRead = 0
        val `is`: InputStream = FileInputStream(file)
        try {
            while (offset < bytes.size
                && `is`.read(bytes, offset, bytes.size - offset).also { numRead = it } >= 0
            ) {
                offset += numRead
            }
        } finally {
            `is`.close()
        }
        if (offset < bytes.size) {
            throw IOException("Could not completely read file " + file.name)
        }
        return bytes
    }

    private fun getBitmapFromUrl(bitmapURL: String?) = downloadScope.launch {

        val request = ImageRequest.Builder(mContext)
            .data(bitmapURL)
            .build()
        try {
            val downloadedBitmap = (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
            saveMediaToStorage(downloadedBitmap)

        } catch (e: Exception) {

        }

    }


    fun saveGifImage(context: Context, bytes: ByteArray?, imgName: String?) {
        var fos: FileOutputStream? = null
        try {
            val externalStoragePublicDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(externalStoragePublicDirectory, imgName)
            fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()
            if (file != null) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, file.name)
                values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                values.put(MediaStore.Images.Media.DESCRIPTION, "")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                val contentResolver = context.contentResolver
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)


                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val f = File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
                val contentUri = Uri.fromFile(f)

                MediaScannerConnection.scanFile(context, arrayOf(file.toString()),
                    null, null)
                mediaScanIntent.data = contentUri
                this.sendBroadcast(mediaScanIntent)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("version", "more than Q"+e.message)

        }
    }

    suspend fun getFileSizeFromUrl(url: String): Int {
        return withContext(Dispatchers.IO) {
            downloadableInfo.getDownloadableSizeFromUrl(url)
        }

    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}" + fileExtension
        Log.e("filename", "chk" + filename)
        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            //getting the contentResolver
            mContext?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {

            Log.e("version", "less than Q")

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f =
                File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            val contentUri = Uri.fromFile(f)
            MediaScannerConnection.scanFile(mContext, arrayOf(f.toString()),
                null, null)
            mediaScanIntent.data = contentUri
            this.sendBroadcast(mediaScanIntent)
            // Toast.makeText(mContext, "Saved to Photos", Toast.LENGTH_LONG).show()
        }
    }

}

private class DownloadableInfo {
    fun getDownloadableSizeFromUrl(url: String): Int {
        val myUrl = URL(url)
        val urlConnection: URLConnection = myUrl.openConnection()
        urlConnection.connect()
        val file_size: Int = urlConnection.getContentLength()/// 1000000
        return file_size
    }

    fun getDownloadableExtension(url: String): String {
        return url.substring(url.lastIndexOf("."))
    }


}
