import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import model.User

fun Application.moduele() {
    routing {
        route("/user") {
            get("/{id}") {
                call.respondText { "OK" }
            }
            post {
                call.respond(mapOf(User("!§&", "§%Q§(Q") to true))
            }
        }
    }
}