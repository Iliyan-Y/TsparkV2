package com.example.tspark.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val batteryCapacity: Double,
    val initialRange: Double,
    val currentRange: Double,
)