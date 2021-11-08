package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }

        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Post)
            method(HttpMethod.Get)

            header(HttpHeaders.Authorization)
            header(HttpHeaders.AccessControlAllowOrigin)

            allowNonSimpleContentTypes = true
            anyHost()
        }

        configureRouting()
    }.start(wait = true)
}
