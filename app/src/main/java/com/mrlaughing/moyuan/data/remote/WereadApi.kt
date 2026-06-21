package com.mrlaughing.moyuan.data.remote

import com.mrlaughing.moyuan.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 微信读书 Gateway API
 * 官方文档: https://i.weread.qq.com/api/agent/gateway
 * 所有请求都是 POST + JSON Body，通过 api_name 区分接口
 */
interface WereadApi {

    @POST("/api/agent/gateway")
    suspend fun callGateway(
        @Body request: GatewayRequest
    ): Response<GatewayResponse>
}
