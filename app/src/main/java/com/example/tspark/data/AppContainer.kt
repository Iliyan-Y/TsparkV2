package com.example.tspark.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val settingsRepository: SettingsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val settingsRepository: SettingsRepository by lazy {
        OfflineSettingsRepository(TSparkDB.getDatabase(context).settingsDao())
    }
}