package com.ktor.finance.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ktor.finance.model.Message
import com.ktor.finance.model.User
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.websocket.webSocket

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
  webSocket("/users/updates") {
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

  webSocket("/messages/updates") {
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
