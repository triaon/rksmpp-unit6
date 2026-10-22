package ru.mirea.barnatovich.nobelserver.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureOpenApi() {
    install(OpenApi) {
        info {
            title = "Nobel Prize API"
            version = "1.0"
            description = "Учебный сервер: премии и лауреаты Нобелевской премии, избранное пользователя. Хранение — PostgreSQL."
        }
        server {
            url = "http://localhost:8080"
            description = "Локальный сервер"
        }
        security {
            securityScheme("JWT") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "JWT"
            }
            defaultSecuritySchemeNames("JWT")
            defaultUnauthorizedResponse {
                description = "Токен отсутствует, недействителен или истёк"
                body<ErrorResponse>()
            }
        }
    }
    routing {
        route("api.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/api.json") {
                deepLinking = true
            }
        }
    }
}
