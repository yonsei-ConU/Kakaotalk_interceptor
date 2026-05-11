package com.nokakao.interceptor.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted Kakao Talk notification payload (sender + body + receive time).
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "sender_name")
    val senderName: String,
    @ColumnInfo(name = "chat_room_name")
    val chatRoomName: String? = null,
    @ColumnInfo(name = "message_content")
    val messageContent: String,
    val timestamp: Long,
)
