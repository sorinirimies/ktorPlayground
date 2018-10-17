import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("ktor-server")
val mongoClientProvider by lazy {
    MongoClientProvider()
}

fun main(args: Array<String>) {
    mongoClientProvider.connect("127.0.0.1", 27017)
    LOG.debug("Starting server ...")
    LOG.debug(mongoClientProvider.getDb("bitChat")?.getCollection("grupuri")?.namespace?.fullName)// TODO this is just for fun to test if table is reachable, remove this after debug is done
    val server = embeddedServer(Netty, 1997, module = Application::module).start(wait = true)
    server.start(true)
}
