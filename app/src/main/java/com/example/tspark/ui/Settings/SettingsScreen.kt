package com.example.tspark.ui.Settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tspark.R
import com.example.tspark.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier
) {
    val uiState by settingsViewModel.settingsUiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Settings Screen")

        TextField(
            value = uiState.batteryCapacity,
            onValueChange = { settingsViewModel.setBatteryCapacity(it) },
            label = { Text("Battery Capacity kWh") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.initialRange,
            onValueChange = { settingsViewModel.setInitialRange(it) },
            label = { Text("Car factory rage") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.currentRange,
            onValueChange = { settingsViewModel.setCurrentRange(it) },
            label = { Text("Current Range") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.electricityPrice,
            onValueChange = { settingsViewModel.setElectricityPrice(it) },
            label = { Text("Electricity Price p/kWh") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.carEfficiency,
            onValueChange = { settingsViewModel.setCarEfficiency(it) },
            label = { Text("Efficiency wH/mi or km") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (uiState.degradationPercentage > 0) {
            Text(
                "Battery degradation: ${
                    String.format(
                        "%.2f",
                        uiState.degradationPercentage * 100
                    )
                }%"
            )
            Text(
                "Current battery capacity: ${
                    String.format(
                        "%.2f",
                        uiState.currentBatteryCapacity
                    )
                } kWh"
            )
        }

        Button(onClick = {
            coroutineScope.launch {
                settingsViewModel.saveSettings()
                keyboardController?.hide()
            }
        }) {
            Text(stringResource(R.string.save))
        }

    }
}