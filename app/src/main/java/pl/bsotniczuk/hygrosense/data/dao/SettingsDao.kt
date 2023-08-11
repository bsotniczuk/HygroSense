package pl.bsotniczuk.hygrosense.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.bsotniczuk.hygrosense.data.DbConstants
import pl.bsotniczuk.hygrosense.data.model.SettingsItem

@Dao
interface SettingsDao {

    @Insert
    fun createSettingsItem(settingsItem: SettingsItem)

    @Update
    suspend fun updateSettingsItem(settingsItem: SettingsItem)

    @Delete
    suspend fun deleteSettingsItem(settingsItem: SettingsItem)

    @Query("DELETE FROM ${DbConstants.SETTINGS_TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${DbConstants.SETTINGS_TABLE_NAME} ORDER BY id ASC LIMIT 1")
    fun readSettingsItem(): LiveData<SettingsItem>

    @Query("SELECT * FROM ${DbConstants.SETTINGS_TABLE_NAME} ORDER BY id ASC")
    fun readAllData(): LiveData<List<SettingsItem>>

}
