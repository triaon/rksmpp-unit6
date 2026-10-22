package ru.mirea.barnatovich.auth.domain.repository

import ru.mirea.barnatovich.auth.domain.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>

    suspend fun getUser(id: Int): User
}
