package com.example.tspark.ui.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.Settings
import com.example.tspark.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

data class SettingsState(
    val batteryCapacity: String = "",
    val initialRange: String = "",
    val currentRange: String = "",
    val degradationPercentage: Double = 0.0,
    val currentBatteryCapacity: Double = 0.0
)

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _settingsUiState = MutableStateFlow(SettingsState())
    val settingsUiState: StateFlow<SettingsState> = _settingsUiState.asStateFlow()
    private var settingsId: Int? = null

    init {
        // Collect settings from the repository
        settingsRepository.getSettingsStream()
            .map { settings ->
                if (settings != null) {
                    settingsId = settings.id
                    // Update _settingsUiState with the values from the repository
                    _settingsUiState.update { currentState ->
                        currentState.copy(
                            batteryCapacity = settings.batteryCapacity.toString(),
                            initialRange = settings.initialRange.toString(),
                            currentRange = settings.currentRange.toString(),
                            degradationPercentage = settings.degradationPercentage,
                            currentBatteryCapacity = settings.currentBatteryCapacity
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun setBatteryCapacity(batteryCapacity: String) {
        _settingsUiState.update { prev ->
            prev.copy(batteryCapacity = batteryCapacity)
        }
    }

    fun setInitialRange(initialRange: String) {
        _settingsUiState.update { prev ->
            prev.copy(initialRange = initialRange)
        }
    }

    fun setCurrentRange(currentRange: String) {
        _settingsUiState.update { prev ->
            prev.copy(currentRange = currentRange)
        }
    }

    fun calculateBatteryDegradation(): Double {
        var initialRange = _settingsUiState.value.initialRange.toDouble()
        return (initialRange - _settingsUiState.value.currentRange.toDouble()) / initialRange
    }

    suspend fun saveSettings() {
        var degradationPercentage = calculateBatteryDegradation()
        var batteryCapacity = _settingsUiState.value.batteryCapacity.toDouble()

        var updatedItem = Settings(
            batteryCapacity = batteryCapacity,
            initialRange = _settingsUiState.value.initialRange.toDouble(),
            currentRange = _settingsUiState.value.currentRange.toDouble(),
            degradationPercentage = degradationPercentage,
            currentBatteryCapacity = batteryCapacity - batteryCapacity * degradationPercentage
        )

        if (settingsId != null) {
            settingsRepository.updateItem(updatedItem.copy(id = settingsId!!))
        } else {
            settingsRepository.insertItem(updatedItem)
        }
    }

}
