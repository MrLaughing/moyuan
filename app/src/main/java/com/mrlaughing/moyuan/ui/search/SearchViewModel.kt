package com.mrlaughing.moyuan.ui.search

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
class SearchViewModel @Inject constructor(
    private val wereadRepository: WereadRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _books = MutableLiveData<List<SearchedBookDto>>(emptyList())
    val books: LiveData<List<SearchedBookDto>> = _books

    private val _keyword = MutableLiveData("")
    val keyword: LiveData<String> = _keyword

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _books.value = emptyList()
            _error.value = null
            return
        }
        _keyword.value = keyword
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = wereadRepository.searchBook(keyword)
            result.onSuccess {
                _books.value = it.books
                if (it.books.isEmpty()) {
                    _error.value = "未找到「$keyword」相关的书籍"
                }
            }.onFailure {
                _books.value = emptyList()
                _error.value = "搜索失败，请检查网络后重试"
            }
            _loading.value = false
        }
    }
}
