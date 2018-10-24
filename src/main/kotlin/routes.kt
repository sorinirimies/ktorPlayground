import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import model.User
import service.MessageService
import service.UserService

fun Route.users(userService: UserService) {
    route("/users") {

        get("/") { call.respond(userService.getAllUsers()) }

        get("/{id}") {
            val user = call.parameters["id"]?.toInt()?.let { it1 -> userService.getUser(it1) }
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
}

fun Route.messages(messageService: MessageService) {
    route("/messages") {
        get("/") { call.respond(messageService.getAllMessages()) }
        get("/{id}") {

        }
        post {

        }
        put("/{id}") {

        }
    }
}
