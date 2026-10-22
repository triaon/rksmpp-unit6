package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize

interface PrizeRepository {
    fun getAll(): List<NobelPrize>
    fun find(year: Int, category: String): NobelPrize?
}
