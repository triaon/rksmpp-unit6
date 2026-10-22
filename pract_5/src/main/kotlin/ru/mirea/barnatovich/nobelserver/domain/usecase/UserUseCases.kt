package ru.mirea.barnatovich.nobelserver.domain.usecase

import ru.mirea.barnatovich.nobelserver.domain.model.FavoritePrize
import ru.mirea.barnatovich.nobelserver.domain.model.UserProfile
import ru.mirea.barnatovich.nobelserver.domain.repository.FavoriteRepository
import ru.mirea.barnatovich.nobelserver.domain.repository.PrizeRepository
import ru.mirea.barnatovich.nobelserver.domain.repository.UserRepository

class GetProfileUseCase(
    private val users: UserRepository,
    private val favorites: FavoriteRepository
) {
    suspend operator fun invoke(userId: Int): UserProfile? {
        val user = users.findById(userId) ?: return null
        return UserProfile(user.id, user.username, user.role, favorites.count(userId))
    }
}

class GetFavoritesUseCase(private val favorites: FavoriteRepository) {
    suspend operator fun invoke(userId: Int): List<FavoritePrize> = favorites.getFavorites(userId)
}

enum class AddFavoriteResult { ADDED, ALREADY_EXISTS, PRIZE_NOT_FOUND }

class AddFavoriteUseCase(
    private val prizes: PrizeRepository,
    private val favorites: FavoriteRepository
) {
    suspend operator fun invoke(userId: Int, prizeId: Int): AddFavoriteResult {
        if (prizes.getById(prizeId) == null) return AddFavoriteResult.PRIZE_NOT_FOUND
        return if (favorites.add(userId, prizeId)) AddFavoriteResult.ADDED else AddFavoriteResult.ALREADY_EXISTS
    }
}

class RemoveFavoriteUseCase(private val favorites: FavoriteRepository) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean = favorites.remove(userId, prizeId)
}
