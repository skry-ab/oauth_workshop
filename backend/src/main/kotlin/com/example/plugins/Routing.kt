package com.example.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.Serializable
import java.net.URL
import java.util.concurrent.TimeUnit

@Serializable
data class Message(val message: String, val author: String)

@Serializable
data class NewMessage(val message: String)

fun Application.configureRouting() {

    val messages = mutableListOf<Message>()
    val issuer = "accounts.google.com"
    val jwkProvider = JwkProviderBuilder(URL("https://www.googleapis.com/oauth2/v3/certs"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        // https://ktor.io/docs/jwt.html
        jwt("auth-jwt") {
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }

            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    println("JWT did not contain email claim!")
                    null
                }
            }
        }

    }

    routing {
        route("/api/messages")  {
            get {
                call.respond(messages)
            }

            authenticate("auth-jwt") {
                post {
                    val principal = call.principal<JWTPrincipal>() ?: error("No principal")
                    val email = principal.payload.getClaim("email").asString()
                    val newMessage = call.receive<NewMessage>()
                    val message = Message(newMessage.message, email)
                    messages.add(message)

                    call.respond(HttpStatusCode.Created, message)
                }
            }
        }
    }

    routing {
        route("/api/messages")  {
            get {
                call.respond(messages)
            }

            post {
                val newMessage = call.receive<NewMessage>()
                val message = Message(newMessage.message, "Unknown author")
                messages.add(message)

                call.respond(HttpStatusCode.Created, message)
            }
        }
    }
}