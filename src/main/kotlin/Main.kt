import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8000) {
        routing {
            get("/") {

            }
            post("/") {

            }
        }
    }
    server.start(true)
}