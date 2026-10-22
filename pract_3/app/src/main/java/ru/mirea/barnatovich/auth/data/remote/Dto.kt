package ru.mirea.barnatovich.auth.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val accessToken: String, val id: Int, val username: String)

@Serializable
data class UsersResponse(val users: List<UserDto>)

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String
)
