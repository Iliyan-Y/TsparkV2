package com.example.tspark.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tspark.TSparkAppContainer
import com.example.tspark.ui.ChargeCalculator.ChargeCalculatorViewModel
import com.example.tspark.ui.Settings.SettingsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {

    val Factory = viewModelFactory {
        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(tSparkApplication().container.settingsRepository)
        }

        initializer {
            //val application = (this[APPLICATION_KEY] as TSparkAppContainer)
            val app = tSparkApplication()
            val settingsRepository = app.container.settingsRepository
            ChargeCalculatorViewModel(
                settingsRepository,
                app.userPreferencesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TSparkAppContainer].
 */
fun CreationExtras.tSparkApplication(): TSparkAppContainer =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TSparkAppContainer)