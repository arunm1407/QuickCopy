package com.example.ocr.screens

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocr.R
import com.example.ocr.databinding.ActivityHomeScreenBinding
import com.example.ocr.eventBus.EventBus
import com.example.ocr.screens.home.HomeScreenViewPagerAdapter
import com.example.ocr.base.binding.viewBinding
import com.example.ocr.screens.screenCapture.service.ScreenCaptureService
import com.example.ocr.util.PermissionHandlerUtil
import com.example.ocr.util.collectFlowWithLifecycle
import com.example.ocr.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {


    companion object {
        private const val PERMISSION_POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
    }

    @Inject
    lateinit var eventBus: EventBus
    private val sharedViewModel: SharedViewModel by viewModels()
    private val binding: ActivityHomeScreenBinding by viewBinding()
    private lateinit var screenCaptureResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private var mediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupActivityResultLaunchers()
        observeEventsFromViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        with(binding.viewPager) {
            adapter = HomeScreenViewPagerAdapter(this@HomeActivity)
            isUserInputEnabled = false
        }
        observeBottomNavbar()
    }

    private fun observeEventsFromViewModel(){
        with(sharedViewModel){
            collectFlowWithLifecycle(settingsEvent,::handleSettingsEvents)
        }
    }


    private fun handleSettingsEvents(events: EasyCopyToggleEvents){
        when(events){
            is EasyCopyToggleEvents.ToggleSelectionMode -> TODO()
            is EasyCopyToggleEvents.ToggleTheme -> TODO()
            is EasyCopyToggleEvents.ToggleVisibility -> TODO()
            is EasyCopyToggleEvents.TriggerGesture -> TODO()
            is EasyCopyToggleEvents.ShowEasyCopyNotification -> {
                handleNotificationEvent(events.isToShowNotification)
            }
        }
    }


    private fun handleNotificationEvent(isToShowNotification:Boolean){
        when(isToShowNotification){
            true -> {
                checkAndRequestPermissions()
            }
            false ->{
                mediaProjectionManager = null
                stopForegroundServiceAndDismissNotification()
            }
        }
    }

    private fun setupActivityResultLaunchers() {
        screenCaptureResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                startNotificationService(result.data!!)
            } else {
                sharedViewModel.setCopyMode(false)
                shortToast(R.string.permission_denied_for_screen_capture)
                deactivateEasyCopy()
            }
        }

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                requestScreenCapturePermission()
            } else {
                sharedViewModel.setCopyMode(false)
                shortToast(R.string.notification_permission_required_to_continue)
                deactivateEasyCopy()
            }
        }
    }

    private fun checkAndRequestPermissions() {

        mediaProjectionManager?.let {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionHandlerUtil.isNotificationPermissionGranted(this, PERMISSION_POST_NOTIFICATIONS)) {
                requestScreenCapturePermission()
            } else {
                notificationPermissionLauncher.launch(PERMISSION_POST_NOTIFICATIONS)
            }
        } else {
            requestScreenCapturePermission()
        }
    }

    private fun deactivateEasyCopy() {
//        sharedPreference.put(PREF_IS_EASY_COPY_ACTIVATED, false)
//        updateActivationButtonState(false)
    }

    private fun startNotificationService(projectionData: Intent) {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
            putExtra("media_projection_intent", projectionData)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun requestScreenCapturePermission() {
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent: Intent = mediaProjectionManager!!.createScreenCaptureIntent()
        screenCaptureResultLauncher.launch(captureIntent)
    }



    private fun stopForegroundServiceAndDismissNotification() {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java)
         stopService(serviceIntent)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(ScreenCaptureService.NOTIFICATION_ID)
    }



    private fun observeBottomNavbar() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val viewPager = binding.viewPager
            when (item.itemId) {
                R.id.home ->  viewPager.currentItem = 0
                R.id.history -> viewPager.currentItem = 1
                R.id.settings -> viewPager.currentItem = 2
                R.id.support -> viewPager.currentItem = 3
            }
            true
        }
    }
}