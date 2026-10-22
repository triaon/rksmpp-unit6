package ru.mirea.barnatovich.nobelserver

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobelserver.data.db.DatabaseFactory
import ru.mirea.barnatovich.nobelserver.data.remote.NobelApiClient
import ru.mirea.barnatovich.nobelserver.data.repository.PostgresFavoriteRepository
import ru.mirea.barnatovich.nobelserver.data.repository.PostgresPrizeRepository
import ru.mirea.barnatovich.nobelserver.data.repository.PostgresUserRepository
import ru.mirea.barnatovich.nobelserver.domain.usecase.AddFavoriteUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetFavoritesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizeUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetProfileUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.ImportPrizesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.RemoveFavoriteUseCase
import ru.mirea.barnatovich.nobelserver.plugins.JwtConfig
import ru.mirea.barnatovich.nobelserver.plugins.configureMonitoring
import ru.mirea.barnatovich.nobelserver.plugins.configureOpenApi
import ru.mirea.barnatovich.nobelserver.plugins.configureSecurity
import ru.mirea.barnatovich.nobelserver.plugins.configureSerialization
import ru.mirea.barnatovich.nobelserver.plugins.configureStatusPages
import ru.mirea.barnatovich.nobelserver.routes.authRoutes
import ru.mirea.barnatovich.nobelserver.routes.prizeRoutes
import ru.mirea.barnatovich.nobelserver.routes.userRoutes

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val config = environment.config
    val jwtConfig = JwtConfig.from(config)
    DatabaseFactory.init(
        url = config.property("database.url").getString(),
        user = config.property("database.user").getString(),
        password = config.property("database.password").getString()
    )

    // ручной DI
    val prizeRepository = PostgresPrizeRepository()
    val userRepository = PostgresUserRepository()
    val favoriteRepository = PostgresFavoriteRepository()
    val importPrizes = ImportPrizesUseCase(prizeRepository, NobelApiClient())

    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    configureSecurity(jwtConfig)
    configureOpenApi()

    routing {
        authRoutes(LoginUseCase(userRepository), jwtConfig)
        prizeRoutes(GetPrizesUseCase(prizeRepository), GetPrizeUseCase(prizeRepository))
        authenticate(JwtConfig.AUTH_NAME) {
            userRoutes(
                GetProfileUseCase(userRepository, favoriteRepository),
                GetFavoritesUseCase(favoriteRepository),
                AddFavoriteUseCase(prizeRepository, favoriteRepository),
                RemoveFavoriteUseCase(favoriteRepository)
            )
        }
    }

    // учебные пользователи и разовый импорт премий (только если таблица prizes пуста)
    launch {
        userRepository.createIfMissing("student", "student123", "user")
        userRepository.createIfMissing("admin", "admin123", "admin")
        try {
            val imported = importPrizes(yearFrom = 2015, yearTo = 2024)
            if (imported > 0) log.info("Импортировано премий из api.nobelprize.org: $imported")
            else log.info("Премии уже есть в БД, импорт не нужен")
        } catch (e: Exception) {
            log.error("Не удалось импортировать премии из Nobel Prize API", e)
        }
    }
}
