package com.mrlaughing.moyuan.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrlaughing.moyuan.data.remote.dto.*
import com.mrlaughing.moyuan.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadApiClient @Inject constructor(
    private val gson: Gson,
    okHttpClient: OkHttpClient
) {

    private val api: WereadApi = Retrofit.Builder()
        .baseUrl(Constants.WEREAD_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(WereadApi::class.java)

    suspend fun fetchReadData(): Result<ReadDataDetailResponse> {
        return try {
            val request = GatewayRequest(apiName = "/readdata/detail")
            val response = api.callGateway(request)
            handleResponse<ReadDataDetailResponse>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchReadDataSummary(): Result<ReadDataDto> {
        return try {
            val request = GatewayRequest(apiName = "/readdata/summary")
            val response = api.callGateway(request)
            handleResponse<ReadDataDto>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchShelf(): Result<ShelfSyncResponse> {
        return try {
            val request = GatewayRequest(apiName = "/shelf/sync")
            val response = api.callGateway(request)
            handleResponse<ShelfSyncResponse>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchNotebooks(): Result<List<NotebookDto>> {
        return try {
            val request = GatewayRequest(apiName = "/notebook/list")
            val response = api.callGateway(request)
            handleResponse<List<NotebookDto>>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRecommendations(): Result<RecommendDto> {
        return try {
            val request = GatewayRequest(apiName = "/recommend/list")
            val response = api.callGateway(request)
            handleResponse<RecommendDto>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchBook(keyword: String): Result<SearchResultDto> {
        return try {
            val request = GatewayRequest(apiName = "/search/book")
            val response = api.callGateway(request)
            handleResponse<SearchResultDto>(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private inline fun <reified T> handleResponse(
        response: Response<GatewayResponse>
    ): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (body.errcode == 0 && body.success && body.data != null) {
                    val type = object : TypeToken<T>() {}.type
                    val data: T = gson.fromJson(body.data, type)
                    Result.success(data)
                } else {
                    Result.failure(IllegalStateException("Gateway error: errcode=${body.errcode}, error=${body.error}"))
                }
            } else {
                Result.failure(NullPointerException("Response body is null"))
            }
        } else {
            Result.failure(IllegalStateException("HTTP ${response.code()}: ${response.message()}"))
        }
    }
}
