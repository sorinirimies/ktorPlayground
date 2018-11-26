package com.ktor.finance.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ktor.finance.model.Messages
import com.ktor.finance.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun initExposedDb() {
    logger().debug("Connecting to DB.")
    Database.connect(hikari).also {
        transaction {
            addLogger(StdOutSqlLogger)
            logger().debug("Creating DBs.")
            SchemaUtils.create(Messages, Users)
        }
    }
}

private val hikari by lazy(LazyThreadSafetyMode.NONE) {
    HikariDataSource(HikariConfig().apply {
        driverClassName = "org.h2.Driver"
        jdbcUrl = "jdbc:h2:mem:finance"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}

suspend fun <T> dbQuery(block: () -> T): T = coroutineScope {
    async(Dispatchers.IO) { transaction { block() } }.await()
}

val mapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}