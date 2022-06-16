package com.example

import com.example.plugins.configureRouting
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/api/ping").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("pong", response.content)
            }
        }
    }
}
