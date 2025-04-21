package com.example.tspark.ui.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.Settings
import com.example.tspark.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SettingsState(
    val batteryCapacity: Double = 0.0,
    val initialRange: Double = 0.0
)

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settingsUiState: StateFlow<SettingsState> =
        settingsRepository.getSettingsStream()
            .map { it.toSettingsState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsState()
            )


}

/**
 * Extension function to convert [Settings] to [SettingsState].
 * It handles the case where the [Settings] object might be null.
 */
fun Settings?.toSettingsState(): SettingsState {
    return if (this != null) {
        SettingsState(
            batteryCapacity = batteryCapacity,
            initialRange = initialRange
        )
    } else {
        SettingsState() // Return default state when settings is null
    }
}
