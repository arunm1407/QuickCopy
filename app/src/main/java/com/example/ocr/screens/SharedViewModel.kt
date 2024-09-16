package com.example.ocr.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Created by Arun @ak - 14213  on 15/09/24.
 */
class SharedViewModel : ViewModel() {


    private val _settingsEvent = Channel<EasyCopyToggleEvents>(Channel.CONFLATED)
    val settingsEvent = _settingsEvent.receiveAsFlow()


    private val _copyModeState = MutableStateFlow(false)
    val copyModeState: StateFlow<Boolean> = _copyModeState

    fun setCopyMode(enabled: Boolean) {
        _copyModeState.value = enabled
    }




    fun sendSettingEvents(event: EasyCopyToggleEvents){
        viewModelScope.launch {
            _settingsEvent.send(event)
        }
    }



}

sealed class EasyCopyToggleEvents {
    data class ShowEasyCopyNotification(val isToShowNotification: Boolean = true) : EasyCopyToggleEvents()
    data class ToggleVisibility(val target: ToggleTarget, val isEnabled: Boolean) : EasyCopyToggleEvents()
    data class ToggleTheme(val isDarkThemeEnabled: Boolean) : EasyCopyToggleEvents()
    data class ToggleSelectionMode(val isManualSelection: Boolean) : EasyCopyToggleEvents()
    data class TriggerGesture(val gesture: GestureType, val event: GestureAction) : EasyCopyToggleEvents()
}

enum class ToggleTarget {
    Notification,
    TextZonePreview
}

enum class GestureType {
    DoubleTap,
    LongPress
}

enum class GestureAction {
    None,
    Copy,
    Edit,
    Share;

    companion object {
        fun getEnumFromString(value: String): GestureAction {
            return runCatching { valueOf(value) }.getOrDefault(None)
        }
    }
}

data class TextCopyMetaData(
    val isSelectionOrderManual : Boolean,
    val shouldSelectAllText : Boolean,
    val shouldPreviewTextZone:Boolean,
    val onDoubleTap : GestureAction,
    val onLongPress :GestureAction
)

enum class SelectionOrder {
    MANUAL,
    DYNAMIC
}

