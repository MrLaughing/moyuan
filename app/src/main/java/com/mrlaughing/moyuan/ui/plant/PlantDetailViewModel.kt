package com.mrlaughing.moyuan.ui.plant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.repository.PlantRepository
import com.mrlaughing.moyuan.engine.wither.WitherEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val witherEngine: WitherEngine,
    private val savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val plantId: String = savedStateHandle.get<String>("plantId") ?: ""

    private val _rebuildMessage = MutableLiveData<String?>()
    val rebuildMessage: LiveData<String?> = _rebuildMessage

    fun observePlant(): LiveData<PlantStateEntity?> =
        plantRepository.observeAll().map { list ->
            list.firstOrNull { p -> p.plantId == plantId }
        }.asLiveData()

    fun isRuined(plant: PlantStateEntity?): Boolean {
        return plant?.let { witherEngine.isRuined(it) } ?: false
    }

    fun rebuildRuins() {
        viewModelScope.launch {
            val plants = plantRepository.getAll()
            val target = plants.firstOrNull { it.plantId == plantId }
            if (target != null && witherEngine.isRuined(target)) {
                val rebuilt = witherEngine.rebuild(target)
                plantRepository.upsert(rebuilt)
                _rebuildMessage.value = "废墟已重建，植物从种子重新开始生长"
            } else if (target == null) {
                _rebuildMessage.value = "植物不存在"
            } else {
                _rebuildMessage.value = "此植物并非废墟状态"
            }
        }
    }

    fun consumeRebuildMessage() {
        _rebuildMessage.value = null
    }
}
