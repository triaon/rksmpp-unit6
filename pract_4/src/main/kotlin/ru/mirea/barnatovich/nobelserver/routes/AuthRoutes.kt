package ru.mirea.barnatovich.nobelserver.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.nobelserver.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.nobelserver.plugins.ErrorResponse
import ru.mirea.barnatovich.nobelserver.plugins.JwtConfig

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val username: String, val role: String, val expiresIn: Long)

fun Route.authRoutes(loginUseCase: LoginUseCase, jwtConfig: JwtConfig) {
    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        val user = loginUseCase(request.username, request.password)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Неверный логин или пароль"))
            return@post
        }
        val token = jwtConfig.makeToken(user.username, user.role)
        call.respond(LoginResponse(token, user.username, user.role, jwtConfig.expiresMinutes * 60))
    }
}
