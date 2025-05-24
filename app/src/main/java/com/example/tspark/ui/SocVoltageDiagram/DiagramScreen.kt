package com.example.tspark.ui.SocVoltageDiagram

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DiagramScreen(modifier: Modifier) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }

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

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..100f
        )
        Text(text = getLfpVoltage(sliderPosition).toString())
    }
}