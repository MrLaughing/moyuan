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
import java.util.UUID
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

    private val _syncStatus = MutableLiveData<SyncStatus>(SyncStatus.Idle)
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    // 由 ViewModel 持有 WorkInfo liveData，在 Fragment 中通过 viewLifecycleOwner 观察
    private var currentWorkLiveData: LiveData<WorkInfo>? = null

    fun syncNow(): LiveData<WorkInfo>? {
        val workId = syncScheduler.triggerNow()
        _syncStatus.postValue(SyncStatus.Running)
        val liveData = syncScheduler.getWorkInfoByIdLiveData(workId)
        currentWorkLiveData = liveData
        return liveData
    }

    fun markSyncSuccess() {
        _syncStatus.postValue(SyncStatus.Success)
    }

    fun markSyncFailed() {
        _syncStatus.postValue(SyncStatus.Failed)
    }

    fun markApiKeyMissing() {
        _syncStatus.postValue(SyncStatus.ApiKeyMissing)
    }

    sealed class SyncStatus {
        data object Idle : SyncStatus()
        data object Running : SyncStatus()
        data object Success : SyncStatus()
        data object Failed : SyncStatus()
        data object ApiKeyMissing : SyncStatus()
    }
}
