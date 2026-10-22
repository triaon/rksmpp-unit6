package ru.mirea.barnatovich.nobel.data.repository

import ru.mirea.barnatovich.nobel.data.remote.NobelApi
import ru.mirea.barnatovich.nobel.domain.model.Laureate
import ru.mirea.barnatovich.nobel.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobel.domain.repository.NobelRepository

class NobelRepositoryImpl(private val api: NobelApi) : NobelRepository {

    override suspend fun getLaureates(year: Int?, category: PrizeCategory?): List<Laureate> {
        val prizes = api.getPrizes(year, category?.code).nobelPrizes
        // Премия может быть разделена между несколькими лауреатами — показываем каждого отдельно
        return prizes.flatMap { prize ->
            val prizeCategory = PrizeCategory.fromApiName(prize.category.en.orEmpty())
                ?: return@flatMap emptyList()
            prize.laureates.map { laureate ->
                Laureate(
                    id = laureate.id,
                    year = prize.awardYear.toInt(),
                    category = prizeCategory,
                    fullName = laureate.fullName?.en ?: laureate.orgName?.en.orEmpty(),
                    motivation = laureate.motivation?.en.orEmpty()
                )
            }
        }
    }

    override suspend fun getLaureateDetails(id: String, year: Int, category: PrizeCategory): LaureateDetails {
        val dto = api.getLaureate(id)
        val prize = dto.nobelPrizes.find {
            it.awardYear.toInt() == year && it.category.en == category.apiName
        }
        // У организаций вместо места рождения — место основания
        val place = (dto.birth ?: dto.founded)?.place
        // Фото необязательно: если Википедия не ответила, просто показываем без него
        val photoUrl = dto.wikipedia?.slug?.let { slug ->
            runCatching { api.getWikiSummary(slug).thumbnail?.source }.getOrNull()
        }
        return LaureateDetails(
            id = dto.id,
            fullName = dto.fullName?.en ?: dto.orgName?.en.orEmpty(),
            year = year,
            category = category,
            motivation = prize?.motivation?.en.orEmpty(),
            birthPlace = place?.locationString?.en?.takeIf { it.isNotBlank() },
            birthCountry = place?.country?.en,
            photoUrl = photoUrl
        )
    }
}
