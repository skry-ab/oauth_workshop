package com.example.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.Serializable

@Serializable
data class Message(val message: String, val author: String)

@Serializable
data class NewMessage(val message: String)

fun Application.configureRouting() {

    val messages = mutableListOf<Message>()

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
