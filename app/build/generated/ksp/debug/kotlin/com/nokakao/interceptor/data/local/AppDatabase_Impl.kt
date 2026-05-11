package com.nokakao.interceptor.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _messageDao: Lazy<MessageDao> = lazy {
    MessageDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "41d8b4db6ad07d46c577c6f645c93dc8", "d29da015990eb2cb03ec00ee1aeebce4") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sender_name` TEXT NOT NULL, `chat_room_name` TEXT, `message_content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '41d8b4db6ad07d46c577c6f645c93dc8')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `messages`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMessages.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("sender_name", TableInfo.Column("sender_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("chat_room_name", TableInfo.Column("chat_room_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("message_content", TableInfo.Column("message_content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMessages: TableInfo = TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages)
        val _existingMessages: TableInfo = read(connection, "messages")
        if (!_infoMessages.equals(_existingMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |messages(com.nokakao.interceptor.data.local.MessageEntity).
              | Expected:
              |""".trimMargin() + _infoMessages + """
              |
              | Found:
              |""".trimMargin() + _existingMessages)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "messages")
  }

  public override fun clearAllTables() {
    super.performClear(false, "messages")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(MessageDao::class, MessageDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun messageDao(): MessageDao = _messageDao.value
}
