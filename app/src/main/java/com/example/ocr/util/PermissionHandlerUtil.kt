package com.example.ocr.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by Arun @ak - 14213  on 15/09/24.
 */
object PermissionHandlerUtil {

     fun isNotificationPermissionGranted(context: Context,permission:String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


}