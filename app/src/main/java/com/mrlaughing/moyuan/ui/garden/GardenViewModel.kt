package com.mrlaughing.moyuan.ui.garden

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.WorkInfo
import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.repository.GardenRepository
import com.mrlaughing.moyuan.sync.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val syncScheduler: SyncScheduler,
    private val userPrefs: UserPrefs
) : ViewModel() {

    val plants: LiveData<List<PlantStateEntity>> =
        gardenRepository.observeAllPlants().asLiveData()

    val meta: LiveData<GardenMetaEntity?> =
        gardenRepository.observeMeta().asLiveData()

    val totalMinutes: LiveData<Int> =
        gardenRepository.observeTotalMinutes().asLiveData()

    val lastSync: LiveData<String?> =
        userPrefs.observeLastSyncDate().asLiveData()

    private val _syncResult = MutableLiveData<SyncResult?>()
    val syncResult: LiveData<SyncResult?> = _syncResult

    fun syncNow() {
        syncScheduler.triggerNow().observeForever { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                _syncResult.value = when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> SyncResult.Success
                    WorkInfo.State.FAILED -> SyncResult.Failed
                    else -> null
                }
            }
        }
    }

    fun clearSyncResult() {
        _syncResult.value = null
    }

    sealed class SyncResult {
        data object Success : SyncResult()
        data object Failed : SyncResult()
    }
}
