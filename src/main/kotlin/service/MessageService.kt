package service

import model.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
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
            message = row[Messages.message],
            dateUpdated = row[Users.dateUpdated])
}