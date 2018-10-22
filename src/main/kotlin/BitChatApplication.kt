import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.routing.*
import model.User
import java.text.DateFormat

fun Application.module() {
    LOG.debug("Starting UserApplication!")

    install(CallLogging)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
            disableDefaultTyping()
            register(ContentType.Application.Json, JacksonConverter(ObjectMapper().apply {
                User::class.java
            }))
        }
    }
    install(Routing) {
        users()
    }

    routing {
        trace { application.log.trace(it.buildText()) }
        route("/users") {
            get {
            }
            post {

            }
            put("/{id}") {
            }
        }
        route("/messages") {
            get {
            }
            post {

            }
        }
    }
}