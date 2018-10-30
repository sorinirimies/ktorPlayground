import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import org.slf4j.LoggerFactory
import service.DatabaseFactory
import service.MessageService
import service.UserService

val LOG = LoggerFactory.getLogger("finance-server")

fun Application.module() {
    LOG.debug("Starting BitChat Server!")
    install(CallLogging)
    install(DefaultHeaders)
    install(WebSockets)
    install(ContentNegotiation) { jackson { configure(SerializationFeature.INDENT_OUTPUT, true) } }
    DatabaseFactory.init()
    install(Routing) {
        users(UserService())
        messages(MessageService())
    }
}

fun main(args: Array<String>) {
    LOG.debug("Starting server ...")
    val server = embeddedServer(Netty, 9596, module = Application::module).start(wait = true)
    server.start(true)
}
