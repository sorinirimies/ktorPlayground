package model

import org.jetbrains.exposed.sql.Table

object Messages : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val message = varchar("message", 255)
    val dateUpdated = long("dateUpdated")
}

data class Message(val id: Int, val message: String, val dateUpdated: Long)
