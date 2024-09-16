package com.example.ocr.screens.settings

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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.example.ocr.appTheme.AppTheme
import com.example.ocr.base.BaseFragment
import com.example.ocr.base.SharedPreference
import com.example.ocr.databinding.FragmentSettingsBinding
import com.example.ocr.screens.EasyCopyToggleEvents
import com.example.ocr.screens.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.switchPreference
import javax.inject.Inject

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::class.java) {

    companion object {
        const val COPY_MODE = "copy_mode"
        const val HOW_TO_USE = "how_to_use"
        const val THEME = "theme"
        const val SELECTION_ORDER = "selection_order"
        const val SELECT_ALL_TEXT_BY_DEFAULT = "all_text_by_default"
        const val TEXT_PREVIEW_ZONE = "text_zone_preview"
        const val DOUBLE_TAP = "double_tap"
        const val LONG_PRESS = "long_press"

    }


    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreference: SharedPreference
    override fun setupUi() {
        binding.composeView.setContent {
            AppTheme {
                ProvidePreferenceLocals {
                    SettingsScreen { event ->
                        handleClickEvent(event)
                    }
                }
            }
        }
    }

    override fun setupObserver() {

    }


    private fun handleClickEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.COPY_MODE -> {
                sharedViewModel.sendSettingEvents(
                    EasyCopyToggleEvents.ShowEasyCopyNotification(
                        sharedPreference.getBoolean(
                            COPY_MODE
                        )
                    )
                )
            }

            SettingsEvent.DARK_THEME -> {

            }

            SettingsEvent.SELECTION_ORDER -> {

            }

            SettingsEvent.SELECT_ALL_TEXT -> {

            }

            SettingsEvent.TEXT_ZONE_PREVIEW -> {

            }

            SettingsEvent.DOUBLE_TAP -> {

            }

            SettingsEvent.LONG_PRESS -> {

            }

            SettingsEvent.HOW_TO_USE -> {

            }
        }
    }


    enum class SettingsEvent {
        COPY_MODE,
        HOW_TO_USE,
        DARK_THEME,
        SELECTION_ORDER,
        SELECT_ALL_TEXT,
        TEXT_ZONE_PREVIEW,
        DOUBLE_TAP,
        LONG_PRESS
    }


    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun SettingsScreen(
        onClick: (event: SettingsEvent) -> Unit
    ) {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                val appLabel = "Settings"
                TopAppBar(
                    title = {
                        Text(
                            text = appLabel,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
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
                    key = COPY_MODE,
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
                        onClick(SettingsEvent.COPY_MODE)
                    },
                )


                preference(
                    key = HOW_TO_USE,
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
                    onClick = { onClick(SettingsEvent.HOW_TO_USE) }
                )


                switchPreference(
                    key = THEME,
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
                        onClick(SettingsEvent.DARK_THEME)
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
                    key = SELECTION_ORDER,
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
                    summary = {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        onClick(SettingsEvent.SELECTION_ORDER)
                    },
                )

                listPreference(
                    key = SELECT_ALL_TEXT_BY_DEFAULT,
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
                    summary = {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        onClick(SettingsEvent.SELECT_ALL_TEXT)
                    },
                )

                switchPreference(
                    key = TEXT_PREVIEW_ZONE,
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
                        onClick(SettingsEvent.TEXT_ZONE_PREVIEW)
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
                    key = DOUBLE_TAP,
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
                        onClick(SettingsEvent.DOUBLE_TAP)
                    },
                )

                listPreference(
                    key = LONG_PRESS,
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
                        onClick(SettingsEvent.LONG_PRESS)
                    },
                )

                item {
                    HorizontalDivider()
                }
            }
        }
    }
}

