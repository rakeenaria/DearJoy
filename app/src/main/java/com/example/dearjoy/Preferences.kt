package com.example.dearjoy

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private val TAG_STATUS = "STATUS"
    private val TAG_LEVEL = "USER_ID"
    private val TAG_APP = "APP_PREFS"

    private val pref: SharedPreferences =
        context.getSharedPreferences(TAG_APP, Context.MODE_PRIVATE)

    var prefStatus: Boolean
        get() = pref.getBoolean(TAG_STATUS, false)
        set(value) = pref.edit().putBoolean(TAG_STATUS, value).apply()

    var preflevel: String?
        get() = pref.getString(TAG_LEVEL, null)
        set(value) = pref.edit().putString(TAG_LEVEL, value).apply()

    fun prefClear(){
        pref.edit().remove(TAG_STATUS).apply()
        pref.edit().remove(TAG_LEVEL).apply()
    }
}