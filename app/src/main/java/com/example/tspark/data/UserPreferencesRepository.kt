package com.example.tspark.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val CURRENT_BATTERY_SOC = stringPreferencesKey("current_battery_soc")
        val TARGET_BATTERY_SOC = stringPreferencesKey("target_battery_soc")
        val AMS = stringPreferencesKey("amps")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveCurrentBatteryPreference(
        currentBattery: String,
        targetBattery: String,
        amps: String
    ) {
        dataStore.edit { preferences ->
            preferences[CURRENT_BATTERY_SOC] = currentBattery
            preferences[TARGET_BATTERY_SOC] = targetBattery
            preferences[AMS] = amps
        }
    }

    val currentBatterySoc: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[CURRENT_BATTERY_SOC] ?: "0"
    }

    val targetBatterySoc: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[TARGET_BATTERY_SOC] ?: "80"
    }

    val amps: Flow<String> = dataStore.data.handleDataStoreError().map { preferences ->
        preferences[AMS] ?: "8"
    }

    // Helper function to handle errors with default values
    private fun <T> Flow<T>.handleDataStoreError(): Flow<T> =
        catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences() as T)
            } else {
                throw it
            }
        }
}