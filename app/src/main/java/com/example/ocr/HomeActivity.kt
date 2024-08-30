package com.example.ocr

import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.ocr.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    companion object {
        const val START_FOREGROUND_SERVICE = 9999
        const val REQUEST_CODE_SCREEN_CAPTURE = 1001
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toggle.setOnClickListener {
            if (binding.toggle.isChecked) {
                if (checkAndRequestNotificationPermission()) {
                    startNotificationPostService()
                    finish()
                }
            } else {
                stopForegroundServiceAndDismissNotification()
            }
        }
    }

    private fun stopForegroundServiceAndDismissNotification() {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java)
        stopService(serviceIntent)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(ScreenCaptureService.NOTIFICATION_ID)

    }

    private fun startNotificationPostService() {
        val intent = Intent(this, ScreenCaptureService::class.java)
        startService(intent)
    }

    private fun checkAndRequestNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted(this)) {
                ActivityCompat.requestPermissions(this, arrayOf("android.permission.POST_NOTIFICATIONS"), START_FOREGROUND_SERVICE)
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            START_FOREGROUND_SERVICE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNotificationPostService()
                } else {
                    Toast.makeText(this, "Notification permission required to continue", Toast.LENGTH_SHORT).show()
                    binding.toggle.isChecked = false
                }
            }
            else -> {
                // Handle other request codes if needed
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == RESULT_OK && data != null) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_INTENT, data)
            }
            startScreenCaptureService(serviceIntent)
        } else {
            Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
            binding.toggle.isChecked = false
        }
    }

    private fun startScreenCaptureService(intent: Intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        finish()
    }

    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED
    }


    private fun showPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Notification Permission Required")
            .setMessage("Please enable notifications for this app to continue.")
            .setPositiveButton("Open Settings") { _, _ ->
                openNotificationSettings(context)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
