package ru.mirea.barnatovich.nobelserver.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import java.util.Date

class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expiresMinutes: Long
) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun makeToken(username: String, role: String): String = JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("username", username)
        .withClaim("role", role)
        .withExpiresAt(Date(System.currentTimeMillis() + expiresMinutes * 60_000))
        .sign(algorithm)

    companion object {
        const val AUTH_NAME = "auth-jwt"

        fun from(config: ApplicationConfig): JwtConfig {
            val secret = config.property("jwt.secret").getString()
            require(secret.length >= 32) { "JWT secret должен быть не короче 32 символов" }
            return JwtConfig(
                secret = secret,
                issuer = config.property("jwt.issuer").getString(),
                audience = config.property("jwt.audience").getString(),
                realm = config.property("jwt.realm").getString(),
                expiresMinutes = config.property("jwt.expiresMinutes").getString().toLong()
            )
        }
    }
}

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt(JwtConfig.AUTH_NAME) {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != null) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Токен отсутствует, недействителен или истёк"))
            }
        }
    }
}
