package com.mrlaughing.moyuan.ui.study

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.data.repository.ReadStatsRepository
import com.mrlaughing.moyuan.data.repository.WereadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val statsRepository: ReadStatsRepository,
    private val wereadRepository: WereadRepository
) : ViewModel() {

    private var loaded = false

    val books: LiveData<List<BookTrackingEntity>> =
        statsRepository.observeBooks().asLiveData()

    val totalMinutes: LiveData<Int> =
        statsRepository.observeTotalMinutes().asLiveData()

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            // 拉取一次书架和统计，保持数据新鲜
            runCatching { wereadRepository.fetchShelf() }
            runCatching { wereadRepository.fetchReadData() }
        }
    }
}
