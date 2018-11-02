package service

import model.DbUpdater
import model.User

interface UserApi {
    suspend fun getUser(id: Int): User?
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): User?
    suspend fun updateUser(user: User): User?
    suspend fun deleteUser(id: Int): Boolean
    fun addChangeListener(id: Int, listener: suspend (DbUpdater<User?>) -> Unit)
    fun removeChangeListener(id: Int): (suspend (DbUpdater<User?>) -> Unit)?
}