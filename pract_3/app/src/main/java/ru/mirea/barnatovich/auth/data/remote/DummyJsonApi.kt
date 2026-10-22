package ru.mirea.barnatovich.auth.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DummyJsonApi(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): LoginResponse =
        client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUsers(): UsersResponse = client.get("$BASE_URL/users").body()

    suspend fun getUser(id: Int): UserDto = client.get("$BASE_URL/users/$id").body()

    companion object {
        const val BASE_URL = "https://dummyjson.com"
    }
}
