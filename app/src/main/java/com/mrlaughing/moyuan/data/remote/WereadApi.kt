package com.mrlaughing.moyuan.data.remote

import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDto
import com.mrlaughing.moyuan.data.remote.dto.RecommendDto
import com.mrlaughing.moyuan.data.remote.dto.SearchResultDto
import com.mrlaughing.moyuan.data.remote.dto.ShelfDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WereadApi {

    // 1. 阅读统计 - 总览
    @GET("/readdata/detail")
    suspend fun getReadData(
        @Query("mode") mode: String = "overall"
    ): Response<ReadDataDto>

    // 2. 书架同步 - 浏览个人藏书
    @GET("/shelf/sync")
    suspend fun getShelf(): Response<ShelfDto>

    // 3. 笔记和划线 - 查看划线 / 想法
    @GET("/user/notebooks")
    suspend fun getNotebooks(): Response<List<NotebookDto>>

    // 4. 书籍搜索 - 按关键词搜索书籍
    @GET("/search")
    suspend fun searchBook(
        @Query("keyword") keyword: String,
        @Query("count") count: Int = 20
    ): Response<SearchResultDto>

    // 5. 推荐好书 - 基于阅读偏好的个性化推荐
    @GET("/book/recommend")
    suspend fun getRecommendations(
        @Query("count") count: Int = 10
    ): Response<RecommendDto>
}
