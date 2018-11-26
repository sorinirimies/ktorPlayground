package com.ktor.finance.model

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val dateUpdated = long("dateUpdated")
}

data class User(val id: Int?, val name: String, val email: String, val dateUpdated: Long)

