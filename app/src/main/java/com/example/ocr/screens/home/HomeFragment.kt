package com.example.ocr.screens.home

import android.content.Intent
import android.content.res.ColorStateList
import android.media.projection.MediaProjectionManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.ocr.R
import com.example.ocr.screens.screenCapture.service.ScreenCaptureService
import com.example.ocr.base.BaseFragment
import com.example.ocr.base.SharedPreference
import com.example.ocr.databinding.FragmentHomeBinding
import com.example.ocr.screens.EasyCopyToggleEvents
import com.example.ocr.screens.SharedViewModel
import com.example.ocr.screens.history.RecentCopiesAdapter
import com.example.ocr.screens.mockDataProvider.MockDataProvider
import com.example.ocr.screens.settings.SettingsFragment
import com.example.ocr.screens.settings.SettingsFragment.Companion.COPY_MODE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::class.java) {

    companion object {
        private const val PREF_IS_EASY_COPY_ACTIVATED = "PREF_IS_EASY_COPY_ACTIVATED"
    }

    private val viewModel : SharedViewModel by activityViewModels()
    private lateinit var clipboardAdapter: RecentCopiesAdapter

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun setupUi() {
        clipboardAdapter = RecentCopiesAdapter()
        binding.recentCopiesList.adapter = clipboardAdapter
        clipboardAdapter.submitList(MockDataProvider.recentCopies)
        initiateActivationButtonState()
    }

    override fun setupObserver() {
        binding.activateButton.setOnClickListener {
            val isActive = !sharedPreference.getBoolean(PREF_IS_EASY_COPY_ACTIVATED, false)
            if (isActive) {
               viewModel.sendSettingEvents(EasyCopyToggleEvents.ShowEasyCopyNotification())
            } else {
                stopForegroundServiceAndDismissNotification()
            }
            sharedPreference.put(PREF_IS_EASY_COPY_ACTIVATED, isActive)
            updateActivationButtonState(isActive)
        }
    }



    private fun initiateActivationButtonState() {
        binding.activateButton.isEnabled = true
        val isActivated = sharedPreference.getBoolean(PREF_IS_EASY_COPY_ACTIVATED, false)
        updateActivationButtonState(isActivated)
    }


    private fun stopForegroundServiceAndDismissNotification() {
        val serviceIntent = Intent(requireContext(), ScreenCaptureService::class.java)
        requireActivity().stopService(serviceIntent)
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.cancel(ScreenCaptureService.NOTIFICATION_ID)
    }

    private fun updateActivationButtonState(isActivated: Boolean) {
        val colorResId = if (isActivated) R.color.blue_disabled else R.color.blue
        val textResId = if (isActivated) R.string.deactivate_easy_copy else R.string.activate_easy_copy

        binding.activateButton.apply {
            text = getString(textResId)
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorResId))
        }
    }


}