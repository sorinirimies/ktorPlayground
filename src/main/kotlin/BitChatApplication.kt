import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import model.User
import service.DatabaseFactory
import service.MessageService
import service.UserService
import java.text.DateFormat

fun Application.module() {
    LOG.debug("Starting BitChat Server!")
    install(CallLogging)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
            disableDefaultTyping()
            register(ContentType.Application.Json, JacksonConverter(ObjectMapper().apply {
                User::class.java
            }))
        }
    }
    DatabaseFactory.init()
    install(Routing) {
        users(UserService())
        messages(MessageService())
    }
}

val mapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
