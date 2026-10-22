package ru.mirea.barnatovich.nobel.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.mirea.barnatovich.nobel.data.remote.dto.LaureateDto
import ru.mirea.barnatovich.nobel.data.remote.dto.NobelPrizesResponse
import ru.mirea.barnatovich.nobel.data.remote.dto.WikiSummaryDto

class NobelApi(private val client: HttpClient, private val baseUrl: String) {

    suspend fun getPrizes(year: Int?, categoryCode: String?): NobelPrizesResponse =
        client.get("$baseUrl/nobelPrizes") {
            parameter("limit", 25)
            parameter("offset", 0)
            parameter("sort", "desc")
            parameter("nobelPrizeYear", year)
            parameter("nobelPrizeCategory", categoryCode)
        }.body()

    // API возвращает массив из одного лауреата
    suspend fun getLaureate(id: String): LaureateDto =
        client.get("$baseUrl/laureate/$id").body<List<LaureateDto>>().first()

    suspend fun getWikiSummary(slug: String): WikiSummaryDto =
        client.get("https://en.wikipedia.org/api/rest_v1/page/summary/$slug").body()
}
