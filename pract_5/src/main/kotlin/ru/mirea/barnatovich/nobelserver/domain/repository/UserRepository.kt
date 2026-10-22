package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.User

interface UserRepository {
    // проверяет пароль по bcrypt-хэшу, возвращает пользователя или null
    suspend fun checkCredentials(username: String, password: String): User?
    suspend fun findById(id: Int): User?
    suspend fun createIfMissing(username: String, password: String, role: String)
}
