package com.mrlaughing.moyuan.ui.notebook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.data.repository.WereadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val wereadRepository: WereadRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _notebooks = MutableLiveData<List<NotebookDto>>(emptyList())
    val notebooks: LiveData<List<NotebookDto>> = _notebooks

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadNotebooks() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            val result = wereadRepository.fetchNotebooks()
            result.onSuccess { _notebooks.value = it }.onFailure { _errorMessage.value = it.message }
            _loading.value = false
        }
    }
}
