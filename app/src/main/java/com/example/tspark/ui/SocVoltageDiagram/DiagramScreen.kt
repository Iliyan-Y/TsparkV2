package com.example.tspark.ui.SocVoltageDiagram

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun DiagramScreen(modifier: Modifier) {
    var showOptionalFields by remember { mutableStateOf(false) }
    var soc by remember { mutableStateOf(0.5f) }
    val voltage = if (showOptionalFields) getNmcVoltage(soc) else getLfpVoltage(soc)
    var socInput by remember { mutableStateOf((soc * 100).roundToInt().toString()) }
    var showInput by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (showOptionalFields) "NMC" else "LFP",
                    textAlign = TextAlign.End
                )
                Switch(
                    checked = showOptionalFields,
                    onCheckedChange = { showOptionalFields = it }
                )
            }
        }

        Row() {
            if (showInput) {
                Text(
                    "SoC: ",
                    fontSize = 20.sp,
                    modifier = Modifier.clickable {
                        showInput = false
                    }

                )
                TextField(
                    value = socInput,
                    onValueChange = { socInput = it },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                )
                Text(
                    "%",
                    fontSize = 20.sp
                )
            } else {
                Text(
                    "SoC: ${(soc * 100).roundToInt()}%",
                    modifier = Modifier.clickable {
                        showInput = true
                    },
                    fontSize = 20.sp
                )
            }
        }

        Text("Voltage: ${"%.3f".format(voltage)} V", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        BatteryCanvas(soc = soc, onSocChange = { soc = it })
    }
}

@Composable
fun BatteryCanvas(soc: Float, onSocChange: (Float) -> Unit) {
    var dragFraction by remember { mutableStateOf(soc) }
    val animatedFill = animateFloatAsState(targetValue = dragFraction, label = "batteryFill")

    Canvas(
        modifier = Modifier
            .height(250.dp)
            .width(100.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    dragFraction = (dragFraction - dragAmount / 250f).coerceIn(0f, 1f)
                    onSocChange(dragFraction)
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // Outer battery container
        drawRoundRect(
            color = Color.DarkGray,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(20f, 20f),
            alpha = 0.2f
        )

        // Filled part
        drawRoundRect(
            color = Color.Green,
            topLeft = Offset(0f, height * (1 - animatedFill.value)),
            size = androidx.compose.ui.geometry.Size(width, height * animatedFill.value),
            cornerRadius = CornerRadius(20f, 20f)
        )
    }
}

fun getLfpVoltage(sliderValue: Float): Float {
    val soc = (sliderValue.coerceIn(0f, 1f)) * 100f

    return when {
        soc <= 10f -> 3.0f + (soc / 10f) * (3.20f - 3.0f)
        soc <= 20f -> 3.20f + ((soc - 10f) / 10f) * (3.30f - 3.20f)
        soc <= 50f -> 3.30f + ((soc - 20f) / 30f) * (3.35f - 3.30f)
        soc <= 80f -> 3.35f + ((soc - 50f) / 30f) * (3.40f - 3.35f)
        soc <= 90f -> 3.40f + ((soc - 80f) / 10f) * (3.45f - 3.40f)
        soc <= 95f -> 3.45f + ((soc - 90f) / 5f) * (3.50f - 3.45f)
        else -> 3.50f + ((soc - 95f) / 5f) * (3.65f - 3.50f)
    }
}

fun getNmcVoltage(sliderValue: Float): Float {
    val soc = (sliderValue.coerceIn(0f, 1f)) * 100f

    return when {
        soc <= 10f -> 3.0f + (soc / 10f) * (3.50f - 3.0f)
        soc <= 20f -> 3.50f + ((soc - 10f) / 10f) * (3.60f - 3.50f)
        soc <= 50f -> 3.60f + ((soc - 20f) / 30f) * (3.75f - 3.60f)
        soc <= 80f -> 3.75f + ((soc - 50f) / 30f) * (3.95f - 3.75f)
        soc <= 90f -> 3.95f + ((soc - 80f) / 10f) * (4.10f - 3.95f)
        else -> 4.10f + ((soc - 90f) / 10f) * (4.20f - 4.10f)
    }
}

