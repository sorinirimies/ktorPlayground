package model

enum class ChangeType { CREATE, UPDATE, DELETE }
data class UserUpdater<T>(val type: ChangeType, val id: Int, val entity: T)