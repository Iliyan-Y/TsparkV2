package com.example.tspark.ui.CalcDistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.Settings
import com.example.tspark.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalcDistanceState(
    val targetDistance: String = "0",
    val consumption: String = "0",
    val powerRequired: Double = 0.0,
    val percentage: Double = 0.0,
    val electricityPrice: Double = 0.0,
    val cost: Double = 0.0
)


class ViewModelCalcDistance(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalcDistanceState())
    val uiState: StateFlow<CalcDistanceState> = _uiState.asStateFlow()
    var settingsData: Settings? = null

    //todo adjust for battery degradation
    private var batteryCapacity = 0.0

    init {
        viewModelScope.launch {
            settingsRepository.getSettingsStream().collect { settings ->
                settings?.let {
                    settingsData = settings.copy()
                    batteryCapacity = it.currentBatteryCapacity
                    _uiState.update { prev ->
                        prev.copy(
                            consumption = it.carEfficiency.toString(),
                            electricityPrice = it.electricityPrice
                        )

                    }
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
        return (_uiState.value.targetDistance.toDouble() * _uiState.value.consumption.toDouble()) / 1000 // kWh
    }


    suspend fun calculate() {
        var powerRequired = calculatePowerRequired()
        var percentage = powerRequired / batteryCapacity * 100
        var totalPrice = (powerRequired * _uiState.value.electricityPrice) / 100

        _uiState.update { prev ->
            prev.copy(percentage = percentage, powerRequired = powerRequired, cost = totalPrice)
        }
        updateConsumption()
    }

    suspend fun updateConsumption() {
        if (settingsData == null) return

        settingsRepository.updateItem(settingsData!!.copy(carEfficiency = _uiState.value.consumption.toDouble()))
    }
}