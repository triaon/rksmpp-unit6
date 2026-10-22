package ru.mirea.barnatovich.nobelserver.data.repository

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.mirea.barnatovich.nobelserver.data.db.DatabaseFactory.dbQuery
import ru.mirea.barnatovich.nobelserver.data.db.LaureatesTable
import ru.mirea.barnatovich.nobelserver.data.db.PrizesTable
import ru.mirea.barnatovich.nobelserver.data.db.UserPrizesTable
import ru.mirea.barnatovich.nobelserver.domain.model.FavoritePrize
import ru.mirea.barnatovich.nobelserver.domain.repository.FavoriteRepository
import java.time.Instant

class PostgresFavoriteRepository : FavoriteRepository {

    override suspend fun getFavorites(userId: Int): List<FavoritePrize> = dbQuery {
        val rows = (UserPrizesTable innerJoin PrizesTable)
            .selectAll()
            .where { UserPrizesTable.userId eq userId }
            .orderBy(UserPrizesTable.addedAt to SortOrder.DESC)
            .toList()
        val ids = rows.map { it[PrizesTable.id] }
        val laureatesByPrize = if (ids.isEmpty()) emptyMap() else
            LaureatesTable.selectAll().where { LaureatesTable.prizeId inList ids }
                .groupBy({ it[LaureatesTable.prizeId] }, { it.toLaureate() })
        rows.map {
            FavoritePrize(
                addedAt = it[UserPrizesTable.addedAt].toString(),
                prize = it.toPrize(laureatesByPrize[it[PrizesTable.id]].orEmpty())
            )
        }
    }

    override suspend fun count(userId: Int): Int = dbQuery {
        UserPrizesTable.selectAll().where { UserPrizesTable.userId eq userId }.count().toInt()
    }

    override suspend fun add(userId: Int, prizeId: Int): Boolean = dbQuery {
        val exists = UserPrizesTable.selectAll()
            .where { (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId) }
            .count() > 0
        if (!exists) {
            UserPrizesTable.insert {
                it[UserPrizesTable.userId] = userId
                it[UserPrizesTable.prizeId] = prizeId
                it[addedAt] = Instant.now()
            }
        }
        !exists
    }

    override suspend fun remove(userId: Int, prizeId: Int): Boolean = dbQuery {
        UserPrizesTable.deleteWhere {
            (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        } > 0
    }
}
