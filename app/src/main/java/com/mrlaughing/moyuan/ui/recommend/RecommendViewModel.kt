package com.mrlaughing.moyuan.ui.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.remote.dto.SearchedBookDto
import com.mrlaughing.moyuan.data.repository.WereadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val wereadRepository: WereadRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _books = MutableLiveData<List<SearchedBookDto>>(emptyList())
    val books: LiveData<List<SearchedBookDto>> = _books

    private val _reason = MutableLiveData("")
    val reason: LiveData<String> = _reason

    fun loadRecommendations() {
        viewModelScope.launch {
            _loading.value = true
            val result = wereadRepository.fetchRecommendations()
            result.onSuccess {
                _books.value = it.books
                _reason.value = it.reason.ifBlank { "从你的阅读偏好出发，为你挑选的好书。" }
            }.onFailure {
                _books.value = emptyList()
                _reason.value = "暂未获取到推荐"
            }
            _loading.value = false
        }
    }
}
