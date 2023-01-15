package com.gakk.noorlibrary.util

import android.os.Handler
import android.os.Looper
import java.util.regex.Pattern

val ukey2 = "STEHLGCilw_Y9_11qcW8"
val ukey = "AIzaSyAO_FJ2SlqU8Q4"

val ykeyTotal = ukey + ukey2


fun delay(millis: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(Runnable {
        callback.invoke()
    }, millis)
}


inline fun <T> exH(func: () -> T): T? {
    return try {
        func.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.bfr(): String {
    val sBuilder = StringBuffer()
    this.forEach {
        when (it) {
            '-' -> sBuilder.append('+')
            '+' -> sBuilder.append('-')
            '<' -> sBuilder.append('>')
            '>' -> sBuilder.append('<')
            '.' -> sBuilder.append('.')
            '[' -> sBuilder.append(']')
            ']' -> sBuilder.append('[')
        }
    }
    return sBuilder.toString()
}

fun String.toFuskaUrl(): String {
    val sBuilder = StringBuffer()
    this.forEach {
        when (it) {
            '-' -> sBuilder.append('a')
            '+' -> sBuilder.append('b')
            '<' -> sBuilder.append('c')
            '>' -> sBuilder.append('d')
            '.' -> sBuilder.append('e')
            '[' -> sBuilder.append('f')
            ']' -> sBuilder.append('g')
        }
    }
    return "omy${sBuilder.toString()}tno"
}

fun String.toFuska(): String {
    val sBuilder = StringBuffer()
    this.forEach {
        when (it) {
            'a' -> sBuilder.append('-')
            'b' -> sBuilder.append('+')
            'c' -> sBuilder.append('<')
            'd' -> sBuilder.append('>')
            'e' -> sBuilder.append('.')
            'f' -> sBuilder.append('[')
            'g' -> sBuilder.append(']')
        }
    }
    return sBuilder.toString()
}

fun String.decodeFuskaUrl(): String {

    return this.replace(BfcUtils.regexPatternURL) { it.normalizeFuska() }
}

fun String.normalizeFuska(): String {
    return com.gakk.noorlibrary.util.BrainFuska(createSource(this.toFuska().bfr())).evaluate()
}

fun String.matchList(regex: String): List<String> {
    val list: MutableList<String> = ArrayList()
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        list.add(matcher.group())
    }
    return list
}

fun String.replace(regex: String, replaceFunc: (string: String) -> String): String {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this)
    val stringBuffer = StringBuffer()
    while (matcher.find()) {
        val newString = replaceFunc(matcher.group())
        matcher.appendReplacement(stringBuffer, newString)
    }
    matcher.appendTail(stringBuffer)
    return stringBuffer.toString()
}

fun rangeMap(x: Float, in_min: Float, in_max: Float, out_min: Float, out_max: Float): Float {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
}

fun calculateVideoHeight(displayWidth: Int, videoWidth: Int, videoHeight: Int): Int {
    return rangeMap(
        videoHeight.toFloat(),
        0F,
        videoWidth.toFloat(),
        0F,
        displayWidth.toFloat()
    ).toInt()
}

fun String?.nullFix(): String {
    if(this !=null && this != "null"){
        return this
    }
    return ""
}


fun isBangladeshiPhoneNumber(phoneNumber: String?): Boolean {

    return phoneNumber?.let { Regex("\\+?(88)01[13456789]\\d{2}\\-?\\d{6}").matches(it) }?:false
}

fun isRobiNumber(phoneNumber:String) =  Regex( "\\+?(88)?01[68]\\d{8}").matches(phoneNumber)






