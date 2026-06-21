package com.mrlaughing.moyuan.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDetailResponse
import com.mrlaughing.moyuan.data.repository.WereadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val wereadRepository: WereadRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _stats = MutableLiveData<ReadDataDetailResponse?>(null)
    val stats: LiveData<ReadDataDetailResponse?> = _stats

    fun loadStats() {
        viewModelScope.launch {
            _loading.value = true
            val result = wereadRepository.fetchReadData()
            result.onSuccess { _stats.value = it }.onFailure { _stats.value = null }
            _loading.value = false
        }
    }
}
