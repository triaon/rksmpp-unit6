package ru.mirea.barnatovich.nobelserver.domain.repository

import ru.mirea.barnatovich.nobelserver.domain.model.FavoritePrize

interface FavoriteRepository {
    suspend fun getFavorites(userId: Int): List<FavoritePrize>
    suspend fun count(userId: Int): Int
    // false, если премия уже была в избранном
    suspend fun add(userId: Int, prizeId: Int): Boolean
    // false, если премии не было в избранном
    suspend fun remove(userId: Int, prizeId: Int): Boolean
}
