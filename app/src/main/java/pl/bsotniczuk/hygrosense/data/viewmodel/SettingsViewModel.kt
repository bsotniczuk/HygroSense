package pl.bsotniczuk.hygrosense.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.bsotniczuk.hygrosense.data.AppDatabase
import pl.bsotniczuk.hygrosense.data.model.SettingsItem
import pl.bsotniczuk.hygrosense.data.repository.SettingsRepository

class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val repository: SettingsRepository
    val settingsItem: LiveData<SettingsItem>
    val allSettingsItems: LiveData<List<SettingsItem>>

    init {
        val settingsDao = AppDatabase.getDatabase(application).settingsDao()
        repository = SettingsRepository(settingsDao)

        settingsItem = repository.readSettingsItem
        allSettingsItems = repository.readAllSettingsItems
    }

    fun createSettingsItem(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.createSettingsItem(settingsItem)
        }
    }

    fun updateSettingsItem(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.updateSettingsItem(settingsItem)
        }
    }

    fun deleteSettingsItem(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.deleteSettingsItem(settingsItem)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}