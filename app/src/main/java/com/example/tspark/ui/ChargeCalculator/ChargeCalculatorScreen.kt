package com.example.tspark.ui.ChargeCalculator

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tspark.R
import com.example.tspark.ui.AppViewModelProvider


@SuppressLint("DefaultLocale")
@Composable
fun ChargeCalculatorScreen(
    viewModel: ChargeCalculatorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by viewModel.uiState.collectAsState()
    var showOptionalFields by remember { mutableStateOf(false) }

    //todo CALCULATE COST

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Show optional fields",
                    textAlign = TextAlign.End
                )
                Switch(
                    checked = showOptionalFields,
                    onCheckedChange = { showOptionalFields = it }
                )
            }
        }

        if (showOptionalFields) {
            TextField(
                value = uiState.voltage,
                onValueChange = {
                    viewModel.setVoltage(it)
                },
                label = { Text("Volt") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        }

        TextField(
            value = uiState.currentSOC,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    viewModel.setCurrentSOC(input)
                }
            },
            label = { Text("Enter current battery %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

        )

        TextField(
            value = uiState.amps,
            onValueChange = {
                viewModel.setAmp(it)
            },
            label = { Text("Amps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.targetSOC,
            onValueChange = {
                viewModel.setTargetSoc(it)
            },
            label = { Text("Target SOC %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = {
            viewModel.handleCalculateTime()
            keyboardController?.hide()
        }) {
            Text(stringResource(R.string.calcBtn))
        }

        if (uiState.power != 0.0) {
            Text("kW needed:  ${String.format("%.2f", uiState.kWhNeeded.toDouble())}")
            Text("Time remain: ${uiState.remainingHours} h ${uiState.remainingMinutes} m")
            Text("On ${uiState.power} kWh")
        }
    }

}