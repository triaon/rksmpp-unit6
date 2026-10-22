package ru.mirea.barnatovich.nobelclient.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.mirea.barnatovich.nobelclient.data.TokenStorage
import ru.mirea.barnatovich.nobelclient.data.remote.dto.FavoriteDto
import ru.mirea.barnatovich.nobelclient.data.remote.dto.LoginRequestDto
import ru.mirea.barnatovich.nobelclient.data.remote.dto.LoginResponseDto
import ru.mirea.barnatovich.nobelclient.data.remote.dto.PrizeDto
import ru.mirea.barnatovich.nobelclient.data.remote.dto.ProfileDto

class NobelApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val tokenStorage: TokenStorage
) {

    suspend fun login(username: String, password: String): LoginResponseDto =
        client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }.body()

    // открытый эндпоинт, фильтры необязательные
    suspend fun getPrizes(year: Int?, category: String?): List<PrizeDto> =
        client.get("$baseUrl/prizes") {
            parameter("year", year)
            parameter("category", category)
        }.body()

    // защищённые эндпоинты — с заголовком Authorization: Bearer <token>
    suspend fun getProfile(): ProfileDto =
        client.get("$baseUrl/users/me") { bearerAuth(tokenStorage.token.orEmpty()) }.body()

    suspend fun getFavorites(): List<FavoriteDto> =
        client.get("$baseUrl/users/me/prizes") { bearerAuth(tokenStorage.token.orEmpty()) }.body()

    suspend fun addFavorite(prizeId: Int) {
        client.post("$baseUrl/users/me/prizes/$prizeId") { bearerAuth(tokenStorage.token.orEmpty()) }
    }

    suspend fun removeFavorite(prizeId: Int) {
        client.delete("$baseUrl/users/me/prizes/$prizeId") { bearerAuth(tokenStorage.token.orEmpty()) }
    }
}
