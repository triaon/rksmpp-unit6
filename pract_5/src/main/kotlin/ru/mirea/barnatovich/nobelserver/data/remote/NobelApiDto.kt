package ru.mirea.barnatovich.nobelserver.data.remote

import kotlinx.serialization.Serializable

// DTO ответов https://api.nobelprize.org/2.1 (только нужные поля)

@Serializable
data class NobelPrizesResponse(val nobelPrizes: List<PrizeDto>)

@Serializable
data class LocalizedText(val en: String = "")

@Serializable
data class PrizeDto(
    val awardYear: String,
    val category: LocalizedText,
    val categoryFullName: LocalizedText,
    val dateAwarded: String = "",
    val prizeAmount: Long = 0,
    val topMotivation: LocalizedText? = null,
    val laureates: List<LaureateShortDto> = emptyList()
)

@Serializable
data class LaureateShortDto(
    val id: String,
    val fullName: LocalizedText? = null,
    val orgName: LocalizedText? = null,
    val portion: String = "",
    val motivation: LocalizedText? = null
)

@Serializable
data class LaureateDetailDto(
    val id: String,
    val birth: EventDto? = null,
    val founded: EventDto? = null,
    val wikidata: WikidataLinkDto? = null
)

@Serializable
data class EventDto(val place: PlaceDto? = null)

@Serializable
data class PlaceDto(
    val country: LocalizedText? = null,
    val countryNow: LocalizedText? = null,
    val locationString: LocalizedText? = null
)

@Serializable
data class WikidataLinkDto(val id: String)
