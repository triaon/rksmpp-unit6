package ru.mirea.barnatovich.nobelserver.domain.usecase

import ru.mirea.barnatovich.nobelserver.domain.model.Laureate
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.repository.PrizeRepository

class GetPrizesUseCase(private val repository: PrizeRepository) {
    operator fun invoke(): List<NobelPrize> = repository.getAll()
}

class GetPrizeUseCase(private val repository: PrizeRepository) {
    operator fun invoke(year: Int, category: String): NobelPrize? =
        repository.find(year, category.lowercase())
}

class GetLaureatesUseCase(private val repository: PrizeRepository) {
    operator fun invoke(year: Int, category: String): List<Laureate>? =
        repository.find(year, category.lowercase())?.laureates
}
