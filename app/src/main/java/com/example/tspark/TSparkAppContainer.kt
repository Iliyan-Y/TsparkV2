package com.example.tspark

import android.app.Application
import com.example.tspark.data.AppContainer
import com.example.tspark.data.AppDataContainer

class TSparkAppContainer : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}