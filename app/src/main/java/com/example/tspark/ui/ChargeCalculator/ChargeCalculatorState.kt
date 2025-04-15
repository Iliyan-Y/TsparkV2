package com.example.tspark.ui.ChargeCalculator

data class ChargeCalculatorState(
    val currentSOC: String = "0",
    val amps: String = "8",
    val power: Double = 0.0,
    val targetSOC: String = "80",
    val remainingHours: Long = 0L,
    val remainingMinutes: Long = 0L,
    val voltage: String = "200",
    val kWhNeeded: String = "0",
    val currentMaxRange: String = "266"
)

