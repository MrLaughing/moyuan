package com.mrlaughing.moyuan.data.remote

import com.mrlaughing.moyuan.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 微信读书 Gateway API
 * 官方文档: https://i.weread.qq.com/api/agent/gateway
 * 所有请求都是 POST + JSON Body
 */
interface WereadApi {

    /**
     * 阅读统计详情
     * mode: weekly / monthly / annually / overall
     * baseTime: 0=当前周期，传历史时间戳可查看历史
     */
    @POST("/readdata/detail")
    suspend fun getReadData(
        @Body request: GatewayRequest
    ): Response<GatewayResponse<ReadDataDetailResponse>>

    /**
     * 书架同步
     */
    @POST("/shelf/sync")
    suspend fun getShelf(
        @Body request: GatewayRequest
    ): Response<GatewayResponse<ShelfSyncResponse>>
}
