package com.mrlaughing.moyuan.data.remote

import com.google.gson.Gson
import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDto
import com.mrlaughing.moyuan.data.remote.dto.RecommendDto
import com.mrlaughing.moyuan.data.remote.dto.SearchResultDto
import com.mrlaughing.moyuan.data.remote.dto.ShelfDto
import com.mrlaughing.moyuan.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadApiClient @Inject constructor(
    gson: Gson,
    okHttpClient: OkHttpClient
) {

    private val api: WereadApi = Retrofit.Builder()
        .baseUrl(Constants.WEREAD_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(WereadApi::class.java)

    // 1. 阅读统计
    suspend fun fetchReadData(mode: String = "overall"): Result<ReadDataDto> {
        return try {
            handleResponse(api.getReadData(mode))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. 书架
    suspend fun fetchShelf(): Result<ShelfDto> {
        return try {
            handleResponse(api.getShelf())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. 笔记和划线
    suspend fun fetchNotebooks(): Result<List<NotebookDto>> {
        return try {
            handleResponse(api.getNotebooks())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. 书籍搜索
    suspend fun searchBook(keyword: String, count: Int = 20): Result<SearchResultDto> {
        return try {
            handleResponse(api.searchBook(keyword, count))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. 推荐好书
    suspend fun fetchRecommendations(count: Int = 10): Result<RecommendDto> {
        return try {
            handleResponse(api.getRecommendations(count))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(NullPointerException("Response body is null"))
            }
        } else {
            Result.failure(IllegalStateException("HTTP ${response.code()}: ${response.message()}"))
        }
    }
}
