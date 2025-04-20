package com.example.tspark.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Settings)

    @Update
    suspend fun update(item: Settings)

    @Query("SELECT * from settings ORDER BY id DESC LIMIT 1")
    fun getSettings(): Flow<Settings>

//    @Delete
//    suspend fun delete(item: Settings)

//    @Query("SELECT * from settings WHERE id = :id")
//    fun getItem(id: Int): Flow<Settings>

//    @Query("SELECT * from settings ORDER BY id ASC")
//    fun getAllItems(): Flow<List<Settings>>
}