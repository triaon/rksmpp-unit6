package ru.mirea.barnatovich.auth.domain.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String
)

// Сервер ответил, что логин или пароль неверные
class InvalidCredentialsException : Exception("Неверные данные")
