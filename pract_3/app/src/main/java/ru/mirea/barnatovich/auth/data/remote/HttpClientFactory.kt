package ru.mirea.barnatovich.auth.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.mirea.barnatovich.auth.data.local.TokenStorage

// Плагин добавляет "Authorization: Bearer <token>" ко всем запросам, если токен сохранён
private fun bearerTokenPlugin(storage: TokenStorage) = createClientPlugin("BearerToken") {
    onRequest { request, _ ->
        storage.getToken()?.let { request.bearerAuth(it) }
    }
}

fun createHttpClient(storage: TokenStorage): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("HTTP", message)
            }
        }
        level = LogLevel.HEADERS
        // В лог попадёт только факт наличия заголовков, а не сами токены
        sanitizeHeader { name ->
            name.equals(HttpHeaders.Authorization, ignoreCase = true) ||
                name.equals(HttpHeaders.SetCookie, ignoreCase = true)
        }
    }
    install(bearerTokenPlugin(storage))
}
