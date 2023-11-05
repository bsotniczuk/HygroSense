package pl.bsotniczuk.hygrosense.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.bsotniczuk.hygrosense.data.DbConstants
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem

@Dao
interface StatisticsDao {

    @Insert
    fun createSettingsItem(statisticsItem: StatisticsItem)

    @Update
    suspend fun updateSettingsItem(statisticsItem: StatisticsItem)

    @Delete
    suspend fun deleteSettingsItem(statisticsItem: StatisticsItem)

    @Query("DELETE FROM ${DbConstants.STATISTICS_TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY id ASC LIMIT 1")
    fun readStatisticsItem(): LiveData<StatisticsItem>

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY id ASC")
    fun readAllData(): LiveData<List<StatisticsItem>>

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY date DESC")
    fun readAllDataOrderByDate(): LiveData<List<StatisticsItem>>

}
