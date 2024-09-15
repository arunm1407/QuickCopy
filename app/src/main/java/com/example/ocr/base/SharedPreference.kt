package com.example.ocr.base

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreference @Inject constructor(
    @ApplicationContext private val context: Context,
    private val defaultSharedPreferences: SharedPreferences
) : BaseSharedPreference() {


    private fun getSharedPreference(preferenceName: String): SharedPreferences {
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    fun getDefaultEditor(): SharedPreferences.Editor {
        return defaultSharedPreferences.edit()
    }

    fun getEditor(preferenceName: String): SharedPreferences.Editor {
        return getSharedPreference(preferenceName).edit()
    }

    fun put(key: String, value: Long) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun put(key: String, value: Boolean) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun put(key: String, value: String?) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun put(key: String, value: Int) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun put(key: String, value: Set<String?>?) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun put(key: String, value: Array<String?>?) {
        basePut(key, value, defaultSharedPreferences)
    }

    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return baseGetInt(key, defaultValue, defaultSharedPreferences)
    }

    fun getSet(key: String): Set<String?>? {
        return baseGetSet(key, defaultSharedPreferences)
    }

    fun getString(key: String?): String? {
        return getString(key, null)
    }

    fun getString(key: String?, defaultValue: String? = null): String? {
        return baseGetString(key, defaultValue, defaultSharedPreferences)
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return baseGetBoolean(key, defaultValue, defaultSharedPreferences)
    }

    fun getLong(key: String): Long {
        return getLong(key, 0)
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return baseGetLong(key, defaultValue, defaultSharedPreferences)
    }

    fun getStringArray(key: String): Array<String?>? {
        return getStringArray(key, null)
    }

    fun getStringArray(key: String, defaultValue: Array<String?>? = null): Array<String?>? {
        return baseGetStringArray(key, defaultValue, defaultSharedPreferences)
    }

    fun removeSharedPreferenceKey(key: String) {
        baseRemoveSharedPreferenceKey(key, defaultSharedPreferences)
    }

    fun clearSharedPreference() {
        baseClearSharedPreference(defaultSharedPreferences)
    }

    fun clearSharedPrefsForOrg(orgId: String) {
        baseClearSharedPreference(getSharedPreference(orgId))
    }

    fun writeIntoSharedPrefsAndCommit(fromPrefs: SharedPreferences, toPrefs: SharedPreferences) {
        baseWriteIntoSharedPrefsAndCommit(fromPrefs, toPrefs)
    }

    fun isPreferenceEmpty(preferenceName: String): Boolean {
        return baseIsPreferenceEmpty(getSharedPreference(preferenceName))
    }

    fun appendToArray(key: String, appendArray: Array<String?>?) {
        baseAppendToArray(key, appendArray, defaultSharedPreferences)
    }

    fun isKeyExists(key: String): Boolean {
        return defaultSharedPreferences.contains(key)
    }

    fun clearSharedPref(preferenceName: String) {
        baseClearSharedPreference(getSharedPreference(preferenceName))
    }
}

object SharedPreferenceName {
    const val DEFAULT_SHARED_PREFERENCE = "com.example.ocr_preferences"
}