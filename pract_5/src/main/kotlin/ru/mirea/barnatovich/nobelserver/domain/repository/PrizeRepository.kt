package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize

interface PrizeRepository {
    suspend fun getPrizes(year: Int?, category: String?): List<NobelPrize>
    suspend fun getById(id: Int): NobelPrize?
    suspend fun count(): Long
    suspend fun saveAll(prizes: List<NobelPrize>)
}
