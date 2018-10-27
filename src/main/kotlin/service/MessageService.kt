package service

import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class MessageService {

    private val listeners = mutableMapOf<Int, suspend (DbUpdater<Message?>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (DbUpdater<Message?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: Message? = null) {
        listeners.values.forEach { it.invoke(DbUpdater(type, id, entity)) }
    }

    suspend fun getAllMessages(): List<Message> = dbQuery {
        Users.selectAll().map { toMessage(it) }
    }

    private fun toMessage(row: ResultRow): Message = Message(id = row[Users.id],
            content = row[Messages.content],
            dateUpdated = row[Users.dateUpdated])

    suspend fun getMessage(id: Int): Message? = dbQuery {
        Messages.select { Messages.id.eq(id) }.mapNotNull {
            toMessage(it)
        }.singleOrNull()
    }

    suspend fun addMessage(message: Message): Message? {
        var key: Int? = 0
        dbQuery {
            key = Messages.insert { it ->
                it[content] = message.content
                it[dateUpdated] = message.dateUpdated
            } get Users.id
        }
        return key?.let { getMessage(it) }
    }

    suspend fun updateMessage(message: Message): Message? {
        val id = message.id
        return if (id == null) {
            addMessage(message)
        } else {
            dbQuery {
                Messages.update({ Messages.id eq id }) {
                    it[content] = message.content
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            getMessage(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun deleteMessage(id: Int): Boolean = dbQuery {
        Messages.deleteWhere { Messages.id eq id } > 0
    }
}