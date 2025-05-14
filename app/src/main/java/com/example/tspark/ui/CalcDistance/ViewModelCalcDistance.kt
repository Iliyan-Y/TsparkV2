package com.example.tspark.ui.CalcDistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private var batteryCapacity = 0.0

    init {
        viewModelScope.launch {
            settingsRepository.getSettingsStream().collect { settings ->
                settings?.let {
                    batteryCapacity = it.currentBatteryCapacity
                }
            }
        }
    }

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

    fun calculatePowerRequired(): Double {
        return (_uiState.value.targetDistance.toInt() * _uiState.value.consumption.toInt()).toDouble() / 1000 // kWh
    }


    fun calculatePercentage() {
        var powerRequired = calculatePowerRequired()
        var percentage = powerRequired / batteryCapacity * 100
        _uiState.update { prev ->
            prev.copy(percentage = percentage.toString(), powerRequired = powerRequired.toString())
        }
    }
}