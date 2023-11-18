package pl.bsotniczuk.hygrosense.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.bsotniczuk.hygrosense.data.AppDatabase
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem
import pl.bsotniczuk.hygrosense.data.repository.StatisticsRepository

class StatisticsViewModel(application: Application): AndroidViewModel(application) {

    private val repository: StatisticsRepository
    val statisticsItem: LiveData<StatisticsItem>
    val allStatisticsItems: LiveData<List<StatisticsItem>>
    val readAllStatisticsOrderByDate: LiveData<List<StatisticsItem>>

    init {
        val statisticsDao = AppDatabase.getDatabase(application).statisticsDao()
        repository = StatisticsRepository(statisticsDao)

        statisticsItem = repository.readStatisticsItem
        allStatisticsItems = repository.readAllStatistics
        readAllStatisticsOrderByDate = repository.readAllStatisticsOrderByDate
    }

    fun createStatisticsItem(statisticsItem: StatisticsItem) {
        viewModelScope.launch {
            repository.createStatisticsItem(statisticsItem)
        }
    }

    fun updateStatisticsItem(statisticsItem: StatisticsItem) {
        viewModelScope.launch {
            repository.updateStatisticsItem(statisticsItem)
        }
    }

    fun deleteStatisticsItem(statisticsItem: StatisticsItem) {
        viewModelScope.launch {
            repository.deleteStatisticsItem(statisticsItem)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}