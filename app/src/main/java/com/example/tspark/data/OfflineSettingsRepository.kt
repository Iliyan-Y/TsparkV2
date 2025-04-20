package com.example.tspark.data

import kotlinx.coroutines.flow.Flow

class OfflineSettingsRepository(private val settingsDao: SettingsDao) : SettingsRepository {

    override fun getSettingsStream(): Flow<Settings?> = settingsDao.getSettings()

    override suspend fun insertItem(item: Settings) = settingsDao.insert(item)
    
    override suspend fun updateItem(item: Settings) = settingsDao.update(item)

//    override suspend fun deleteItem(item: Settings) {
//        TODO("Not yet implemented")
//    }

    //    override fun getAllItemsStream(): Flow<List<Settings>> {
//        TODO("Not yet implemented")
//    }
}