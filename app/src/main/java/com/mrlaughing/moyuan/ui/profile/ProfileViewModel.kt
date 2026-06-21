package com.mrlaughing.moyuan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.repository.GardenRepository
import com.mrlaughing.moyuan.sync.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPrefs,
    private val gardenRepository: GardenRepository,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    val apiKey: LiveData<String?> =
        userPrefs.observeApiKey().asLiveData()

    val lastSync: LiveData<String?> =
        userPrefs.observeLastSyncDate().asLiveData()

    val totalMinutes: LiveData<Int> =
        gardenRepository.observeTotalMinutes().asLiveData()

    val plantCount: LiveData<Int> =
        gardenRepository.observeAllPlants().map { it.size }.asLiveData()

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            userPrefs.setApiKey(key)
            syncScheduler.triggerNow()
        }
    }

    fun syncNow() {
        syncScheduler.triggerNow()
    }
}
