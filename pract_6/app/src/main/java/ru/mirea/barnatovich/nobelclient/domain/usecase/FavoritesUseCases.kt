package ru.mirea.barnatovich.nobelclient.domain.usecase

import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.UserProfile
import ru.mirea.barnatovich.nobelclient.domain.repository.NobelRepository

class GetFavoritesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(): List<Laureate> = repository.getFavorites()
}

class ToggleFavoriteUseCase(private val repository: NobelRepository) {
    // возвращает новое состояние: true — премия теперь в избранном
    suspend operator fun invoke(prizeId: Int, isFavorite: Boolean): Boolean {
        if (isFavorite) repository.removeFavorite(prizeId) else repository.addFavorite(prizeId)
        return !isFavorite
    }
}

class GetProfileUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(): UserProfile = repository.getProfile()
}
