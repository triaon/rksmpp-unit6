package ru.mirea.barnatovich.nobelserver.domain.usecase

import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.repository.NobelRemoteSource
import ru.mirea.barnatovich.nobelserver.domain.repository.PrizeRepository

class GetPrizesUseCase(private val repository: PrizeRepository) {
    suspend operator fun invoke(year: Int?, category: String?): List<NobelPrize> =
        repository.getPrizes(year, category?.lowercase())
}

class GetPrizeUseCase(private val repository: PrizeRepository) {
    suspend operator fun invoke(id: Int): NobelPrize? = repository.getById(id)
}

// Разовый импорт: если таблица премий пуста, загружаем данные из публичного API и сохраняем в БД
class ImportPrizesUseCase(
    private val repository: PrizeRepository,
    private val remote: NobelRemoteSource
) {
    suspend operator fun invoke(yearFrom: Int, yearTo: Int): Int {
        if (repository.count() > 0) return 0
        val prizes = remote.loadPrizes(yearFrom, yearTo)
        repository.saveAll(prizes)
        return prizes.size
    }
}
