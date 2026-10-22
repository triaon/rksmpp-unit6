package ru.mirea.barnatovich.nobelclient.domain.usecase

import ru.mirea.barnatovich.nobelclient.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.domain.repository.NobelRepository

class GetLaureateDetailsUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(id: String, year: Int, category: PrizeCategory): LaureateDetails =
        repository.getLaureateDetails(id, year, category)
}
