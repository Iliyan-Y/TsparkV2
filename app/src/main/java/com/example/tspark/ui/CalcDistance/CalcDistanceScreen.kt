package com.example.tspark.ui.CalcDistance

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
fun CalcDistanceScreen(
    modifier: Modifier,
    viewModel: ViewModelCalcDistance = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Calculate the power required to reach a target distance")

        TextField(
            value = uiState.targetDistance,
            onValueChange = { viewModel.setTargetDistance(it) },
            label = { Text("Target Distance") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = uiState.consumption,
            onValueChange = { viewModel.setConsumption(it) },
            label = { Text("Consumption in wH per 1 unit (km/mi)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (uiState.percentage > 0) {
            Text("Power required: ${String.format("%.2f", uiState.powerRequired)} kWh")
            Text("Percentage: ${String.format("%.2f", uiState.percentage)}%")
            Text("Total cost: ${String.format("%.2f", uiState.cost)}")
        }

        Button(onClick = {
            coroutineScope.launch {
                viewModel.calculate()
                keyboardController?.hide()
            }
        }) {
            Text(stringResource(R.string.calcBtn))
        }

    }
}