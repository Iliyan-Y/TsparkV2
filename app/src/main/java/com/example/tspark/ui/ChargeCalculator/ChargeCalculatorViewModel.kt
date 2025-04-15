package com.example.tspark.ui.ChargeCalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.hours

class ChargeCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChargeCalculatorState())
    val uiState: StateFlow<ChargeCalculatorState> = _uiState.asStateFlow()

    // Tesla LFP battery usable capacity  57.5 kWh
    private val batteryCapacity = 57.5

    fun setCurrentMaxRange(currentMaxRange: String) {
        _uiState.update { prev ->
            prev.copy(currentMaxRange = currentMaxRange)
        }

    }

    fun setVoltage(voltage: String) {
        _uiState.update { prev ->
            prev.copy(voltage = voltage)
        }
    }

    fun setCurrentSOC(currentSOC: String) {
        _uiState.update { prev ->
            prev.copy(currentSOC = currentSOC)
        }
    }

    fun setAmp(amps: String) {
        _uiState.update { prev ->
            prev.copy(amps = amps)
        }
    }

    fun setTargetSoc(targetSOC: String) {
        _uiState.update { prev ->
            prev.copy(targetSOC = targetSOC)
        }
    }

    fun calculateBatteryDegradation(): Double {
        val initialRange = 272.0 //mi

        return (initialRange - _uiState.value.currentMaxRange.toInt()) / initialRange
    }

    //    Power (kW) = Voltage (V) x Amps (A) / 1000
    //    230V x 8A = 1840 Watts
    //    1840 Watts / 1000 = 1.84 kW
    //    1.84 kW x 1 hour = 1.84 kWh
    fun handleCalculateTime() {
        val effectiveCapacity = batteryCapacity - batteryCapacity * calculateBatteryDegradation()

        val powerKwh = _uiState.value.voltage.toDouble() * _uiState.value.amps.toInt() / 1000 // kWh
        val energyNeeded =
            (_uiState.value.targetSOC.toInt() - _uiState.value.currentSOC.toInt()) / 100.0 * effectiveCapacity // kWh
        val timeHours = energyNeeded / powerKwh
        val duration = timeHours.hours

        _uiState.update { prev ->
            prev.copy(
                power = powerKwh,
                kWhNeeded = energyNeeded.toString(),
                remainingHours = duration.inWholeHours,
                remainingMinutes = (duration - duration.inWholeHours.hours).inWholeMinutes
            )
        }

    }
}