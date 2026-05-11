package com.nokakao.interceptor.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit contract for a custom LLM HTTP API.
 *
 * Build with [Retrofit.Builder.baseUrl] pointing at your server (for example via `BuildConfig`).
 */
interface LlmApiService {

    /**
     * Sends accumulated messages as JSON and receives a filtered subset / ranking from the LLM.
     *
     * @param body Serializable payload listing saved chat lines.
     */
    @POST("filterImportantMessages")
    suspend fun filterImportantMessages(
        @Body body: FilterImportantMessagesRequest,
    ): Response<FilterImportantMessagesResponse>
}
