package com.nokakao.interceptor.notification

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.NotificationListenerService.RankingMap
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nokakao.interceptor.NotificationApp
import com.nokakao.interceptor.data.local.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Intercepts status-bar notifications, persists Kakao Talk payloads to Room, then dismisses them.
 *
 * The user must grant **notification listener** access in system settings for this service to run.
 */
class KakaoNotificationListener : NotificationListenerService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main.immediate)

    private val messageDao by lazy {
        (application as NotificationApp).database.messageDao()
    }

    private val eventLog by lazy {
        (application as NotificationApp).eventLog
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        (application as NotificationApp).isListenerConnected.value = true
        val msg = "Notification listener connected and active"
        Log.d(TAG, msg)
        eventLog.append(msg)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        (application as NotificationApp).isListenerConnected.value = false
        val msg = "Notification listener disconnected"
        Log.d(TAG, msg)
        eventLog.append(msg)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: RankingMap) {
        handleNotification(sbn)
    }

    private fun handleNotification(sbn: StatusBarNotification) {
        val pkg = sbn.packageName ?: "unknown"
        val app = (application as NotificationApp)
        app.lastPackageName.value = pkg

        val isKakao = pkg.contains("kakao", ignoreCase = true)
        
        if (!isKakao) return

        val notification = sbn.notification
        val extras = notification.extras ?: Bundle.EMPTY
        
        // --- DATA EXTRACTION STRATEGY ---
        
        // 1. Message Body (Usually simple)
        val messageContent = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence("android.text")?.toString() 
            ?: ""

        // 2. Title Logic (Sender or Room Name)
        val mainTitle = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() 
            ?: extras.getCharSequence("android.title")?.toString() 
            ?: ""

        // 3. SubText / InfoText (Often the Group Name in Kakao)
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val infoText = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()

        // --- FILTERING LOGIC ---

        // A: Filter out "n unread messages" summaries
        if (messageContent.contains("개의 안 읽은 메시지") || mainTitle.contains("카카오톡")) {
            Log.d(TAG, "Filtering out summary notification: $messageContent")
            return
        }

        // B: Identify Group Chat vs Private Chat
        // In Kakao, if it's a group chat:
        // subText or infoText usually contains the Room Name.
        // mainTitle is the Sender Name.
        var finalSender = mainTitle
        var finalRoom = subText ?: infoText

        // Sometimes if room name is empty, we check if title contains it (rare for Kakao)
        if (finalSender.isBlank()) finalSender = "(unknown sender)"

        // Final safety check: if everything is empty or looks like a system alert, skip
        if (messageContent.isBlank() || messageContent == "새로운 메시지가 왔습니다.") return

        serviceScope.launch(Dispatchers.IO) {
            runCatching {
                messageDao.insert(
                    MessageEntity(
                        senderName = finalSender,
                        chatRoomName = finalRoom,
                        messageContent = messageContent,
                        timestamp = sbn.postTime,
                    ),
                )
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    runCatching {
                        cancelNotification(sbn.key)
                    }.onSuccess {
                        val displayInfo = if (!finalRoom.isNullOrBlank()) "[$finalRoom] $finalSender" else finalSender
                        eventLog.append("Captured: $displayInfo - ${messageContent.take(20)}...")
                    }.onFailure { error ->
                        Log.e(TAG, "Failed to remove notification", error)
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "Failed to save message", error)
            }
        }
    }

    /**
     * Kakao (and most messaging apps) populate [Notification.EXTRA_TITLE] / [Notification.EXTRA_TEXT].
     * Fallbacks cover OEM variations and big-text layouts.
     */
    private fun Bundle.readSenderTitle(): String {
        return sequenceOf(
            getCharSequence(Notification.EXTRA_TITLE_BIG),
            getCharSequence(Notification.EXTRA_TITLE),
            getCharSequence("android.title"),
        ).firstOrNull { !it.isNullOrBlank() }
            ?.toString()
            .orEmpty()
    }

    private fun Bundle.readMessageBody(): String {
        return sequenceOf(
            getCharSequence(Notification.EXTRA_BIG_TEXT),
            getCharSequence(Notification.EXTRA_TEXT),
            getCharSequence(Notification.EXTRA_SUB_TEXT),
            getCharSequence("android.text"),
        ).firstOrNull { !it.isNullOrBlank() }
            ?.toString()
            .orEmpty()
    }

    companion object {
        private const val TAG = "KakaoInterceptor"
        private const val KAKAO_TALK_PACKAGE = "com.kakao.talk"
        private const val PREVIEW_LIMIT = 80
    }
}
