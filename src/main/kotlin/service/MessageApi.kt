package service

import model.DbUpdater
import model.Message

interface MessageApi {
    suspend fun getMessage(id: Int): Message?
    suspend fun getAllMessages(): List<Message>
    suspend fun addMessage(message: Message): Message?
    suspend fun updateMessage(message: Message): Message?
    suspend fun deleteMessage(id: Int): Boolean
    fun addChangeListener(id: Int, listener: suspend (DbUpdater<Message?>) -> Unit)
    fun removeChangeListener(id: Int): (suspend (DbUpdater<Message?>) -> Unit)?
}