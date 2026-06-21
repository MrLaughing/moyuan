package com.mrlaughing.moyuan.data.remote

import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor(
    private val userPrefs: UserPrefs
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val apiKey = runBlocking { userPrefs.getApiKey() }

        if (!apiKey.isNullOrEmpty()) {
            val newRequest = original.newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(original)
    }
}
