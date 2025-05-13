package com.example.tspark.ui.CalcDistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CalcDistanceState(
    val targetDistance: String = "0",
    val consumption: String = "0",
    val powerRequired: String = "0",
    val percentage: String = "0",
)


class ViewModelCalcDistance(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalcDistanceState())
    val uiState: StateFlow<CalcDistanceState> = _uiState.asStateFlow()

    //todo adjust for battery degradation
    val batteryCapacity: StateFlow<Double> = settingsRepository.getSettingsStream()
        .map { settings -> settings?.batteryCapacity ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Example: stop collecting after 5 seconds of inactivity
            initialValue = 0.0
        )

    fun setTargetDistance(targetDistance: String) {
        _uiState.update { prev ->
            prev.copy(targetDistance = targetDistance)
        }
    }

    fun setConsumption(consumption: String) {
        _uiState.update { prev ->
            prev.copy(consumption = consumption)
        }
    }

    fun calculatePowerRequired() {
        val powerRequired =
            (_uiState.value.targetDistance.toInt() * _uiState.value.consumption.toInt()) / 1000 // kWh
        _uiState.update { prev ->
            prev.copy(powerRequired = powerRequired.toString())
        }
    }

    fun calculatePercentage() {
        calculatePowerRequired()
        val percentage = _uiState.value.powerRequired.toDouble() / batteryCapacity.value * 100
        _uiState.update { prev ->
            prev.copy(percentage = percentage.toString())
        }
    }
}