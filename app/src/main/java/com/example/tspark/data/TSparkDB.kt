package com.example.tspark.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Settings::class], version = 1, exportSchema = false)
abstract class TSparkDB : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var Instance: TSparkDB? = null

        fun getDatabase(context: Context): TSparkDB {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TSparkDB::class.java, "tspark_database")
                    .build().also { Instance = it }
            }
        }
    }
}