package ru.mirea.barnatovich.nobel.data.remote.dto

import kotlinx.serialization.Serializable

// В API почти все строки приходят словарём по языкам: {"en": "...", "se": "..."}
@Serializable
data class TextDto(val en: String? = null)

@Serializable
data class NobelPrizesResponse(val nobelPrizes: List<NobelPrizeDto> = emptyList())

@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: TextDto,
    val laureates: List<PrizeLaureateDto> = emptyList()
)

@Serializable
data class PrizeLaureateDto(
    val id: String,
    val fullName: TextDto? = null,
    val orgName: TextDto? = null,
    val motivation: TextDto? = null
)

@Serializable
data class LaureateDto(
    val id: String,
    val fullName: TextDto? = null,
    val orgName: TextDto? = null,
    val birth: EventDto? = null,
    val founded: EventDto? = null,
    val wikipedia: WikipediaDto? = null,
    val nobelPrizes: List<LaureatePrizeDto> = emptyList()
)

@Serializable
data class EventDto(val place: PlaceDto? = null)

@Serializable
data class PlaceDto(
    val country: TextDto? = null,
    val locationString: TextDto? = null
)

@Serializable
data class WikipediaDto(val slug: String? = null)

@Serializable
data class LaureatePrizeDto(
    val awardYear: String,
    val category: TextDto,
    val motivation: TextDto? = null
)

// Ответ Wikipedia REST API (page/summary) — оттуда берём портрет
@Serializable
data class WikiSummaryDto(val thumbnail: WikiImageDto? = null)

@Serializable
data class WikiImageDto(val source: String)
