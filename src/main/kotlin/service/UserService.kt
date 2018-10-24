package service

import model.ChangeType
import model.DbUpdater
import model.User
import model.Users
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class UserService {

    private val listeners = mutableMapOf<Int, suspend (DbUpdater<User?>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (DbUpdater<User?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: User? = null) {
        listeners.values.forEach { it.invoke(DbUpdater(type, id, entity)) }
    }

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.id.eq(id) }.mapNotNull {
            toUser(it)
        }.singleOrNull()
    }

    private fun toUser(row: ResultRow): User = User(id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            dateUpdated = row[Users.dateUpdated])

    suspend fun updateUser(user: User): User? {
        val id = user.id
        return if (id == null) {
            addUser(user)
        } else {
            dbQuery {
                Users.update({ Users.id eq id }) {
                    it[name] = user.name
                    it[email] = user.email
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            getUser(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun addUser(user: User): User? {
        var key: Int? = 0
        dbQuery {
            key = Users.insert { it ->
                it[name] = user.name
                it[email] = user.email
                it[dateUpdated] = user.dateUpdated
            } get Users.id
        }
        return key?.let { getUser(it) }
    }

    suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

}