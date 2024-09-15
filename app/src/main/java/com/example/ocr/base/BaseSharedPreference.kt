package com.example.ocr.base

import android.content.SharedPreferences


/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
open class BaseSharedPreference {

    protected fun basePut(key: String, value: Long, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putLong(key, value)
        sharedPrefEditor.apply()
    }

    protected fun basePut(key: String, value: Boolean, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putBoolean(key, value)
        sharedPrefEditor.apply()
    }

    protected fun basePut(key: String, value: String?, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putString(key, value)
        sharedPrefEditor.apply()
    }

    protected fun basePut(key: String, value: Int, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putInt(key, value)
        sharedPrefEditor.apply()
    }

    protected fun basePut(key: String, value: Set<String?>?, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putStringSet(key, value)
        sharedPrefEditor.apply()
    }

    protected fun basePut(key: String, array: Array<String?>?, sharedPreference: SharedPreferences) {
        if (null == array) {
            return
        }
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.putInt(key + "_size", array.size)
        for (i in array.indices) {
            sharedPrefEditor.putString(key + "_" + i, array[i])
        }
        sharedPrefEditor.apply()
    }

    protected fun baseGetInt(key: String, defaultValue: Int = 0, sharedPreference: SharedPreferences): Int {
        return sharedPreference.getInt(key, defaultValue)
    }

    protected fun baseGetSet(key: String, sharedPreference: SharedPreferences): Set<String?>? {
        return sharedPreference.getStringSet(key, null)
    }

    protected fun baseGetString(key: String?, defaultValue: String? = null, sharedPreference: SharedPreferences): String? {
        return sharedPreference.getString(key, defaultValue)
    }

    protected fun baseGetBoolean(key: String, defaultValue: Boolean = false, sharedPreference: SharedPreferences): Boolean {
        return sharedPreference.getBoolean(key, defaultValue)
    }

    protected fun baseGetLong(key: String, defaultValue: Long = 0, sharedPreference: SharedPreferences): Long {
        return sharedPreference.getLong(key, defaultValue)
    }

    protected fun baseGetStringArray(key: String, defaultValue: Array<String?>? = null, sharedPreference: SharedPreferences): Array<String?>? {
        val size = sharedPreference.getInt(key + "_size", -1) // No I18N
        if (size == -1) {
            return defaultValue
        }
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) {
            array[i] = sharedPreference.getString(key + "_" + i, null) // No I18N
        }
        return array
    }

    protected fun baseRemoveSharedPreferenceKey(key: String, sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.remove(key)
        sharedPrefEditor.apply()
    }

    protected fun baseClearSharedPreference(sharedPreference: SharedPreferences) {
        val sharedPrefEditor = sharedPreference.edit()
        sharedPrefEditor.clear()
        sharedPrefEditor.apply()
    }

    protected fun baseWriteIntoSharedPrefsAndCommit(fromPrefs: SharedPreferences, toPrefs: SharedPreferences) {
        val sharedPrefValues = fromPrefs.all
        val toEditor = toPrefs.edit()
        for ((key, value) in sharedPrefValues) {
            putValue(toEditor, key, value)
        }
        toEditor.apply()
    }

    protected fun baseIsPreferenceEmpty(sharedPreference: SharedPreferences): Boolean {
        return sharedPreference.all.isEmpty()
    }

    protected fun baseAppendToArray(key: String, appendArray: Array<String?>?, sharedPreference: SharedPreferences) {
        if (null != appendArray) {
            val array = baseGetStringArray(key, null, sharedPreference)
            if (array != null) {
                val currentArraySize = array.size
                val editor = sharedPreference.edit()
                editor.putInt(key + "_size", currentArraySize + appendArray.size) // No I18N
                for (i in currentArraySize until currentArraySize + appendArray.size) {
                    editor.putString(key + "_" + i, appendArray[currentArraySize - i]) // No I18N
                }
                editor.apply()
            } else {
                basePut(key, appendArray, sharedPreference)
            }
        }
    }

    protected fun baseIsPreferenceContainsKey(key: String, sharedPreference: SharedPreferences): Boolean {
        return sharedPreference.contains(key)
    }

    private fun putValue(toEditor: SharedPreferences.Editor, key: String, value: Any?) {
        when (value) {
            is String? -> toEditor.putString(key, value)
            is Int -> toEditor.putInt(key, value)
            is Long -> toEditor.putLong(key, value)
            is Boolean -> toEditor.putBoolean(key, value)
            is Float -> toEditor.putFloat(key, value)
            is Set<*>? -> toEditor.putStringSet(key, value as Set<String>?)
        }
    }
}