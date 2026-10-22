package ru.mirea.barnatovich.nobel.domain.usecase

import ru.mirea.barnatovich.nobel.domain.model.Laureate
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobel.domain.repository.NobelRepository

class GetLaureatesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: Int? = null, category: PrizeCategory? = null): List<Laureate> =
        repository.getLaureates(year, category)
}
