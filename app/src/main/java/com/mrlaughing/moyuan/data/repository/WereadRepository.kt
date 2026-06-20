package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.remote.WereadApiClient
import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDto
import com.mrlaughing.moyuan.data.remote.dto.RecommendDto
import com.mrlaughing.moyuan.data.remote.dto.SearchResultDto
import com.mrlaughing.moyuan.data.remote.dto.ShelfDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadRepository @Inject constructor(
    private val apiClient: WereadApiClient,
    private val userPrefs: UserPrefs
) {

    // 1. 阅读统计
    suspend fun fetchReadData(): Result<ReadDataDto> {
        return apiClient.fetchReadData("overall")
    }

    // 2. 书架
    suspend fun fetchShelf(): Result<ShelfDto> {
        return apiClient.fetchShelf()
    }

    // 3. 笔记和划线
    suspend fun fetchNotebooks(): Result<List<NotebookDto>> {
        return apiClient.fetchNotebooks()
    }

    // 4. 书籍搜索
    suspend fun searchBook(keyword: String, count: Int = 20): Result<SearchResultDto> {
        return apiClient.searchBook(keyword, count)
    }

    // 5. 推荐好书
    suspend fun fetchRecommendations(count: Int = 10): Result<RecommendDto> {
        return apiClient.fetchRecommendations(count)
    }

    // 是否配置了 API Key
    suspend fun isConfigured(): Boolean {
        return userPrefs.observeApiKey().first().orEmpty().isNotEmpty()
    }

    fun observeApiKey(): kotlinx.coroutines.flow.Flow<String?> = userPrefs.observeApiKey()
}
