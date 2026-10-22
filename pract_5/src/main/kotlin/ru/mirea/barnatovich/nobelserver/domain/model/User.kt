package ru.mirea.barnatovich.nobelserver.domain.model

import kotlinx.serialization.Serializable

data class User(
    val id: Int,
    val username: String,
    val role: String
)

@Serializable
data class UserProfile(
    val id: Int,
    val username: String,
    val role: String,
    val favoritesCount: Int
)
