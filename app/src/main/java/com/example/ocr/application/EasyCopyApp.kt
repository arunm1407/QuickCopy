package com.example.ocr.application

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
@HiltAndroidApp
class EasyCopyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("arun","EasyCopyApp application started")
    }
}