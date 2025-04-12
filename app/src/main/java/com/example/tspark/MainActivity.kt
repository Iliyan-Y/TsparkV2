package com.example.tspark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tspark.ui.theme.TSparkTheme
import kotlin.time.Duration.Companion.hours

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val focusManager = LocalFocusManager.current
            TSparkTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }) { innerPadding ->
                    CalculatorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .padding(innerPadding)

                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorView(modifier: Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current


    var currentSOC by remember { mutableStateOf("0") }
    var amps by remember { mutableStateOf("8") }
    var power by remember { mutableDoubleStateOf(0.0) }
    var targetSOC by remember { mutableStateOf("80") }
    var remainingHours by remember { mutableStateOf(0L) }
    var remainingMinutes by remember { mutableStateOf(0L) }
    var voltage by remember { mutableStateOf("200") } // default to 200 as usually we charge 1.6 kWh
    // Tesla LFP battery usable capacity  57.5 kWh
    val batteryCapacity = 57.5

    var kWhNeeded by remember { mutableStateOf("0") }

    //battery degradation
    var currentMaxRange by remember { mutableStateOf("266") } //mi
    fun calculateBatteryDegradation(): Double {
        val initialRange = 272 //mi

        return (currentMaxRange.toInt() - initialRange) / initialRange.toDouble()
    }

    //    Power (kW) = Voltage (V) x Amps (A) / 1000
    //    230V x 8A = 1840 Watts
    //    1840 Watts / 1000 = 1.84 kW
    //    1.84 kW x 1 hour = 1.84 kWh
    fun handleCalculateTime() {
        val effectiveCapacity = batteryCapacity * (1 - calculateBatteryDegradation())
        power = voltage.toDouble() * amps.toInt() / 1000 // kWh
        val energyNeeded =
            (targetSOC.toInt() - currentSOC.toInt()) / 100.0 * effectiveCapacity // kWh
        kWhNeeded = energyNeeded.toString()
        val timeHours = energyNeeded / power
        val duration = timeHours.hours
        remainingHours = duration.inWholeHours
        remainingMinutes = (duration - remainingHours.hours).inWholeMinutes

        keyboardController?.hide()
    }

    //todo add also cost to the result
    // make cost and voltage optional params to show field by toggle otherwise use the default

    var showOptionalFields by remember { mutableStateOf(false) }

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
                value = currentMaxRange,
                onValueChange = { currentMaxRange = it },
                label = { Text("Current Max Range at 100%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = voltage,
                onValueChange = {
                    voltage = it
                },
                label = { Text("Volt") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        }


        TextField(
            value = currentSOC,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    currentSOC = input
                }
            },
            label = { Text("Enter current battery %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

        )

        TextField(
            value = amps,
            onValueChange = {
                amps = it
            },
            label = { Text("Amps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )


        TextField(
            value = targetSOC,
            onValueChange = {
                targetSOC = it
            },
            label = { Text("Target SOC %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = { handleCalculateTime() }) {
            Text(stringResource(R.string.calcBtn))
        }

        if (power != 0.0) {
            Text(
                "Battery degradation: ${
                    String.format(
                        "%.2f",
                        calculateBatteryDegradation() * 100
                    )
                }%"
            )
            Text("kW needed:  ${String.format("%.2f", kWhNeeded.toDouble())}")
            Text("Time remain: $remainingHours h $remainingMinutes m")
            Text("On $power kWh")
        }
    }

}