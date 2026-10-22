package ru.mirea.barnatovich.nobelserver.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.mirea.barnatovich.nobelserver.domain.model.Laureate
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.repository.NobelRemoteSource
import java.net.URLEncoder
import java.security.MessageDigest

class NobelApiClient : NobelRemoteSource {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            headers.append(HttpHeaders.UserAgent, "NobelServer/1.0 (RTU MIREA student project)")
        }
    }

    override suspend fun loadPrizes(yearFrom: Int, yearTo: Int): List<NobelPrize> {
        val response: NobelPrizesResponse = client.get("https://api.nobelprize.org/2.1/nobelPrizes") {
            parameter("nobelPrizeYear", yearFrom)
            parameter("yearTo", yearTo)
            parameter("limit", 100)
            parameter("sort", "desc")
        }.body()

        // страна рождения есть только в карточке лауреата — грузим их пачками по 10 запросов
        val ids = response.nobelPrizes.flatMap { p -> p.laureates.map { it.id } }.distinct()
        val details = ids.chunked(10).flatMap { chunk ->
            coroutineScope { chunk.map { id -> async { loadLaureate(id) } }.awaitAll() }
        }.filterNotNull().associateBy { it.id }
        val portraits = loadPortraits(details.values.mapNotNull { it.wikidata?.id })

        return response.nobelPrizes.map { dto ->
            val category = CATEGORIES[dto.category.en] ?: dto.category.en.lowercase()
            val laureates = dto.laureates.map { l ->
                val detail = details[l.id]
                val place = detail?.birth?.place ?: detail?.founded?.place
                Laureate(
                    id = l.id.toInt(),
                    fullName = l.fullName?.en ?: l.orgName?.en ?: "",
                    portion = l.portion,
                    motivation = l.motivation?.en ?: "",
                    birthCountry = place?.country?.en ?: place?.countryNow?.en ?: "",
                    birthPlace = place?.locationString?.en ?: "",
                    portraitUrl = detail?.wikidata?.id?.let { portraits[it] } ?: ""
                )
            }
            NobelPrize(
                id = 0,
                awardYear = dto.awardYear.toInt(),
                category = category,
                categoryFullName = dto.categoryFullName.en,
                dateAwarded = dto.dateAwarded,
                prizeAmount = dto.prizeAmount,
                motivation = dto.topMotivation?.en ?: laureates.firstOrNull()?.motivation ?: "",
                detailLink = "https://www.nobelprize.org/prizes/${LINK_SLUGS[category] ?: category}/${dto.awardYear}/summary/",
                laureates = laureates
            )
        }
    }

    private suspend fun loadLaureate(id: String): LaureateDetailDto? = try {
        client.get("https://api.nobelprize.org/2.1/laureate/$id").body<List<LaureateDetailDto>>().firstOrNull()
    } catch (e: Exception) {
        null
    }

    // Портреты берём из Wikidata (свойство P18 — изображение) и строим ссылку на превью 330px в Wikimedia Commons
    private suspend fun loadPortraits(wikidataIds: List<String>): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (chunk in wikidataIds.distinct().chunked(50)) {
            val json: JsonObject = client.get("https://www.wikidata.org/w/api.php") {
                parameter("action", "wbgetentities")
                parameter("format", "json")
                parameter("props", "claims")
                parameter("ids", chunk.joinToString("|"))
            }.body()
            for ((qid, entity) in json["entities"]!!.jsonObject) {
                val claim = entity.jsonObject["claims"]?.jsonObject?.get("P18")?.jsonArray?.firstOrNull() ?: continue
                val fileName = claim.jsonObject["mainsnak"]!!.jsonObject["datavalue"]!!.jsonObject["value"]!!
                    .jsonPrimitive.content.replace(' ', '_')
                result[qid] = commonsThumbUrl(fileName)
            }
        }
        return result
    }

    private fun commonsThumbUrl(fileName: String): String {
        val md5 = MessageDigest.getInstance("MD5").digest(fileName.toByteArray())
            .joinToString("") { "%02x".format(it) }
        val encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20")
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/${md5[0]}/${md5.substring(0, 2)}/$encoded/330px-$encoded"
    }

    companion object {
        private val CATEGORIES = mapOf(
            "Physics" to "physics",
            "Chemistry" to "chemistry",
            "Physiology or Medicine" to "medicine",
            "Literature" to "literature",
            "Peace" to "peace",
            "Economic Sciences" to "economics"
        )
        private val LINK_SLUGS = mapOf("economics" to "economic-sciences")
    }
}
