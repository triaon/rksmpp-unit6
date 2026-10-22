package ru.mirea.barnatovich.nobelclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import ru.mirea.barnatovich.nobelclient.data.TokenStorage
import ru.mirea.barnatovich.nobelclient.data.remote.NobelApi
import ru.mirea.barnatovich.nobelclient.data.repository.AuthRepositoryImpl
import ru.mirea.barnatovich.nobelclient.data.repository.NobelRepositoryImpl
import ru.mirea.barnatovich.nobelclient.domain.repository.AuthRepository
import ru.mirea.barnatovich.nobelclient.domain.repository.NobelRepository
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetFavoritesUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetLaureateDetailsUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetLaureatesUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetProfileUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.LogoutUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.ToggleFavoriteUseCase

// Ручной DI: все зависимости создаются здесь.
class AppContainer {

    // Wikimedia отдаёт 403 на стандартный User-Agent okhttp, поэтому задаём свой
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "NobelClientApp/1.0 (student project; Android)")
                    .build()
            )
        }
        .build()

    private val httpClient = HttpClient(OkHttp) {
        engine { preconfigured = okHttpClient }
        // Коды 4xx/5xx превращаются в исключения
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) { level = LogLevel.INFO }
    }

    private val tokenStorage = TokenStorage()
    private val api = NobelApi(httpClient, BASE_URL, tokenStorage)
    private val repository: NobelRepository = NobelRepositoryImpl(api)
    private val authRepository: AuthRepository = AuthRepositoryImpl(api, tokenStorage)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getLaureatesUseCase = GetLaureatesUseCase(repository)
    val getLaureateDetailsUseCase = GetLaureateDetailsUseCase(repository)
    val getFavoritesUseCase = GetFavoritesUseCase(repository)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
    val getProfileUseCase = GetProfileUseCase(repository)

    companion object {
        // собственный сервер из задания 5; 10.0.2.2 — это localhost компьютера из эмулятора
        const val BASE_URL = "http://10.0.2.2:8080"
    }
}
