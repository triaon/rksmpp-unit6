package ru.mirea.barnatovich.nobelserver

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import ru.mirea.barnatovich.nobelserver.data.repository.InMemoryPrizeRepository
import ru.mirea.barnatovich.nobelserver.data.repository.InMemoryUserRepository
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetLaureatesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizeUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.nobelserver.plugins.JwtConfig
import ru.mirea.barnatovich.nobelserver.plugins.configureMonitoring
import ru.mirea.barnatovich.nobelserver.plugins.configureSecurity
import ru.mirea.barnatovich.nobelserver.plugins.configureSerialization
import ru.mirea.barnatovich.nobelserver.plugins.configureStatusPages
import ru.mirea.barnatovich.nobelserver.routes.authRoutes
import ru.mirea.barnatovich.nobelserver.routes.prizeRoutes

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val jwtConfig = JwtConfig.from(environment.config)

    // ручной DI: создаём репозитории и use case-ы здесь
    val prizeRepository = InMemoryPrizeRepository()
    val userRepository = InMemoryUserRepository()
    val loginUseCase = LoginUseCase(userRepository)
    val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    val getPrizeUseCase = GetPrizeUseCase(prizeRepository)
    val getLaureatesUseCase = GetLaureatesUseCase(prizeRepository)

    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    configureSecurity(jwtConfig)

    routing {
        authRoutes(loginUseCase, jwtConfig)
        authenticate(JwtConfig.AUTH_NAME) {
            prizeRoutes(getPrizesUseCase, getPrizeUseCase, getLaureatesUseCase)
        }
    }
}
