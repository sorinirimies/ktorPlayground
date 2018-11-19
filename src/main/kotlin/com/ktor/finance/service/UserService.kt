package com.ktor.finance.service

import com.ktor.finance.util.dbQuery
import com.ktor.finance.model.ChangeType
import com.ktor.finance.model.DbUpdater
import com.ktor.finance.model.User
import com.ktor.finance.model.Users
import org.jetbrains.exposed.sql.*

class UserService : UserApi {

    private val listeners = mutableMapOf<Int, suspend (DbUpdater<User?>) -> Unit>()

    override fun addChangeListener(id: Int, listener: suspend (DbUpdater<User?>) -> Unit) {
        listeners[id] = listener
    }

    override fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: User? = null) {
        listeners.values.forEach { it.invoke(DbUpdater(type, id, entity)) }
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
      com.ktor.finance.model.Users.selectAll().map { toUser(it) }
    }

    override suspend fun getUser(id: Int): User? = dbQuery {
      com.ktor.finance.model.Users.select { Users.id.eq(id) }.mapNotNull {
        toUser(it)
      }.singleOrNull()
    }

    private fun toUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        dateUpdated = row[Users.dateUpdated])

    override suspend fun updateUser(user: User): User? {
        val id = user.id
        return if (id == null) {
            addUser(user)
        } else {
          dbQuery {
            com.ktor.finance.model.Users.update({ Users.id eq id }) {
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

    override suspend fun addUser(user: User): User? {
        var key: Int? = 0
      dbQuery {
        key = com.ktor.finance.model.Users.insert { it ->
          it[name] = user.name
          it[email] = user.email
          it[dateUpdated] = user.dateUpdated
        } get Users.id
      }
        return key?.let { getUser(it) }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
      com.ktor.finance.model.Users.deleteWhere { Users.id eq id } > 0
    }
}