package com.example.tspark.ui.ChargeCalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tspark.data.Settings
import com.example.tspark.data.SettingsRepository
import com.example.tspark.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

class ChargeCalculatorViewModel(
    private val settingsRepository: SettingsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChargeCalculatorState())
    val uiState: StateFlow<ChargeCalculatorState> = _uiState.asStateFlow()

    // Tesla LFP battery usable capacity  57.5 kWh
    private var batteryCapacity = 57.5
    private var initialRange = 272.0 //mi
    private var currentMaxRange = initialRange  //assuming no battery degradation

    init {
        // done this way we can collect both the settings stream and the datastore values at the same time
        // it runs as coroutine so it doesn't block the UI thread
        viewModelScope.launch {
            // Collect settings stream
            launch {
                settingsRepository.getSettingsStream().collect { settings ->
                    if (settings != null) {
                        batteryCapacity = settings.currentBatteryCapacity
                        initialRange = settings.initialRange
                        currentMaxRange = settings.currentRange
                    } else {
                        // todo: move this initialization to the higher component as this might not be the first app screen
                        settingsRepository.insertItem(
                            Settings(
                                batteryCapacity = batteryCapacity,
                                initialRange = initialRange,
                                currentRange = currentMaxRange,
                                degradationPercentage = 0.0,
                                currentBatteryCapacity = batteryCapacity
                            )
                        )
                    }
                }
            }

            // Collect DataStore values
            launch {
                combine(
                    userPreferencesRepository.currentBatterySoc,
                    userPreferencesRepository.targetBatterySoc,
                    userPreferencesRepository.amps
                ) { currentBattery, targetBattery, amps ->
                    Triple(currentBattery, targetBattery, amps)
                }.collect { (currentBattery, targetBattery, amps) ->
                    _uiState.update { prev ->
                        prev.copy(
                            currentSOC = currentBattery,
                            targetSOC = targetBattery,
                            amps = amps
                        )
                    }
                }
            }
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

    //    Power (kW) = Voltage (V) x Amps (A) / 1000
    //    230V x 8A = 1840 Watts
    //    1840 Watts / 1000 = 1.84 kW
    //    1.84 kW x 1 hour = 1.84 kWh
    fun handleCalculateTime() {
        val powerKwh = _uiState.value.voltage.toDouble() * _uiState.value.amps.toInt() / 1000 // kWh
        val energyNeeded =
            (_uiState.value.targetSOC.toInt() - _uiState.value.currentSOC.toInt()) / 100.0 * batteryCapacity // kWh
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

        // save current battery state
        viewModelScope.launch {
            userPreferencesRepository.saveCurrentBatteryPreference(
                _uiState.value.currentSOC,
                _uiState.value.targetSOC,
                _uiState.value.amps
            )
        }

    }
}