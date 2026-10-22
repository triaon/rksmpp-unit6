package ru.mirea.barnatovich.auth.data.repository

import io.ktor.client.plugins.ClientRequestException
import ru.mirea.barnatovich.auth.data.local.TokenStorage
import ru.mirea.barnatovich.auth.data.remote.DummyJsonApi
import ru.mirea.barnatovich.auth.data.remote.LoginRequest
import ru.mirea.barnatovich.auth.domain.model.InvalidCredentialsException
import ru.mirea.barnatovich.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: DummyJsonApi,
    private val storage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String) {
        val response = try {
            api.login(LoginRequest(username, password))
        } catch (e: ClientRequestException) {
            // dummyjson на неверный логин/пароль отвечает 400
            throw InvalidCredentialsException()
        }
        storage.saveToken(response.accessToken)
    }

    override suspend fun logout() = storage.clearToken()

    override suspend fun isLoggedIn(): Boolean = storage.getToken() != null
}
