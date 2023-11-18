package pl.bsotniczuk.hygrosense.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.bsotniczuk.hygrosense.data.DbConstants
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem

@Dao
interface StatisticsDao {

    @Insert
    fun createStatisticsItem(statisticsItem: StatisticsItem)

    @Update
    suspend fun updateStatisticsItem(statisticsItem: StatisticsItem)

    @Delete
    suspend fun deleteStatisticsItem(statisticsItem: StatisticsItem)

    @Query("DELETE FROM ${DbConstants.STATISTICS_TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY id ASC LIMIT 1")
    fun readStatisticsItem(): LiveData<StatisticsItem>

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY id ASC")
    fun readAllStatistics(): LiveData<List<StatisticsItem>>

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY date DESC")
    fun readAllStatisticsOrderByDate(): LiveData<List<StatisticsItem>>

    @Query("SELECT * FROM ${DbConstants.STATISTICS_TABLE_NAME} ORDER BY date DESC LIMIT 1000")
    fun readAllStatisticsOrderByDateLimit1000(): LiveData<List<StatisticsItem>>

}
