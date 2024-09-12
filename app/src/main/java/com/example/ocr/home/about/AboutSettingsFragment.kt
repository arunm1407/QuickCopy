package com.example.ocr.home.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ocr.R
import com.example.ocr.base.BaseFragment
import com.example.ocr.databinding.FragmentAboutSettingsBinding
import com.example.ocr.home.settings.AppTheme
import me.zhanghai.compose.preference.ProvidePreferenceLocals

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
class AboutSettingsFragment :
    BaseFragment<FragmentAboutSettingsBinding>(FragmentAboutSettingsBinding::class.java) {
    companion object {
        const val PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.yourcompany.yourappname"
        const val PRIVACY_POLICY_URL = "https://github.com/arunm1406?tab=repositories"
    }

    override fun setupUi() {
        binding.composeView.setContent {
            AppTheme {
                ProvidePreferenceLocals {
                    AboutScreen { event ->
                        handleClickEvent(event)
                    }
                }
            }
        }
    }

    override fun setupObserver() {

    }

    private fun onShareClick() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this app: $PLAY_STORE_URL ")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun onPrivacyPolicyClick() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
        startActivity(browserIntent)
    }

    private fun onKnowledgeBaseClick() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
        startActivity(browserIntent)
    }

    private fun onAboutClick() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
        startActivity(browserIntent)
    }


    private fun handleClickEvent(event: AboutSettingClickEvent) {
        when (event) {
            AboutSettingClickEvent.ABOUT -> onAboutClick()
            AboutSettingClickEvent.KNOWLEDGE -> onKnowledgeBaseClick()
            AboutSettingClickEvent.PRIVACY -> onPrivacyPolicyClick()
            AboutSettingClickEvent.SHARE -> onShareClick()
        }
    }

    enum class AboutSettingClickEvent {
        ABOUT,
        KNOWLEDGE,
        PRIVACY,
        SHARE
    }


    @Composable
    fun AboutScreen(
        onClick: (event: AboutSettingClickEvent) -> Unit

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "About Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsItem(
                        icon = painterResource(id = R.drawable.ic_share),
                        text = "Share",
                        onClick = { onClick(AboutSettingClickEvent.SHARE) }
                    )

                    SettingsItem(
                        icon = painterResource(id = R.drawable.ic_shield),
                        text = "Privacy Policy",
                        onClick = { onClick(AboutSettingClickEvent.PRIVACY) }
                    )

                    SettingsItem(
                        icon = painterResource(id = R.drawable.ic_question),
                        text = "Knowledge Base",
                        onClick = { onClick(AboutSettingClickEvent.KNOWLEDGE) }
                    )

                    SettingsItem(
                        icon = painterResource(id = R.drawable.ic_sentiment),
                        text = "About Easy Copy",
                        onClick = { onClick(AboutSettingClickEvent.ABOUT) }
                    )
                }
            }
        }
    }


    @Composable
    fun SettingsItem(icon: Painter, text: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}






