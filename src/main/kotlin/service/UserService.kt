package service

import model.ChangeType
import model.User
import model.UserUpdater
import model.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import service.DatabaseFactory.dbQuery

class UserService {
    private val listeners = mutableMapOf<Int, suspend (UserUpdater<User?>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (UserUpdater<User?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: User? = null) {
        listeners.values.forEach { it.invoke(UserUpdater(type, id, entity)) }
    }

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    private fun toUser(row: ResultRow): User = User(id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            dateUpdated = row[Users.dateUpdated])
}