package com.example.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class Message(val message: String, val author: String)

@Serializable
data class NewMessage(val message: String)

fun Application.configureRouting() {

    val messages = mutableListOf<Message>()

    routing {
        route("/api/ping") {
            get {
                call.respond("pang")
            }
        }

        route("/api/messages") {
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
