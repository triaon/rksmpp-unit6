package ru.mirea.barnatovich.nobel.domain.repository

import ru.mirea.barnatovich.nobel.domain.model.Laureate
import ru.mirea.barnatovich.nobel.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory

interface NobelRepository {
    suspend fun getLaureates(year: Int?, category: PrizeCategory?): List<Laureate>

    suspend fun getLaureateDetails(id: String, year: Int, category: PrizeCategory): LaureateDetails
}
