package com.nokakao.interceptor.logging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong

/**
 * Thread-safe append-only log for UI. Oldest entries drop when [maxEntries] is exceeded.
 */
class InterceptorEventLog(
    private val maxEntries: Int = MAX_ENTRIES_DEFAULT,
) {

    private val nextId = AtomicLong(1L)
    private val _entries = MutableStateFlow<List<InterceptorLogEntry>>(emptyList())
    val entries: StateFlow<List<InterceptorLogEntry>> = _entries.asStateFlow()

    /**
     * Records a line (safe from any thread; listener runs off the main UI thread).
     */
    fun append(message: String) {
        val entry = InterceptorLogEntry(
            id = nextId.getAndIncrement(),
            timestampMs = System.currentTimeMillis(),
            message = message,
        )
        synchronized(this) {
            _entries.value = listOf(entry) + _entries.value.take(maxEntries - 1)
        }
    }

    /**
     * Clears all log entries.
     */
    fun clear() {
        synchronized(this) {
            _entries.value = emptyList()
        }
    }

    companion object {
        private const val MAX_ENTRIES_DEFAULT = 200
    }
}
