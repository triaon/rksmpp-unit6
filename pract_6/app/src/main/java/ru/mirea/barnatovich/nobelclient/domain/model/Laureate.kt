package ru.mirea.barnatovich.nobelclient.domain.model

// Элемент списка: один лауреат одной премии
data class Laureate(
    val id: String,
    val prizeId: Int,
    val year: Int,
    val category: PrizeCategory,
    val fullName: String,
    val motivation: String
)

data class LaureateDetails(
    val id: String,
    val prizeId: Int,
    val fullName: String,
    val year: Int,
    val category: PrizeCategory,
    val motivation: String,
    val birthPlace: String?,
    val birthCountry: String?,
    val photoUrl: String?,
    val isFavorite: Boolean
)

data class UserProfile(
    val username: String,
    val role: String,
    val favoritesCount: Int
)
