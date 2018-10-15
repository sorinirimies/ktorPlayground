import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import model.User

fun Application.main() {

    LOG.debug("Starting UserApplication!")

    install(CallLogging)

    routing {
        route("/user") {
            get("/{id}") {
                call.respondText { "OK" }
            }
            post("/{id}") {
                call.respond(mapOf(User("!§&", "§%Q§(Q") to true))
            }
        }
    }
}