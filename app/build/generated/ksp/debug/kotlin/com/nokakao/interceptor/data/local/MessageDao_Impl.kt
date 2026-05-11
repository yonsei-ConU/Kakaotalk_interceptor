package com.nokakao.interceptor.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MessageDao_Impl(
  __db: RoomDatabase,
) : MessageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMessageEntity: EntityInsertAdapter<MessageEntity>

  private val __deleteAdapterOfMessageEntity: EntityDeleteOrUpdateAdapter<MessageEntity>

  private val __updateAdapterOfMessageEntity: EntityDeleteOrUpdateAdapter<MessageEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMessageEntity = object : EntityInsertAdapter<MessageEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `messages` (`id`,`sender_name`,`chat_room_name`,`message_content`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MessageEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.senderName)
        val _tmpChatRoomName: String? = entity.chatRoomName
        if (_tmpChatRoomName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpChatRoomName)
        }
        statement.bindText(4, entity.messageContent)
        statement.bindLong(5, entity.timestamp)
      }
    }
    this.__deleteAdapterOfMessageEntity = object : EntityDeleteOrUpdateAdapter<MessageEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `messages` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MessageEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfMessageEntity = object : EntityDeleteOrUpdateAdapter<MessageEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `messages` SET `id` = ?,`sender_name` = ?,`chat_room_name` = ?,`message_content` = ?,`timestamp` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MessageEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.senderName)
        val _tmpChatRoomName: String? = entity.chatRoomName
        if (_tmpChatRoomName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpChatRoomName)
        }
        statement.bindText(4, entity.messageContent)
        statement.bindLong(5, entity.timestamp)
        statement.bindLong(6, entity.id)
      }
    }
  }

  public override suspend fun insert(message: MessageEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMessageEntity.insertAndReturnId(_connection, message)
    _result
  }

  public override suspend fun delete(message: MessageEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMessageEntity.handle(_connection, message)
  }

  public override suspend fun update(message: MessageEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfMessageEntity.handle(_connection, message)
  }

  public override fun getAllMessages(): Flow<List<MessageEntity>> {
    val _sql: String = "SELECT * FROM messages ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSenderName: Int = getColumnIndexOrThrow(_stmt, "sender_name")
        val _columnIndexOfChatRoomName: Int = getColumnIndexOrThrow(_stmt, "chat_room_name")
        val _columnIndexOfMessageContent: Int = getColumnIndexOrThrow(_stmt, "message_content")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<MessageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MessageEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSenderName: String
          _tmpSenderName = _stmt.getText(_columnIndexOfSenderName)
          val _tmpChatRoomName: String?
          if (_stmt.isNull(_columnIndexOfChatRoomName)) {
            _tmpChatRoomName = null
          } else {
            _tmpChatRoomName = _stmt.getText(_columnIndexOfChatRoomName)
          }
          val _tmpMessageContent: String
          _tmpMessageContent = _stmt.getText(_columnIndexOfMessageContent)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = MessageEntity(_tmpId,_tmpSenderName,_tmpChatRoomName,_tmpMessageContent,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): MessageEntity? {
    val _sql: String = "SELECT * FROM messages WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSenderName: Int = getColumnIndexOrThrow(_stmt, "sender_name")
        val _columnIndexOfChatRoomName: Int = getColumnIndexOrThrow(_stmt, "chat_room_name")
        val _columnIndexOfMessageContent: Int = getColumnIndexOrThrow(_stmt, "message_content")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MessageEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSenderName: String
          _tmpSenderName = _stmt.getText(_columnIndexOfSenderName)
          val _tmpChatRoomName: String?
          if (_stmt.isNull(_columnIndexOfChatRoomName)) {
            _tmpChatRoomName = null
          } else {
            _tmpChatRoomName = _stmt.getText(_columnIndexOfChatRoomName)
          }
          val _tmpMessageContent: String
          _tmpMessageContent = _stmt.getText(_columnIndexOfMessageContent)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _result = MessageEntity(_tmpId,_tmpSenderName,_tmpChatRoomName,_tmpMessageContent,_tmpTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM messages WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM messages"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
