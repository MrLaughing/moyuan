package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.remote.WereadApiClient
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDetailResponse
import com.mrlaughing.moyuan.data.remote.dto.ShelfSyncResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadRepository @Inject constructor(
    private val apiClient: WereadApiClient,
    private val userPrefs: UserPrefs
) {

    /**
     * 获取阅读统计数据
     */
    suspend fun fetchReadData(): Result<ReadDataDetailResponse> {
        return apiClient.fetchReadData("overall")
    }

    /**
     * 获取书架数据
     */
    suspend fun fetchShelf(): Result<ShelfSyncResponse> {
        return apiClient.fetchShelf()
    }

    /**
     * 是否配置了 API Key
     */
    suspend fun isConfigured(): Boolean {
        return userPrefs.observeApiKey().first().orEmpty().isNotEmpty()
    }

    fun observeApiKey(): kotlinx.coroutines.flow.Flow<String?> = userPrefs.observeApiKey()
}
