package ru.mirea.barnatovich.nobelserver.domain.model

data class User(
    val username: String,
    val password: String,
    val role: String
)
