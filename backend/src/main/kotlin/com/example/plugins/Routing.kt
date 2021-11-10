package com.example.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.client.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.Serializable
import java.net.URL
import java.util.*
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

        //https://ktor.io/docs/oauth.html
        oauth("oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "REPLACE_ME",
                    clientSecret = "REPLACE_ME",
                    // Definition: https://developers.google.com/identity/protocols/oauth2/scopes#oauth2
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.email")
                )
            }
            client = HttpClient()
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

        authenticate("oauth-google") {
            get("/login") {

            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2 = call.principal() ?: error("No principal returned")

                // Not used in this example
                val accessToken = principal.accessToken

                val idTokenString = principal.extraParameters["id_token"] ?: error("No ID-token returned")

                // Decode parts if you want to inspect them, or enter idTokenString on: https://www.jstoolset.com/jwt
                val decoder = Base64.getUrlDecoder()
                val jwtParts = idTokenString.split(".")
                val header = (String(decoder.decode(jwtParts[0])))
                val payload = String(decoder.decode(jwtParts[1]))
                val signature = decoder.decode(jwtParts[2])

                call.respondRedirect("http://localhost:3000/callback?token=$idTokenString")
            }
        }
    }
}