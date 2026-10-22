package ru.mirea.barnatovich.nobelserver.data.repository

import kotlinx.serialization.json.Json
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.repository.PrizeRepository

// Данные один раз скачаны с api.nobelprize.org/2.1 и лежат в resources/nobel_prizes.json
class InMemoryPrizeRepository : PrizeRepository {

    private val prizes: List<NobelPrize> = loadPrizes()

    override fun getAll(): List<NobelPrize> = prizes

    override fun find(year: Int, category: String): NobelPrize? =
        prizes.find { it.awardYear == year && it.category == category }

    private fun loadPrizes(): List<NobelPrize> {
        val text = javaClass.getResource("/nobel_prizes.json")!!.readText()
        return Json { ignoreUnknownKeys = true }.decodeFromString(text)
    }
}
