package ru.mirea.barnatovich.nobelclient.domain.repository

import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.domain.model.UserProfile

interface NobelRepository {
    suspend fun getLaureates(year: Int?, category: PrizeCategory?): List<Laureate>

    suspend fun getLaureateDetails(id: String, year: Int, category: PrizeCategory): LaureateDetails

    suspend fun getFavorites(): List<Laureate>

    suspend fun addFavorite(prizeId: Int)

    suspend fun removeFavorite(prizeId: Int)

    suspend fun getProfile(): UserProfile
}
