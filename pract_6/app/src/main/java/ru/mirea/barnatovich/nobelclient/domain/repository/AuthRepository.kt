package ru.mirea.barnatovich.nobelclient.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String)

    fun logout()
}
