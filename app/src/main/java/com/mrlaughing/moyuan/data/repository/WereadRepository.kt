package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.remote.WereadApiClient
import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDetailResponse
import com.mrlaughing.moyuan.data.remote.dto.RecommendDto
import com.mrlaughing.moyuan.data.remote.dto.SearchResultDto
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
     * 获取阅读统计数据（详情）
     */
    suspend fun fetchReadData(): Result<ReadDataDetailResponse> {
        return apiClient.fetchReadData()
    }

    /**
     * 获取阅读统计数据（摘要）
     */
    suspend fun fetchReadDataSummary(): Result<com.mrlaughing.moyuan.data.remote.dto.ReadDataDto> {
        return apiClient.fetchReadDataSummary()
    }

    /**
     * 获取书架数据
     */
    suspend fun fetchShelf(): Result<ShelfSyncResponse> {
        return apiClient.fetchShelf()
    }

    /**
     * 获取笔记本列表
     */
    suspend fun fetchNotebooks(): Result<List<NotebookDto>> {
        return apiClient.fetchNotebooks()
    }

    /**
     * 获取书籍推荐
     */
    suspend fun fetchRecommendations(): Result<RecommendDto> {
        return apiClient.fetchRecommendations()
    }

    /**
     * 搜索书籍
     */
    suspend fun searchBook(keyword: String): Result<SearchResultDto> {
        return apiClient.searchBook(keyword)
    }

    /**
     * 是否配置了 API Key
     */
    suspend fun isConfigured(): Boolean {
        return userPrefs.observeApiKey().first().orEmpty().isNotEmpty()
    }

    fun observeApiKey(): kotlinx.coroutines.flow.Flow<String?> = userPrefs.observeApiKey()
}
