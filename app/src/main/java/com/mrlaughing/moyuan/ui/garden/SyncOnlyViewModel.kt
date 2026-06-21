package com.mrlaughing.moyuan.ui.garden

import androidx.lifecycle.ViewModel
import com.mrlaughing.moyuan.sync.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncOnlyViewModel @Inject constructor(
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    fun syncNow() {
        syncScheduler.triggerNow()
    }
}
