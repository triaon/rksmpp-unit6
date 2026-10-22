package ru.mirea.barnatovich.auth.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String)

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean
}
