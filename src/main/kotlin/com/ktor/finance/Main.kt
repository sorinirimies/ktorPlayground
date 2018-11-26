package com.ktor.finance

import com.fasterxml.jackson.databind.SerializationFeature
import com.ktor.finance.service.MessageService
import com.ktor.finance.service.UserService
import com.ktor.finance.service.messages
import com.ktor.finance.service.users
import com.ktor.finance.util.initExposedDb
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.netty.EngineMain
import io.ktor.websocket.WebSockets

fun Application.module() {
    install(CallLogging)
    install(DefaultHeaders)
    install(WebSockets)
    install(ContentNegotiation) { jackson { configure(SerializationFeature.INDENT_OUTPUT, true) } }
    install(Routing) {
        users(UserService())
        messages(MessageService())
    }
    initExposedDb()
}

fun main(args: Array<String>): Unit = EngineMain.main(args)
