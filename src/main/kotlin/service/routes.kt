package service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import model.Message
import model.User

fun Route.users(userService: UserApi) {
    route("/users") {

        get("/") { call.respond(userService.getAllUsers()) }

        get("/{id}") {
            val user = call.parameters["id"]?.toInt()?.let { userService.getUser(it) }
            if (user == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(user)
        }

        post("/") {
            val user = call.receive<User>()
            userService.addUser(user)?.let { userResult -> call.respond(userResult) }
        }

        put("/") {
            val user = call.receive<User>()
            userService.updateUser(user)
        }

        delete("/{id}") {
            val removed = userService.deleteUser(call.parameters["id"]?.toInt()!!)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
    webSocket("/userupdates") {
        try {
            userService.addChangeListener(this.hashCode()) {
                outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
            }
            while (true) {
                incoming.receiveOrNull() ?: break
            }
        } finally {
            userService.removeChangeListener(this.hashCode())
        }
    }
}

fun Route.messages(messageService: MessageApi) {
    route("/messages") {

        get("/") { call.respond(messageService.getAllMessages()) }

        get("/{id}") {
            val message = call.parameters["id"]?.toInt()?.let { messageService.getMessage(it) }
            if (message == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(message)
        }

        put("/{id}") {
            val message = call.receive<Message>()
            messageService.updateMessage(message)
        }

        post {
            val message = call.receive<Message>()
            messageService.addMessage(message)?.let { messageResult -> call.respond(messageResult) }
        }

        delete("/{id}") {
            val removed = messageService.deleteMessage(call.parameters["id"]?.toInt()!!)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }

    webSocket("/messageupdates") {
        try {
            messageService.addChangeListener(this.hashCode()) {
                outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
            }
            while (true) {
                incoming.receiveOrNull() ?: break
            }
        } finally {
            messageService.removeChangeListener(this.hashCode())
        }
    }
}

val mapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
