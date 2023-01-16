package com.gakk.noorlibrary.util

import android.app.Activity
import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.ImageRequest
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import java.io.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*


fun Activity.setStatusColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(applicationContext, color)
    }
}

fun Context.setApplicationLanguage(newLanguage: String) {
    val activityRes = resources
    val activityConf = activityRes.configuration
    val newLocale = Locale(newLanguage)
    activityConf.setLocale(newLocale)
    activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

    val applicationRes = applicationContext.resources
    val applicationConf = applicationRes.configuration
    applicationConf.setLocale(newLocale)
    applicationRes.updateConfiguration(applicationConf, applicationRes.displayMetrics)
}

fun String.getNumberInBangla(): String {
    val bangla_number = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val eng_number = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val values = StringBuilder()
    val character = this.toCharArray()

    for (c1 in character) {
        var c: Char? = null
        for (j in eng_number.indices) {
            if (c1 == eng_number[j]) {
                c = bangla_number[j]
                break
            } else {
                c = c1
            }
        }
        values.append(c)
    }

    return values.toString()
}

fun View.handleClickEvent(clickAction: () -> Unit) {

    this.setOnClickListener {

        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - BaseApplication.getMLastClickTime() < 300) {

        } else {
            BaseApplication.setMLastClickTime(SystemClock.elapsedRealtime())
            clickAction()
        }

    }
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun <T> List<T>.mutableCopyOf(): MutableList<T> {
    val original = this
    return mutableListOf<T>().apply { addAll(original) }
}

fun String.getLocalisedDuration(): String {
    var strArr: MutableList<String> = this.split(":").toMutableList()
    var result = ""
    for (i in strArr.indices) {
        if (i == strArr.size - 1) {
            result += "${TimeFormtter.getNumberByLocale(strArr[i])!!}"
        } else {
            result += "${TimeFormtter.getNumberByLocale(strArr[i])!!}:"
        }

    }
    return result

}


fun View.resizeView(viewDimen: ViewDimension, screenWdth: Int?, context: Context) {
    when (viewDimen) {
        ViewDimension.HalfScreenWidth -> if (screenWdth != null) {
            this.layoutParams.width =
                Math.ceil((screenWdth / 2 - 20 * context.resources.displayMetrics.density).toDouble())
                    .toInt()
        }
        ViewDimension.HalfScreenWidthMargin -> if (screenWdth != null) {
            this.layoutParams.width =
                Math.ceil((screenWdth / 2 - 28 * context.resources.displayMetrics.density).toDouble())
                    .toInt()
        }
        ViewDimension.OneFourthScreenWidth -> if (screenWdth != null) {
            this.layoutParams?.width =
                Math.ceil((screenWdth / 4 - 10 * context.resources.displayMetrics.density).toDouble())
                    .toInt()
        }
    }

}

fun Int.getLocalisedTextFromResId(): String {
    var text = "N/A"
    BaseApplication.getAppContext()?.let {
        text = it.resources.getString(this)
    }
    return text
}

fun Int.in12HrFormat(): Int {
    when {
        this == 12 -> return 0
        this > 12 -> return this - 12
        else -> return this
    }
}


fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun String.getTime(): String {
    var finalDate = this
    val dateFormatCurrent = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val objDate: Date? = dateFormatCurrent.parse(this)

    objDate?.let {
        val dateFormatRequired = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        finalDate = dateFormatRequired.format(it)
    }

    return finalDate
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

sealed class ViewDimension {
    object OneFourthScreenWidth : ViewDimension()
    object HalfScreenWidth : ViewDimension()
    object HalfScreenWidthMargin : ViewDimension()
}

/*fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts(
            "package", BuildConfig.APPLICATION_ID, null
        )
    })
}*/

// Extension function to share save bitmap in cache directory and share
fun Activity.shareCacheDirBitmap(uri: Uri) {
    var txtShare: String = ""
    when (AppPreference.language) {

        LAN_ENGLISH -> {
            txtShare =
                "Indeed, I am Allah . There is no deity except Me, so worship Me and establish prayer for My remembrance. \n" +
                        "Al- Quran\n" +
                        "#Callforprayer\n" +
                        "Download Noor App\n" +
                        "https://goo.gl/KvU8Zq"
        }

        LAN_BANGLA -> {
            txtShare = "\"যদি তোমরা মুমিন হও তবে সঠিক সময়ে সালাত আদায় কর।\"-আল কুরআন \n" +
                    "#নামাজেরডাক \n" +
                    "ডাউনলোড নূর অ্যাপ\n" +
                    "https://goo.gl/KvU8Zq"
        }

        else -> {
            txtShare = "\"যদি তোমরা মুমিন হও তবে সঠিক সময়ে সালাত আদায় কর।\"-আল কুরআন \n" +
                    "#নামাজেরডাক \n" +
                    "ডাউনলোড নূর অ্যাপ\n" +
                    "https://goo.gl/KvU8Zq"
        }
    }

    val fis = FileInputStream(uri.path)  // 2nd line
    val bitmap = BitmapFactory.decodeStream(fis)
    fis.close()

    try {
        val file = File("${this.cacheDir}/prayer.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
        val contentUri = FileProvider.getUriForFile(this, "com.gakk.noorlibrary.provider", file)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, txtShare)
        shareIntent.type = "image/*"
        this.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}

fun Activity.shareCacheDirBitmapV2(uri: Uri?) {
    val txtShare = "Lead your life in an Islamic way. Get the Noor app now. https://cutt.ly/z2V8tm8"

    val fis = FileInputStream(uri?.path)
    val bitmap = BitmapFactory.decodeStream(fis)
    fis.close()

    try {
        val file = File("${this.cacheDir}/prayer.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
        val contentUri = FileProvider.getUriForFile(this, "com.gakk.noorlibrary.provider", file)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, txtShare)
        shareIntent.type = "image/*"
        this.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}

// Extension method to save bitmap to internal storage
fun Bitmap.saveToInternalStorage(context: Context): Uri {
    // Get the context wrapper instance
    val wrapper = ContextWrapper(context)

    // Initializing a new file
    // The bellow line return a directory in internal storage
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)


    // Create a file to save the image, random file name
    //file = File(file, "${UUID.randomUUID()}.png")

    file = File(file, "image.png")

    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // Compress bitmap
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)

        // Flush the stream
        stream.flush()

        // Close stream
        stream.close()
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
    }

    // Return the saved image uri
    return Uri.parse(file.absolutePath)
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun String.toBitmap(): Bitmap? {
    Base64.decode(this, Base64.NO_WRAP).apply {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun List<KhatamQuranVideosResponse.Data>?.videoNewList(
    mediaId: String?,
    isPlaying: Boolean
): List<KhatamQuranVideosResponse.Data> {
    val newList: MutableList<KhatamQuranVideosResponse.Data> = ArrayList()
    if (mediaId == null && this != null) {
        return this
    }
    this?.forEach {
        val newItem = it.copy(isPlaying = false, isSelected = false)
        if (it.id == mediaId) {
            newItem.isPlaying = isPlaying
            newItem.isSelected = true
        }
        newList.add(newItem)
    }
    return newList
}

fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)
}

fun Uri.getName(context: Context): String? {
    val returnCursor = context.contentResolver.query(this, null, null, null, null)
    val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor?.moveToFirst()
    val fileName = nameIndex?.let { returnCursor.getString(it) }
    returnCursor?.close()
    return fileName
}

fun loadJSONFromAsset(inputStream: InputStream): String? {
    val json: String = try {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, StandardCharsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    return json
}

suspend fun getBitmapFromUrlX(bitmapURL: String?, context: Activity?) {

    context?.let {
        val request = ImageRequest.Builder(context).data(bitmapURL).build()
        val imageLoader = Coil.imageLoader(context)
        try {
            val downloadedBitmap =
                (imageLoader.execute(request).drawable as BitmapDrawable).bitmap

            val uri = downloadedBitmap.saveToInternalStorage(context)

            context?.shareCacheDirBitmapV2(uri)
        } catch (e: Exception) {
        }
    }


}