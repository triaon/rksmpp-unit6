package ru.mirea.barnatovich.nobel.domain.model

// Элемент списка: один лауреат одной премии
data class Laureate(
    val id: String,
    val year: Int,
    val category: PrizeCategory,
    val fullName: String,
    val motivation: String
)

data class LaureateDetails(
    val id: String,
    val fullName: String,
    val year: Int,
    val category: PrizeCategory,
    val motivation: String,
    val birthPlace: String?,
    val birthCountry: String?,
    val photoUrl: String?
)
