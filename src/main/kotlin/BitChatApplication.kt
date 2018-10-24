import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import model.Coin
import model.Snippet
import model.User
import java.text.DateFormat
import java.util.*

fun Application.module() {
    LOG.debug("Starting UserApplication!")

    val user = User("Sorin")
    val userList = arrayListOf(User("Test"), User("Aloha"), user)

    val snippet = Snippet("Hellow here")
    val snippets = Collections.synchronizedList(mutableListOf(
            Snippet("hello"),
            Snippet("world"),
            snippet
    ))

    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
            disableDefaultTyping()
            register(ContentType.Application.Json, JacksonConverter(ObjectMapper().apply {
                User::class.java
                Coin::class.java
            }))
        }
    }

    routing {
        route("/users") {
            get {
                call.respond(userList)
            }
            post {
                val post = call.receive<User>()
                userList += User(post.name)
                call.respond(mapOf("OK" to true))
            }
            put("/{id}") {
                call.respond(userList to true)
            }
        }

        route("/snippets") {
            get {
                call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
            }
            post {
                val post = call.receive<Snippet>()
                snippets += Snippet(post.message)
                call.respond(mapOf("OK" to true))
            }
        }
    }
}