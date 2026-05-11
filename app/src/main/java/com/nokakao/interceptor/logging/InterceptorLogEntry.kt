package com.nokakao.interceptor.logging

/**
 * Single line shown in the in-app activity log (newest first).
 */
data class InterceptorLogEntry(
    val id: Long,
    val timestampMs: Long,
    val message: String,
)
