package pl.bsotniczuk.hygrosense.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.bsotniczuk.hygrosense.data.dao.SettingsDao
import pl.bsotniczuk.hygrosense.data.model.SettingsItem

class SettingsRepository(private val settingsDao: SettingsDao) {

    val readSettingsItem: LiveData<SettingsItem> = settingsDao.readSettingsItem()
    val readAllSettingsItems: LiveData<List<SettingsItem>> = settingsDao.readAllData()

    suspend fun createSettingsItem(settingsItem: SettingsItem) {
        return withContext(Dispatchers.IO) {
            settingsDao.createSettingsItem(settingsItem)
        }
    }

    suspend fun updateSettingsItem(settingsItem: SettingsItem) {
        return withContext(Dispatchers.IO) {
            settingsDao.updateSettingsItem(settingsItem)
        }
    }

    suspend fun deleteSettingsItem(settingsItem: SettingsItem) {
        return withContext(Dispatchers.IO) {
            settingsDao.deleteSettingsItem(settingsItem)
        }
    }

    suspend fun deleteAll() {
        return withContext(Dispatchers.IO) {
            settingsDao.deleteAll()
        }
    }

}