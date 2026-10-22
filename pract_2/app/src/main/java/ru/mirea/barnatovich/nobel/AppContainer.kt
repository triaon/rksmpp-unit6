package ru.mirea.barnatovich.nobel

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import ru.mirea.barnatovich.nobel.data.remote.NobelApi
import ru.mirea.barnatovich.nobel.data.repository.NobelRepositoryImpl
import ru.mirea.barnatovich.nobel.domain.repository.NobelRepository
import ru.mirea.barnatovich.nobel.domain.usecase.GetLaureateDetailsUseCase
import ru.mirea.barnatovich.nobel.domain.usecase.GetLaureatesUseCase

// Ручной DI: все зависимости создаются здесь.
// Чтобы перейти на другой сервер, достаточно поменять BASE_URL или реализацию репозитория.
class AppContainer {

    // Wikimedia отдаёт 403 на стандартный User-Agent okhttp, поэтому задаём свой
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "NobelLaureatesApp/1.0 (student project; Android)")
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

    private val repository: NobelRepository = NobelRepositoryImpl(NobelApi(httpClient, BASE_URL))

    val getLaureatesUseCase = GetLaureatesUseCase(repository)
    val getLaureateDetailsUseCase = GetLaureateDetailsUseCase(repository)

    companion object {
        const val BASE_URL = "https://api.nobelprize.org/2.1"
    }
}
