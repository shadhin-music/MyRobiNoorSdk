package com.gakk.noorlibrary.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.Keep
import com.gakk.noorlibrary.model.UserLocation

//have to change with user data class
import com.gakk.noorlibrary.model.billboard.Data

import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.util.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object AppPreference {

    private lateinit var mGSonInstance: Gson

    private val whiteListNumber =
        arrayListOf("8801537673977", "8801917589656")

    private const val PREF_FILE_NAME = "NoorPreference"
    private const val LANGUAGE = "Language"
    private const val USER_CURRENT_LOCATION = "userCurrentLocation"
    private const val USER_NUMBER = "userNumber"
    private const val FIREBASE_TOKEN = "firebaseToken"
    private const val PREF_USER = "user"
    private const val PREF_USER_INFO = "userInfo"

    private const val PREF_DOWNLOAD_PROGRESS_MAP = "downloadProgressMap"
    private const val PREF_DOWNLOAD_PATH_MAP = "downloadPathMap"
    private const val ISNOTIFICATIONON = "isNotificationOn"
    private const val ISSOUNDON = "isSoundOn"
    private const val TOTALCOUNTTAG = "totalcount"

    private const val PREF_RAMADAN_SEHRI_IFTER_LIST = "ramadanSehriIfterList"
    private const val PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST = "nextTenDaysSehriIfterList"

    private const val PREF_RAMADAN_NOTIFICATION_SEHRI_ALERT_ON = "ramadanNotificationSehriAlertOn"
    private const val PREF_RAMADAN_NOTIFICATION_IFTER_ALERT_ON = "ramadanNotificationIfterAlertOn"
    private const val PREF_RAMADAN_NOTIFICATION_SOUND_ON = "ramadanNotificationSoundOn"
    private const val PREF_RAMADAN_NOTIFICATION_VIBRATION_ON = "ramadanNotificationVibrationOn"
    private const val PREF_LAST_SET_SEHRI_ALARM_TIME = "lastSavedSehriAlarmTime"
    private const val PREF_LAST_SET_IFTER_ALARM_TIME = "lastSavedIfterAlarmTime"

    const val IS_FAJR_ALARM_SET = "is_fajr_alarm_set"
    const val IS_DHUHR_ALARM_SET = "is_dhuhr_alarm_set"
    const val IS_ASR_ALARM_SET = "is_asr_alarm_set"
    const val IS_MAGHRIB_ALARM_SET = "is_maghrib_alarm_set"
    const val IS_ISHA_ALARM_SET = "is_isha_alarm_set"

    private const val ISSUBWEEKLY = "is_sub_daily"
    private const val ISSUBMONTHLY = "is_sub_monthly"
    private const val ISSUBYEARLY = "is_sub_yearly"
    private const val ISSUBMONTHLYGPAY = "is_sub_monthly_gpay"
    private const val ISSUBWEEKLYSOFTBUNDLE = "is_sub_weekly_soft"
    private const val ISSUBMONTHLYSOFTBUNDLE = "is_sub_monthly_soft"
    private const val ISSUBMONTHLYSOFTBUNDLEROBI = "is_sub_monthly_soft_robi"
    private const val ISSUBWEEKLYSOFTBUNDLEROBI = "is_sub_weekly_soft_robi"
    private const val ISSUBSOFTBUNDLEROBI = "is_sub_soft_robi"
    private const val ISSUBSOFTBUNDLERAMADANROBI = "is_sub_soft_ramadan_robi"
    private const val ISSUBSOFTBUNDLESEVENDAYSROBI = "is_sub_soft_seven_days_robi"
    private const val ISSUBSOFTBUNDLEFIFTEENDAYSROBI = "is_sub_soft_fifteen_days_robi"
    private const val ISSUBIQRA = "is_sub_iqra"
    private const val ISSUBQURAN = "is_sub_quran"
    private const val ISSUBMONTHLYNAGAD = "is_sub_monthly_nagad"
    private const val ISSUBHALFYEARLYNAGAD = "is_sub_half_yearly_nagad"
    private const val ISSUBYEARLYNAGAD = "is_sub_yearly_nagad"

    private const val ISSUBMONTHLYSSL = "is_sub_monthly_ssl"
    private const val ISSUBHALFYEARLYSSL = "is_sub_half_yearly_ssl"
    private const val ISSUBYEARLYSSL = "is_sub_yearly_ssl"
    private const val ISFROMBD = "is_from_bd"

    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private lateinit var SET_LANGUAGE: Pair<String, String>

    private val SET_NUMBER = Pair(USER_NUMBER, NUMBER)
    private val NOTIFICATION_FLAG = Pair(ISNOTIFICATIONON, true)
    private val SOUND_FLAG = Pair(ISSOUNDON, true)
    private val TOTAL_COUNT = Pair(TOTALCOUNTTAG, 0)

    private val IS_SUB_WEEKLY = Pair(ISSUBWEEKLY, false)
    private val IS_SUB_MONTHLY = Pair(ISSUBMONTHLY, false)
    private val IS_SUB_YEARLY = Pair(ISSUBYEARLY, false)
    private val IS_SUB_MONTHLY_GPAY = Pair(ISSUBMONTHLYGPAY, false)
    private val IS_SUB_WEEKLY_SOFT_BUNDLE = Pair(ISSUBWEEKLYSOFTBUNDLE, false)
    private val IS_SUB_MONTHLY_SOFT_BUNDLE = Pair(ISSUBMONTHLYSOFTBUNDLE, false)
    private val IS_SUB_MONTHLY_SOFT_BUNDLE_ROBI = Pair(ISSUBMONTHLYSOFTBUNDLEROBI, false)
    private val IS_SUB_WEEKLY_SOFT_BUNDLE_ROBI = Pair(ISSUBWEEKLYSOFTBUNDLEROBI, false)
    private val IS_SUB_SOFT_BUNDLE_ROBI = Pair(ISSUBSOFTBUNDLEROBI, false)
    private val IS_SUB_SOFT_BUNDLE_RAMADAN_ROBI = Pair(ISSUBSOFTBUNDLERAMADANROBI, false)
    private val IS_SUB_SOFT_BUNDLE_SEVEN_DAYS_ROBI = Pair(ISSUBSOFTBUNDLESEVENDAYSROBI, false)
    private val IS_SUB_SOFT_BUNDLE_FIFTEEN_DAYS_ROBI = Pair(ISSUBSOFTBUNDLEFIFTEENDAYSROBI, false)
    private val SET_FIREBASE_TOKEN = Pair(FIREBASE_TOKEN, TOKEN_TAG)
    private val IS_SUB_IQRA = Pair(ISSUBIQRA, false)
    private val IS_SUB_QURAN = Pair(ISSUBQURAN, false)
    private val IS_SUB_MONTHLY_NAGAD = Pair(ISSUBMONTHLYNAGAD, false)
    private val IS_SUB_HALF_YEARLY_NAGAD = Pair(ISSUBHALFYEARLYNAGAD, false)
    private val IS_SUB_YEARLY_NAGAD = Pair(ISSUBYEARLYNAGAD, false)

    private val IS_SUB_MONTHLY_SSL = Pair(ISSUBMONTHLYSSL, false)
    private val IS_SUB_HALF_YEARLY_SSL = Pair(ISSUBHALFYEARLYSSL, false)
    private val IS_SUB_YEARLY_SSL = Pair(ISSUBYEARLYSSL, false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE)

        mGSonInstance = Gson()

        SET_LANGUAGE = Pair(LANGUAGE, LAN_BANGLA)
    }


    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    var language: String?
        get() = preferences.getString(SET_LANGUAGE.first, SET_LANGUAGE.second)
        set(value) = preferences.edit { it.putString(SET_LANGUAGE.first, value) }

    var userNumber: String?
        get() = preferences.getString(SET_NUMBER.first, SET_NUMBER.second)
        set(value) = preferences.edit { it.putString(SET_NUMBER.first, value) }


    var cachedUser: Data?
        set(value) {
            val userDataStr = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_USER, userDataStr) }
        }
        get() {
            val userDataStr = preferences.getString(PREF_USER, "")
            val type: Type = genericType<Data>()
            return mGSonInstance.fromJson(userDataStr, type)
        }
    var cachedUserInfo: com.gakk.noorlibrary.model.profile.Data
        set(value) {
            val userDataStr = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_USER_INFO, userDataStr) }
        }
        get() {
            val userDataStr = preferences.getString(PREF_USER_INFO, "")
            val type: Type = genericType<com.gakk.noorlibrary.model.profile.Data>()
            return mGSonInstance.fromJson(userDataStr, type)
        }

    var sehriAlertOn: Boolean
        get() = preferences.getBoolean(PREF_RAMADAN_NOTIFICATION_SEHRI_ALERT_ON, false)
        set(value) = preferences.edit {
            it.putBoolean(PREF_RAMADAN_NOTIFICATION_SEHRI_ALERT_ON, value)
        }

    var lastRozaAlarmDisMissTimeMs: Long
        get() = preferences.getLong("last_roza_alrm_dismiss_time_ms", 0L)
        set(value) = preferences.edit {
            it.putLong("last_roza_alrm_dismiss_time_ms", value)
            Log.e("RECEIVER", "$value")
        }


    var ifterAlertOn: Boolean
        get() = preferences.getBoolean(PREF_RAMADAN_NOTIFICATION_IFTER_ALERT_ON, false)
        set(value) = preferences.edit {
            it.putBoolean(PREF_RAMADAN_NOTIFICATION_IFTER_ALERT_ON, value)
        }

    var sehriOrifterAlertSoundOn: Boolean
        get() = preferences.getBoolean(PREF_RAMADAN_NOTIFICATION_SOUND_ON, false)
        set(value) = preferences.edit {
            it.putBoolean(PREF_RAMADAN_NOTIFICATION_SOUND_ON, value)
        }


    var sehriOrifterAlertVibrationOn: Boolean
        get() = preferences.getBoolean(PREF_RAMADAN_NOTIFICATION_VIBRATION_ON, false)
        set(value) = preferences.edit {
            it.putBoolean(PREF_RAMADAN_NOTIFICATION_VIBRATION_ON, value)
        }

    var lastSavedSehriTimeMs: String?
        get() = preferences.getString(PREF_LAST_SET_SEHRI_ALARM_TIME, "")
        set(value) = preferences.edit {
            it.putString(PREF_LAST_SET_SEHRI_ALARM_TIME, value)
        }

    var lastSavedIfterTimeMs: String?
        get() = preferences.getString(PREF_LAST_SET_IFTER_ALARM_TIME, "")
        set(value) = preferences.edit {
            it.putString(PREF_LAST_SET_IFTER_ALARM_TIME, value)
        }
    var _ramadanSehriIfterTimes: MutableList<IfterAndSehriTime>? = mutableListOf()

    var ramadanSehriIfterTimes: MutableList<IfterAndSehriTime>?
        set(value) {
            _ramadanSehriIfterTimes = value
            /* val sehriIfterList = mGSonInstance.toJson(value)
             preferences.edit { it.putString(PREF_RAMADAN_SEHRI_IFTER_LIST, sehriIfterList) }*/
        }
        get() {
            return _ramadanSehriIfterTimes
            /* val sehriIfterList = preferences.getString(PREF_RAMADAN_SEHRI_IFTER_LIST, "")
             val type: Type = genericType<MutableList<IfterAndSehriTime>?>()
             return mGSonInstance.fromJson(sehriIfterList, type)*/
        }
    var _ramadanSehriIfterTimes2: MutableList<IftarAndSheriTimeforBD>? = mutableListOf()

    var ramadanSehriIfterTimes2: MutableList<IftarAndSheriTimeforBD>?
        set(value) {
            _ramadanSehriIfterTimes2 = value
            /* val sehriIfterList = mGSonInstance.toJson(value)
             preferences.edit { it.putString(PREF_RAMADAN_SEHRI_IFTER_LIST, sehriIfterList) }*/
        }
        get() {
            return _ramadanSehriIfterTimes2
            /* val sehriIfterList = preferences.getString(PREF_RAMADAN_SEHRI_IFTER_LIST, "")
             val type: Type = genericType<MutableList<IfterAndSehriTime>?>()
             return mGSonInstance.fromJson(sehriIfterList, type)*/
        }
    var _nextTenDaysSehriIfterTimes2: MutableList<IftarAndSheriTimeforBD>? = mutableListOf()
    var nextTenDaysSehriIfterTimes2: MutableList<IftarAndSheriTimeforBD>?
        set(value) {
            _nextTenDaysSehriIfterTimes2 = value
            /*val sehriIfterList = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST, sehriIfterList) }*/
        }
        get() {
            return _nextTenDaysSehriIfterTimes2
            /*val sehriIfterList = preferences.getString(PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST, "")
            val type: Type = genericType<MutableList<IfterAndSehriTime>?>()
            return mGSonInstance.fromJson(sehriIfterList, type)*/
        }
//    var lastSetSehriTimeInMs: Long
//        get() = preferences.getLong(PREF_LAST_SET_SEHRI_ALARM_TIME_MS, 0)
//        set(value) = preferences.edit { it.putLong(PREF_LAST_SET_SEHRI_ALARM_TIME_MS, value) }
//
//    var lastSetIfterTimeInMs: Long
//        get() = preferences.getLong(PREF_LAST_SET_IFTER_ALARM_TIME_MS, 0)
//        set(value) = preferences.edit { it.putLong(PREF_LAST_SET_IFTER_ALARM_TIME_MS, value) }

    var _nextTenDaysSehriIfterTimes: MutableList<IfterAndSehriTime>? = mutableListOf()
    var nextTenDaysSehriIfterTimes: MutableList<IfterAndSehriTime>?
        set(value) {
            _nextTenDaysSehriIfterTimes = value
            /*val sehriIfterList = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST, sehriIfterList) }*/
        }
        get() {
            return _nextTenDaysSehriIfterTimes
            /*val sehriIfterList = preferences.getString(PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST, "")
            val type: Type = genericType<MutableList<IfterAndSehriTime>?>()
            return mGSonInstance.fromJson(sehriIfterList, type)*/
        }


    fun getDownloadPath(id: String): String? {
        var path = downloadPathMap[id]
        return path
    }

    var downloadPathMap: HashMap<String, String?>
        set(value) {
            val mapStr = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_DOWNLOAD_PATH_MAP, mapStr) }
        }
        get() {
            val mapStr = preferences.getString(PREF_DOWNLOAD_PATH_MAP, null)
            if (mapStr == null) {
                return HashMap()
            } else {
                val type: Type = genericType<HashMap<String, String?>>()
                return mGSonInstance.fromJson(mapStr, type)
            }
        }

    fun updateDownloadProgress(id: String, value: Int) {
        val tempDownloadProgressMap = downloadProgressMap
        tempDownloadProgressMap.put(id, value)
        downloadProgressMap = tempDownloadProgressMap
    }

    fun getDownloadProgress(id: String): Int {
        var progress = downloadProgressMap[id] ?: -1
        return progress
    }

    var downloadProgressMap: HashMap<String, Int?>
        set(value) {
            val mapStr = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_DOWNLOAD_PROGRESS_MAP, mapStr) }
        }
        get() {
            val mapStr = preferences.getString(PREF_DOWNLOAD_PROGRESS_MAP, null)
            if (mapStr == null) {
                return HashMap()
            } else {
                val type: Type = genericType<HashMap<String, Int?>>()
                return mGSonInstance.fromJson(mapStr, type)
            }
        }

    fun clearCachedUser() {
        preferences.edit {
            it.remove(PREF_USER)
            it.remove(ISSUBWEEKLY)
            it.remove(ISSUBMONTHLY)
            it.remove(ISSUBMONTHLYGPAY)
            it.remove(ISSUBYEARLY)
            it.remove(ISSUBWEEKLYSOFTBUNDLE)
            it.remove(ISSUBMONTHLYSOFTBUNDLE)
            it.remove(ISSUBMONTHLYSOFTBUNDLEROBI)
            it.remove(ISSUBWEEKLYSOFTBUNDLEROBI)
            it.remove(ISSUBSOFTBUNDLEROBI)
            it.remove(ISSUBSOFTBUNDLERAMADANROBI)
            it.remove(ISSUBSOFTBUNDLESEVENDAYSROBI)
            it.remove(ISSUBSOFTBUNDLEFIFTEENDAYSROBI)
            it.remove(ISSUBIQRA)
            it.remove(ISSUBMONTHLYNAGAD)
            it.remove(ISSUBHALFYEARLYNAGAD)
            it.remove(ISSUBYEARLYNAGAD)
            it.remove(ISSUBMONTHLYSSL)
            it.remove(ISSUBHALFYEARLYSSL)
            it.remove(ISSUBYEARLYSSL)
            it.remove(ISFROMBD)
            it.remove(ISSUBQURAN)
        }
    }

    fun saveUserCurrentLocation(location: UserLocation) {
        val userCurLocString = mGSonInstance.toJson(location)
        preferences.edit { it.putString(USER_CURRENT_LOCATION, userCurLocString) }
    }

    fun getUserCurrentLocation(): UserLocation {
        val userCurLocString = preferences.getString(USER_CURRENT_LOCATION, "")
        Log.e("Error", ""+userCurLocString)
        if (userCurLocString == null || userCurLocString.length < 1) {
            val mLocation = UserLocation(
                23.8103,
                90.4125
            )
            return mLocation
        }
        val type: Type = genericType<UserLocation>()
        return mGSonInstance.fromJson(userCurLocString, type)
    }

    fun saveAllMalayNamazTime(callLog: List<List<Long>>, requestType: String) {
        val json = mGSonInstance.toJson(callLog)
        preferences.edit {
            it.putString(requestType, json)
        }
    }

    @Keep
    fun loadAllMalayNamazTime(requestType: String): List<List<Long>>? {
        var callLog: List<List<Long>>? = null
        try {
            val json = preferences.getString(requestType, "")
            if (json != null /*&& check month*/) {
                callLog = if (json.isEmpty()) {
                    ArrayList()
                } else {
                    val type = object : TypeToken<List<List<Long?>?>?>() {}.type
                    mGSonInstance.fromJson<List<List<Long>>>(json, type)
                }
            }
        } catch (e: JsonSyntaxException) {

        }
        return callLog
    }

    fun clearNamazTime(requestType: String) {
        preferences.edit {
            it.remove(requestType)
        }
    }

    var nitificationflag: Boolean
        get() = preferences.getBoolean(NOTIFICATION_FLAG.first, NOTIFICATION_FLAG.second)
        set(value) = preferences.edit {
            it.putBoolean(NOTIFICATION_FLAG.first, value)
        }

    var soundflag: Boolean
        get() = preferences.getBoolean(SOUND_FLAG.first, SOUND_FLAG.second)
        set(value) = preferences.edit {
            it.putBoolean(SOUND_FLAG.first, value)
        }

    var totalCount: Int
        get() = preferences.getInt(TOTAL_COUNT.first, TOTAL_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(TOTAL_COUNT.first, value)
        }

    fun cleartotalCount() {
        preferences.edit {
            it.remove(TOTALCOUNTTAG)
        }
    }

    fun setAlarmForAzan(value: Boolean, tag: String) {
        preferences.edit {
            it.putBoolean(tag, value)
        }
    }

    fun getAlarmForAzan(tag: String): Boolean {
        return preferences.getBoolean(tag, false)
    }


    fun saveTashbihCount(value: Int, tag: String) {
        preferences.edit {
            it.putInt(tag, value)
        }
    }


    fun loadTashbihCount(tag: String): Int {
        return preferences.getInt(tag, 0)
    }

    fun clearHistoryCount(tag: String) {
        preferences.edit {
            it.remove(tag)
        }
    }

    fun saveHajjGuideStep(value: Boolean, tag: String) {
        preferences.edit {
            it.putBoolean(tag + userNumber, value)
        }
    }

    fun loadHajjGuideStep(tag: String): Boolean {
        return preferences.getBoolean(tag, false)
    }

    var subWeekly: Boolean
        get() = preferences.getBoolean(
            IS_SUB_WEEKLY.first,
            IS_SUB_WEEKLY.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_WEEKLY.first, value)
        }
    var subMonthly: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_MONTHLY.first,
            IS_SUB_MONTHLY.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY.first, value)
        }
    var subYearly: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_YEARLY.first,
            IS_SUB_YEARLY.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_YEARLY.first, value)
        }

    var subMonthlyGpay: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_MONTHLY_GPAY.first,
            IS_SUB_MONTHLY_GPAY.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_GPAY.first, value)
        }

    var subWeeklySoftBundle: Boolean
        get() = preferences.getBoolean(
            IS_SUB_WEEKLY_SOFT_BUNDLE.first,
            IS_SUB_WEEKLY_SOFT_BUNDLE.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_WEEKLY_SOFT_BUNDLE.first, value)
        }

    var subMonthlySoftBundle: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_MONTHLY_SOFT_BUNDLE.first,
            IS_SUB_MONTHLY_SOFT_BUNDLE.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_SOFT_BUNDLE.first, value)
        }
    var subMonthlySoftBundleRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_MONTHLY_SOFT_BUNDLE_ROBI.first,
            IS_SUB_MONTHLY_SOFT_BUNDLE_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_SOFT_BUNDLE_ROBI.first, value)
        }
    var subWeeklySoftBundleRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_WEEKLY_SOFT_BUNDLE_ROBI.first,
            IS_SUB_WEEKLY_SOFT_BUNDLE_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_WEEKLY_SOFT_BUNDLE_ROBI.first, value)
        }
    var subSoftBundleRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_SOFT_BUNDLE_ROBI.first,
            IS_SUB_SOFT_BUNDLE_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_SOFT_BUNDLE_ROBI.first, value)
        }
    var subSoftBundleRamadanRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_SOFT_BUNDLE_RAMADAN_ROBI.first,
            IS_SUB_SOFT_BUNDLE_RAMADAN_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_SOFT_BUNDLE_RAMADAN_ROBI.first, value)
        }
    var subSoftBundleSevenDaysRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_SOFT_BUNDLE_SEVEN_DAYS_ROBI.first,
            IS_SUB_SOFT_BUNDLE_SEVEN_DAYS_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_SOFT_BUNDLE_SEVEN_DAYS_ROBI.first, value)
        }

    var subSoftBundleFifteenDaysRobi: Boolean
        get() = preferences.getBoolean(
            IS_SUB_SOFT_BUNDLE_FIFTEEN_DAYS_ROBI.first,
            IS_SUB_SOFT_BUNDLE_FIFTEEN_DAYS_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_SOFT_BUNDLE_FIFTEEN_DAYS_ROBI.first, value)
        }

    var subIqra: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_IQRA.first,
            IS_SUB_IQRA.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_IQRA.first, value)
        }

    var subQuran: Boolean
        get() = preferences.getBoolean(
            IS_SUB_QURAN.first,
            IS_SUB_QURAN.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_QURAN.first, value)
        }

    var subMonthlyNagad: Boolean
        get() = preferences.getBoolean(
            IS_SUB_MONTHLY_NAGAD.first,
            IS_SUB_MONTHLY_NAGAD.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_NAGAD.first, value)
        }

    var subHalfYearlyNagad: Boolean
        get() = preferences.getBoolean(
            IS_SUB_HALF_YEARLY_NAGAD.first,
            IS_SUB_HALF_YEARLY_NAGAD.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_HALF_YEARLY_NAGAD.first, value)
        }

    var subYearlyNagad: Boolean
        get() = preferences.getBoolean(
            IS_SUB_YEARLY_NAGAD.first,
            IS_SUB_YEARLY_NAGAD.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_YEARLY_NAGAD.first, value)
        }

    var subMonthlySsl: Boolean
        get() = preferences.getBoolean(
            IS_SUB_MONTHLY_SSL.first,
            IS_SUB_MONTHLY_SSL.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_SSL.first, value)
        }

    var subHalfYearlySsl: Boolean
        get() = preferences.getBoolean(
            IS_SUB_HALF_YEARLY_SSL.first,
            IS_SUB_HALF_YEARLY_SSL.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_HALF_YEARLY_SSL.first, value)
        }

    var subYearlySsl: Boolean
        get() = preferences.getBoolean(
            IS_SUB_YEARLY_SSL.first,
            IS_SUB_YEARLY_SSL.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_YEARLY_SSL.first, value)
        }

}