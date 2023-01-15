package com.gakk.noorlibrary.util

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */
object Parser {
    fun getJSONByIndex(
        str: String?,
        str2: String?,
        strArr: Array<String>
    ): ArrayList<Array<String>> {
        val jSONArray: JSONArray
        val arrayList: ArrayList<Array<String>> = ArrayList()
        if (str != null) {
            try {
                if (str.isNotEmpty()) {
                    jSONArray = if (str2 != null) {
                        JSONObject(str).getJSONArray(str2)
                    } else {
                        JSONArray(str)
                    }
                    for (i in 0 until jSONArray.length()) {
                        val jSONObject = jSONArray.getJSONObject(i)
                        val strArr2 = Array(strArr.size) { "" }
                        for (i2 in strArr.indices) {
                            if (jSONObject.has(strArr[i2])) {
                                strArr2[i2] = jSONObject.getString(strArr[i2])
                            }
                        }
                        arrayList.add(strArr2)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("test", "Parser Exception: $e")
            }
        }
        return arrayList
    }
}