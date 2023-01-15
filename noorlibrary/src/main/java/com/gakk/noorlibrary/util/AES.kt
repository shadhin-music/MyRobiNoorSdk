package com.gakk.noorlibrary.util


import android.util.Base64
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

const val algorithm = "AES/CBC/PKCS5Padding"
const val base64Regex = "^(?:[A-Za-z\\d+/]{4})*(?:[A-Za-z\\d+/]{3}=|[A-Za-z\\d+/]{2}==)?"
private var key: SecretKeySpec? = null
private var IV: IvParameterSpec? = null
private val urlsMap: MutableMap<String, String> = HashMap()
fun decrypt(cipherText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, key, iv)
    val base64String = Base64.decode(cipherText, Base64.DEFAULT)
    val plainText = cipher.doFinal(base64String)
    return String(plainText)
}

fun encrypt(inputText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)
    val cipherText = cipher.doFinal(inputText.toByteArray())
    return Base64.encodeToString(cipherText, Base64.DEFAULT)
}

fun keyIVInit() {
    if (key == null || IV == null) {
        key = createKey()
        IV = createIV()
    }
}

fun String.decryptUrl(): String? {
    keyIVInit()

    if (urlsMap[this] == null) {
        if (this.isEncryptedUrl()) {
            val url = exH { decrypt(this, key!!, IV!!) }
            urlsMap[this] = url.toString()
            return url
        }
        return this

    }
    return urlsMap[this]
}
fun decryptData(string: String):String{
    keyIVInit()
    return try{
        decrypt(string, key!!, IV!!)
    }catch (e:Exception){
        string
    }
}
fun String.encrypt(): String? {
    keyIVInit()
    return encrypt(this, key!!, IV!!)
}

fun String.isEncryptedUrl(): Boolean {
    return Regex(base64Regex).matches(this)
}

fun createKey(): SecretKeySpec {
    val str = BrainFuska(createSource(BfcUtils.key.bfr()))
        .evaluate()
    return SecretKeySpec(str.toByteArray(), "AES")
}

fun createIV(): IvParameterSpec {
    val str = BrainFuska(createSource(BfcUtils.iv.bfr()))
        .evaluate()

    return IvParameterSpec(str.toByteArray())
}

fun createSource(string: String): Vector<Char> {
    val source = Vector<Char>()
    string.forEach {
        source.add(it)
    }
    return source
}
fun String.decryptStr(): String? {
    return decryptUrlX()
}

fun String.decryptUrlX(): String? {
    keyIVInit()

    if (urlsMap[this] == null) {
        if (this.isEncryptedUrl()) {
            val url = exH { decrypt(this, key!!, IV!!) }
            urlsMap[this] = url.nullFix()
            return url
        }
        return this
    }
    return urlsMap[this]
}


