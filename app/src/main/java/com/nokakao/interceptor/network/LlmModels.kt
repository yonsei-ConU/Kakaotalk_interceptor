package com.nokakao.interceptor.network

import com.google.gson.annotations.SerializedName

/**
 * JSON body sent to your LLM backend for importance filtering.
 */
data class FilterImportantMessagesRequest(
    @SerializedName("messages")
    val messages: List<SavedMessagePayload>,
)

/**
 * Mirrors persisted Room fields without local DB ids (adjust if your API requires them).
 */
data class SavedMessagePayload(
    @SerializedName("senderName")
    val senderName: String,
    @SerializedName("messageContent")
    val messageContent: String,
    @SerializedName("timestamp")
    val timestamp: Long,
)

/**
 * Response shape from `filterImportantMessages` — tune fields to match your server contract.
 */
data class FilterImportantMessagesResponse(
    @SerializedName("messages")
    val messages: List<SavedMessagePayload>? = null,
    @SerializedName("importantMessageIds")
    val importantMessageIds: List<Long>? = null,
)
