package ru.mirea.barnatovich.nobelserver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val awardYear: Int,
    val category: String,
    val categoryFullName: String,
    val dateAwarded: String,
    val prizeAmount: Long,
    val motivation: String,
    val detailLink: String,
    val laureates: List<Laureate>
)

@Serializable
data class Laureate(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val birthCountry: String,
    val birthPlace: String,
    val portraitUrl: String
)
