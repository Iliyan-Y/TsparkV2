package com.example.tspark.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    /**
     * Retrieve all the items from the the given data source.
     */
    // fun getAllItemsStream(): Flow<List<Settings>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getSettingsStream(): Flow<Settings?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Settings)

    /**
     * Delete item from the data source
     */
    // suspend fun deleteItem(item: Settings)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Settings)
}