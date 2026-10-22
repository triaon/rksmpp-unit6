package ru.mirea.barnatovich.nobelclient.data.remote.dto

import kotlinx.serialization.Serializable

// DTO ответов собственного сервера (задание 5)

@Serializable
data class PrizeDto(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val categoryFullName: String = "",
    val motivation: String = "",
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class LaureateDto(
    val id: Int,
    val fullName: String,
    val portion: String = "",
    val motivation: String = "",
    val birthCountry: String = "",
    val birthPlace: String = "",
    val portraitUrl: String = ""
)

@Serializable
data class FavoriteDto(val addedAt: String, val prize: PrizeDto)

@Serializable
data class LoginRequestDto(val username: String, val password: String)

@Serializable
data class LoginResponseDto(val token: String, val username: String, val role: String, val expiresIn: Long)

@Serializable
data class ProfileDto(val id: Int, val username: String, val role: String, val favoritesCount: Int)
