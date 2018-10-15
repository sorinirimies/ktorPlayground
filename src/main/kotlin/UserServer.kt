import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("ktor-server")
fun main(args: Array<String>) {
    LOG.debug("Starting server ...")
    val server = embeddedServer(Netty, 1987, module = Application::main).start(wait = true)
    server.start(true)
}
