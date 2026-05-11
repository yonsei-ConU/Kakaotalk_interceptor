package com.nokakao.interceptor

import android.app.Application
import com.nokakao.interceptor.data.SettingsRepository
import com.nokakao.interceptor.data.local.AppDatabase
import com.nokakao.interceptor.logging.InterceptorEventLog
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Provides a single [AppDatabase] instance for the process.
 */
class NotificationApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.create(this)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }

    /** In-memory activity log for the notification interceptor (UI + listener share this). */
    val eventLog: InterceptorEventLog by lazy {
        InterceptorEventLog()
    }

    /** Tracks whether the [KakaoNotificationListener] is currently bound by the system. */
    val isListenerConnected = MutableStateFlow(false)

    /** Tracks the last package name that posted a notification (for debugging). */
    val lastPackageName = MutableStateFlow<String?>(null)
}
