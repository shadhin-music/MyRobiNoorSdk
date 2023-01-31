package com.gakk.noorlibrary.data.prefs

//have to change with user data class

import android.content.Context
import android.content.SharedPreferences
import com.gakk.noorlibrary.model.UserLocation
import com.gakk.noorlibrary.model.billboard.Data
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.util.LAN_BANGLA
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object AppPreference {

    private lateinit var mGSonInstance: Gson

    private val whiteListNumber =
        arrayListOf("8801537673977")

    private const val PREF_FILE_NAME = "NoorPreference"
    private const val LANGUAGE = "Language"
    private const val USER_CURRENT_LOCATION = "userCurrentLocation"
    private const val USER_NUMBER = "userNumber"
    private const val PREF_USER = "user"
    private const val PREF_USER_INFO = "userInfo"

    private const val ISNOTIFICATIONON = "isNotificationOn"

    private const val PREF_RAMADAN_SEHRI_IFTER_LIST = "ramadanSehriIfterList"
    private const val PREF_NEXT_TEN_DAYS_SEHRI_IFTER_LIST = "nextTenDaysSehriIfterList"

    private const val PREF_RAMADAN_NOTIFICATION_SEHRI_ALERT_ON = "ramadanNotificationSehriAlertOn"
    private const val PREF_RAMADAN_NOTIFICATION_IFTER_ALERT_ON = "ramadanNotificationIfterAlertOn"
    private const val PREF_RAMADAN_NOTIFICATION_SOUND_ON = "ramadanNotificationSoundOn"
    private const val PREF_RAMADAN_NOTIFICATION_VIBRATION_ON = "ramadanNotificationVibrationOn"
    private const val PREF_LAST_SET_SEHRI_ALARM_TIME = "lastSavedSehriAlarmTime"
    private const val PREF_LAST_SET_IFTER_ALARM_TIME = "lastSavedIfterAlarmTime"


    private const val ISSUBDAILY = "is_sub_daily"
    private const val ISSUBFIFTEENDAYS = "is_sub_fifteendays"
    private const val ISSUBWEEKLYROBI = "is_sub_weekly_robi"
    private const val ISSUBMONTHLYROBI = "is_sub_monthly_robi"
    private val IS_SUB_WEEKLY_ROBI = Pair(ISSUBWEEKLYROBI, false)
    private val IS_SUB_MONTHLY_ROBI = Pair(ISSUBMONTHLYROBI, false)

    private const val ISSUBMONTHLYSSL = "is_sub_monthly_ssl"
    private const val ISSUBHALFYEARLYSSL = "is_sub_half_yearly_ssl"
    private const val ISSUBYEARLYSSL = "is_sub_yearly_ssl"
    private val IS_SUB_MONTHLY_SSL = Pair(ISSUBMONTHLYSSL, false)
    private val IS_SUB_HALF_YEARLY_SSL = Pair(ISSUBHALFYEARLYSSL, false)
    private val IS_SUB_YEARLY_SSL = Pair(ISSUBYEARLYSSL, false)

    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private lateinit var SET_LANGUAGE: Pair<String, String>

    private val NOTIFICATION_FLAG = Pair(ISNOTIFICATIONON, true)

    private val IS_SUB_DAILY = Pair(ISSUBDAILY, false)
    private val IS_SUB_FIFTEENDAYS = Pair(ISSUBFIFTEENDAYS, false)

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

    var userNumber: String? = null

    var userToken: String? = null
    var userId: String? = null

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


    fun clearCachedUser() {
        preferences.edit {
            it.remove(PREF_USER)
            it.remove(ISSUBDAILY)
            it.remove(ISSUBFIFTEENDAYS)
            it.remove(ISSUBMONTHLYSSL)
            it.remove(ISSUBHALFYEARLYSSL)
            it.remove(ISSUBYEARLYSSL)
        }
    }

    fun saveUserCurrentLocation(location: UserLocation) {
        val userCurLocString = mGSonInstance.toJson(location)
        preferences.edit { it.putString(USER_CURRENT_LOCATION, userCurLocString) }
    }

    fun getUserCurrentLocation(context: Context? = null): UserLocation {

        if ((!this::preferences.isInitialized || !this::mGSonInstance.isInitialized) && context != null) {
            init(context)

        }

        var userCurLocString: String? = null
        kotlin.runCatching {
            userCurLocString = preferences.getString(USER_CURRENT_LOCATION, "")
        }
        if (userCurLocString.isNullOrEmpty()) {
            val mLocation = UserLocation(
                23.8103, 90.4125
            )
            return mLocation
        }
        val type: Type = genericType<UserLocation>()
        return mGSonInstance.fromJson(userCurLocString, type)
    }

    var nitificationflag: Boolean
        get() = preferences.getBoolean(NOTIFICATION_FLAG.first, NOTIFICATION_FLAG.second)
        set(value) = preferences.edit {
            it.putBoolean(NOTIFICATION_FLAG.first, value)
        }


    fun saveHajjGuideStep(value: Boolean, tag: String) {
        preferences.edit {
            it.putBoolean(tag + userNumber, value)
        }
    }

    fun loadHajjGuideStep(tag: String): Boolean {
        return preferences.getBoolean(tag, false)
    }

    var subDaily: Boolean
        get() = preferences.getBoolean(
            IS_SUB_DAILY.first,
            IS_SUB_DAILY.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_DAILY.first, value)
        }

    var subFifteenDays: Boolean
        get() = preferences.getBoolean(
            IS_SUB_FIFTEENDAYS.first,
            IS_SUB_FIFTEENDAYS.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_FIFTEENDAYS.first, value)
        }

    var subWeeklyRobi: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_WEEKLY_ROBI.first,
            IS_SUB_WEEKLY_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_WEEKLY_ROBI.first, value)
        }

    var subMonthlyRobi: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
            IS_SUB_MONTHLY_ROBI.first,
            IS_SUB_MONTHLY_ROBI.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_SUB_MONTHLY_ROBI.first, value)
        }

    var subMonthlySsl: Boolean
        get() = if (whiteListNumber.contains(userNumber)) true else preferences.getBoolean(
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