package com.mrlaughing.moyuan.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    val plants: LiveData<List<PlantStateEntity>> =
        plantRepository.observeAll().asLiveData()
}
