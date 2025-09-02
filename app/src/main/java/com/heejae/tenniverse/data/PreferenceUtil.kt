package com.heejae.tenniverse.data

import android.content.Context
import android.content.SharedPreferences
import com.heejae.tenniverse.util.PREFERENCE

class PreferenceUtil(
    private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE) }
    private val editor: SharedPreferences.Editor by lazy { prefs.edit() }

    fun getBoolean(key: String, defaultValue: Boolean)
        = prefs.getBoolean(key, defaultValue)
    fun getString(key: String, defaultValue: String?)
        = prefs.getString(key, defaultValue)
    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }
    fun setString(key: String, value: String) {
        editor.putString(key, value).apply()
    }
}