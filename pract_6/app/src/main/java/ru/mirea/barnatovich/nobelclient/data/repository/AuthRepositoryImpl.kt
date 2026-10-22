package ru.mirea.barnatovich.nobelclient.data.repository

import ru.mirea.barnatovich.nobelclient.data.TokenStorage
import ru.mirea.barnatovich.nobelclient.data.remote.NobelApi
import ru.mirea.barnatovich.nobelclient.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: NobelApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String) {
        tokenStorage.token = api.login(username, password).token
    }

    override fun logout() {
        tokenStorage.token = null
    }
}
