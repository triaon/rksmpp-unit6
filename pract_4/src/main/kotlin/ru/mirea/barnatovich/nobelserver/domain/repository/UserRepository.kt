package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.User

interface UserRepository {
    fun findByUsername(username: String): User?
}
