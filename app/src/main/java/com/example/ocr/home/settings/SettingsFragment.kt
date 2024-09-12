package com.example.ocr.home.settings

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBarDefaults.windowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ocr.base.BaseFragment
import com.example.ocr.databinding.FragmentSettingsBinding
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.switchPreference

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
class SettingsFragment :
    BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::class.java) {
    override fun setupUi() {
        binding.composeView.setContent {
            AppTheme {
                ProvidePreferenceLocals {
                    SettingsScreen()
                }
            }
        }
    }

    override fun setupObserver() {

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun SettingsScreen() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                val context = LocalContext.current
                val appLabel = "Settings"
                TopAppBar(
                    title = {
                        Text(
                            text = appLabel,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth() // Ensure the title takes up full width
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    windowInsets =
                    windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background),
            contentWindowInsets = windowInsets,
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding
            ) {
                item {
                    HorizontalDivider()
                    Text(
                        text = "General Settings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                }


                switchPreference(
                    key = "copy_mode",
                    defaultValue = false,
                    title = {
                        Text(
                            text = "Copy Mode",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = if (it) "On" else "Off",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )


                preference(
                    key = "how_to_use",
                    title = {
                        Text(
                            text = "Mastering Easy Copy: A Step-by-Step Guide",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = "Explore the latest guide on effectively using Easy Copy.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )


                switchPreference(
                    key = "notification",
                    defaultValue = false,
                    title = {
                        Text(
                            text = "Notification",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = if (it) "On" else "Off",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )

                switchPreference(
                    key = "theme",
                    defaultValue = false,
                    title = {
                        Text(
                            text = "Dark theme",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = if (it) "On" else "Off",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )

                item {
                    HorizontalDivider()
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                }

                listPreference(
                    key = "selection_order",
                    defaultValue = "Manual : The selected text will be arranged in the order it was picked",
                    values = listOf(
                        "Manual : The selected text will be arranged in the order it was picked",
                        "Dynamic :The selection order of the text will be reorganized according to its on-screen position"
                    ),
                    title = {
                        Text(
                            text = "Selection Order",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = { Text(text = it, style = MaterialTheme.typography.bodyMedium) },
                )

                listPreference(
                    key = "all_text_by_default",
                    defaultValue = "No, allow me to choose what I need",
                    values = listOf(
                        "No, allow me to choose what I need",
                        "Yes, automatically select all text areas during each capture"
                    ),
                    title = {
                        Text(
                            text = "Select all text by default",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = { Text(text = it, style = MaterialTheme.typography.bodyMedium) },
                )

                switchPreference(
                    key = "text_zone_preview",
                    defaultValue = false,
                    title = {
                        Text(
                            text = "Text Zone Preview",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = "Preview the text zones detected by Easy Copy",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )

                item {
                    HorizontalDivider()
                    Text(
                        text = "Shortcuts",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                }

                listPreference(
                    key = "double_tap",
                    defaultValue = "Copy",
                    values = listOf("None", "Copy", "Edit", "Share"),
                    title = {
                        Text(
                            text = "Double Tap",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = "Action on double tap : $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )

                listPreference(
                    key = "long_press",
                    defaultValue = "Copy",
                    values = listOf("None", "Copy", "Edit", "Share"),
                    title = {
                        Text(
                            text = "Long press",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    summary = {
                        Text(
                            text = "Action on Long press : $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )

                item {
                    HorizontalDivider()
                }
            }
        }
    }
}

