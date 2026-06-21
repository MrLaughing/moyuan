package com.mrlaughing.moyuan.data.remote

import com.mrlaughing.moyuan.data.remote.dto.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadApiClient @Inject constructor(
    private val api: WereadApi
) {
    /**
     * 阅读统计详情
     */
    suspend fun fetchReadData(mode: String = "overall"): Result<ReadDataDetailResponse> {
        return try {
            val request = GatewayRequest(apiName = "/readdata/detail")
            val response = api.getReadData(request)
            handleGatewayResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 书架同步
     */
    suspend fun fetchShelf(): Result<ShelfSyncResponse> {
        return try {
            val request = GatewayRequest(apiName = "/shelf/sync")
            val response = api.getShelf(request)
            handleGatewayResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleGatewayResponse(response: Response<GatewayResponse<T>>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (body.errcode == 0 && body.success && body.data != null) {
                    Result.success(body.data)
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
