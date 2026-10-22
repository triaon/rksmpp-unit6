package ru.mirea.barnatovich.nobelserver.data.repository

import ru.mirea.barnatovich.nobelserver.domain.model.User
import ru.mirea.barnatovich.nobelserver.domain.repository.UserRepository

class InMemoryUserRepository : UserRepository {

    // учебные пользователи (данные хранятся в памяти)
    private val users = listOf(
        User(username = "admin", password = "admin123", role = "admin"),
        User(username = "student", password = "student123", role = "user")
    )

    override fun findByUsername(username: String): User? =
        users.find { it.username == username }
}
