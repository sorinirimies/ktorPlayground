package model

import org.jetbrains.exposed.sql.Table

object Messages : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val content = varchar("content", 255)
    val dateUpdated = long("dateUpdated")
}

data class Message(val id: Int?, val content: String, val dateUpdated: Long)
