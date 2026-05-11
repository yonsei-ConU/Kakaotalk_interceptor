package com.nokakao.interceptor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nokakao.interceptor.NotificationApp
import com.nokakao.interceptor.data.SettingsRepository
import com.nokakao.interceptor.data.local.MessageDao
import com.nokakao.interceptor.data.local.MessageEntity
import com.nokakao.interceptor.logging.InterceptorEventLog
import com.nokakao.interceptor.logging.InterceptorLogEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Surfaces Room-backed messages and in-app interceptor logs as UI state.
 */
class MainViewModel(
    private val messageDao: MessageDao,
    private val settingsRepository: SettingsRepository,
    private val app: NotificationApp,
) : ViewModel() {

    val messages: StateFlow<List<MessageEntity>> = messageDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val showLogs: StateFlow<Boolean> = settingsRepository.showLogsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    /** Newest-first lines emitted by [KakaoNotificationListener]. */
    val logEntries: StateFlow<List<InterceptorLogEntry>> = app.eventLog.entries

    val isListenerConnected: StateFlow<Boolean> = app.isListenerConnected
    
    val lastPackageName: StateFlow<String?> = app.lastPackageName

    fun deleteAllMessages() {
        viewModelScope.launch {
            messageDao.deleteAll()
            app.eventLog.clear()
        }
    }

    fun updateShowLogs(showLogs: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowLogs(showLogs)
        }
    }

    class Factory(
        private val messageDao: MessageDao,
        private val settingsRepository: SettingsRepository,
        private val app: NotificationApp,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(messageDao, settingsRepository, app) as T
        }
    }
}
