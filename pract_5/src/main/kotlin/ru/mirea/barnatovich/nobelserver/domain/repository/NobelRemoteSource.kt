package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize

// источник данных — публичный Nobel Prize API
interface NobelRemoteSource {
    suspend fun loadPrizes(yearFrom: Int, yearTo: Int): List<NobelPrize>
}
