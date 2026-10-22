package ru.mirea.barnatovich.nobelclient.data.repository

import ru.mirea.barnatovich.nobelclient.data.remote.NobelApi
import ru.mirea.barnatovich.nobelclient.data.remote.dto.PrizeDto
import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.domain.model.UserProfile
import ru.mirea.barnatovich.nobelclient.domain.repository.NobelRepository

class NobelRepositoryImpl(private val api: NobelApi) : NobelRepository {

    override suspend fun getLaureates(year: Int?, category: PrizeCategory?): List<Laureate> =
        api.getPrizes(year, category?.code).flatMap { it.toLaureates() }

    override suspend fun getLaureateDetails(id: String, year: Int, category: PrizeCategory): LaureateDetails {
        // Отдельного эндпоинта для лауреата нет — берём премию по году и категории и ищем в ней лауреата
        val prize = api.getPrizes(year, category.code)
            .firstOrNull { p -> p.laureates.any { it.id.toString() == id } }
            ?: throw NoSuchElementException("Лауреат не найден")
        val laureate = prize.laureates.first { it.id.toString() == id }
        val favoriteIds = api.getFavorites().map { it.prize.id }
        return LaureateDetails(
            id = id,
            prizeId = prize.id,
            fullName = laureate.fullName,
            year = year,
            category = category,
            motivation = laureate.motivation,
            birthPlace = laureate.birthPlace.ifBlank { null },
            birthCountry = laureate.birthCountry.ifBlank { null },
            photoUrl = laureate.portraitUrl.ifBlank { null },
            isFavorite = prize.id in favoriteIds
        )
    }

    override suspend fun getFavorites(): List<Laureate> =
        api.getFavorites().flatMap { it.prize.toLaureates() }

    override suspend fun addFavorite(prizeId: Int) = api.addFavorite(prizeId)

    override suspend fun removeFavorite(prizeId: Int) = api.removeFavorite(prizeId)

    override suspend fun getProfile(): UserProfile =
        api.getProfile().let { UserProfile(it.username, it.role, it.favoritesCount) }

    // Премия может быть разделена между несколькими лауреатами — показываем каждого отдельно
    private fun PrizeDto.toLaureates(): List<Laureate> {
        val prizeCategory = PrizeCategory.fromCode(category) ?: return emptyList()
        return laureates.map {
            Laureate(
                id = it.id.toString(),
                prizeId = id,
                year = awardYear,
                category = prizeCategory,
                fullName = it.fullName,
                motivation = it.motivation
            )
        }
    }
}
