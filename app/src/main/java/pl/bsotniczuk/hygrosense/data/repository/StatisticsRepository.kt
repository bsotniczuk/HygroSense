package pl.bsotniczuk.hygrosense.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.bsotniczuk.hygrosense.data.dao.StatisticsDao
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem

class StatisticsRepository(private val statisticsDao: StatisticsDao) {

    val readStatisticsItem: LiveData<StatisticsItem> = statisticsDao.readStatisticsItem()
    val readAllStatistics: LiveData<List<StatisticsItem>> = statisticsDao.readAllStatistics()
    val readAllStatisticsOrderByDate: LiveData<List<StatisticsItem>> = statisticsDao.readAllStatisticsOrderByDate()
    val readAllStatisticsOrderByDateLimit1000: LiveData<List<StatisticsItem>> = statisticsDao.readAllStatisticsOrderByDateLimit1000()

    suspend fun createStatisticsItem(statisticsItem: StatisticsItem) {
        return withContext(Dispatchers.IO) {
            statisticsDao.createStatisticsItem(statisticsItem)
        }
    }

    suspend fun updateStatisticsItem(statisticsItem: StatisticsItem) {
        return withContext(Dispatchers.IO) {
            statisticsDao.updateStatisticsItem(statisticsItem)
        }
    }

    suspend fun deleteStatisticsItem(statisticsItem: StatisticsItem) {
        return withContext(Dispatchers.IO) {
            statisticsDao.deleteStatisticsItem(statisticsItem)
        }
    }

    suspend fun deleteAll() {
        return withContext(Dispatchers.IO) {
            statisticsDao.deleteAll()
        }
    }

}