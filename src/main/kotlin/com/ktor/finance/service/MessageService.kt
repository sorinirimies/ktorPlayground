package com.ktor.finance.service

import com.ktor.finance.model.ChangeType
import com.ktor.finance.model.DbUpdater
import com.ktor.finance.model.Message
import com.ktor.finance.model.Messages
import com.ktor.finance.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class MessageService : MessageApi {

  private val listeners = mutableMapOf<Int, suspend (DbUpdater<Message?>) -> Unit>()

  override fun addChangeListener(id: Int, listener: suspend (DbUpdater<Message?>) -> Unit) {
    listeners[id] = listener
  }

  override fun removeChangeListener(id: Int) = listeners.remove(id)

  private suspend fun onChange(type: ChangeType, id: Int, entity: Message? = null) {
    listeners.values.forEach { it.invoke(DbUpdater(type, id, entity)) }
  }

  override suspend fun getAllMessages(): List<Message> = dbQuery {
    Messages.selectAll().map { toMessage(it) }
  }

  private fun toMessage(row: ResultRow): Message = Message(
      id = row[Messages.id],
      content = row[Messages.content],
      dateUpdated = row[Messages.dateUpdated])

  override suspend fun getMessage(id: Int): Message? = dbQuery {
    Messages.select { Messages.id.eq(id) }.mapNotNull {
      toMessage(it)
    }.singleOrNull()
  }

  override suspend fun addMessage(message: Message): Message? {
    var key: Int? = 0
    dbQuery {
      key = com.ktor.finance.model.Messages.insert { it ->
        it[content] = message.content
        it[dateUpdated] = message.dateUpdated
      } get Messages.id
    }
    return key?.let { getMessage(it) }
  }

  override suspend fun updateMessage(message: Message): Message? {
    val id = message.id
    return if (id == null) {
      addMessage(message)
    } else {
      dbQuery {
        com.ktor.finance.model.Messages.update({ Messages.id eq id }) {
          it[content] = message.content
          it[dateUpdated] = System.currentTimeMillis()
        }
      }
      getMessage(id).also {
        onChange(ChangeType.UPDATE, id, it)
      }
    }
  }

  override suspend fun deleteMessage(id: Int): Boolean = dbQuery {
    Messages.deleteWhere { Messages.id eq id } > 0
  }
}