package ru.mirea.barnatovich.nobelserver.data.repository

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.mirea.barnatovich.nobelserver.data.db.DatabaseFactory.dbQuery
import ru.mirea.barnatovich.nobelserver.data.db.LaureatesTable
import ru.mirea.barnatovich.nobelserver.data.db.PrizesTable
import ru.mirea.barnatovich.nobelserver.domain.model.Laureate
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.repository.PrizeRepository

class PostgresPrizeRepository : PrizeRepository {

    override suspend fun getPrizes(year: Int?, category: String?): List<NobelPrize> = dbQuery {
        var condition: Op<Boolean> = Op.TRUE
        if (year != null) condition = condition and (PrizesTable.awardYear eq year)
        if (category != null) condition = condition and (PrizesTable.category eq category)
        val rows = PrizesTable.selectAll().where(condition)
            .orderBy(PrizesTable.awardYear to SortOrder.DESC, PrizesTable.category to SortOrder.ASC)
            .toList()
        withLaureates(rows)
    }

    override suspend fun getById(id: Int): NobelPrize? = dbQuery {
        withLaureates(PrizesTable.selectAll().where { PrizesTable.id eq id }.toList()).firstOrNull()
    }

    override suspend fun count(): Long = dbQuery { PrizesTable.selectAll().count() }

    override suspend fun saveAll(prizes: List<NobelPrize>) = dbQuery {
        for (prize in prizes) {
            val prizeId = PrizesTable.insert {
                it[awardYear] = prize.awardYear
                it[category] = prize.category
                it[fullName] = prize.categoryFullName
                it[motivation] = prize.motivation
                it[detailLink] = prize.detailLink
                it[dateAwarded] = prize.dateAwarded
                it[prizeAmount] = prize.prizeAmount
            } get PrizesTable.id
            for (laureate in prize.laureates) {
                LaureatesTable.insert {
                    it[LaureatesTable.prizeId] = prizeId
                    it[nobelId] = laureate.id
                    it[fullName] = laureate.fullName
                    it[portion] = laureate.portion
                    it[motivation] = laureate.motivation
                    it[birthCountry] = laureate.birthCountry
                    it[birthPlace] = laureate.birthPlace
                    it[portraitUrl] = laureate.portraitUrl
                }
            }
        }
    }

    // подтягиваем лауреатов одним запросом для всех найденных премий
    private fun withLaureates(prizeRows: List<ResultRow>): List<NobelPrize> {
        val ids = prizeRows.map { it[PrizesTable.id] }
        val laureatesByPrize = if (ids.isEmpty()) emptyMap() else
            LaureatesTable.selectAll().where { LaureatesTable.prizeId inList ids }
                .orderBy(LaureatesTable.id)
                .groupBy({ it[LaureatesTable.prizeId] }, { it.toLaureate() })
        return prizeRows.map { it.toPrize(laureatesByPrize[it[PrizesTable.id]].orEmpty()) }
    }
}

fun ResultRow.toPrize(laureates: List<Laureate>) = NobelPrize(
    id = this[PrizesTable.id],
    awardYear = this[PrizesTable.awardYear],
    category = this[PrizesTable.category],
    categoryFullName = this[PrizesTable.fullName],
    dateAwarded = this[PrizesTable.dateAwarded],
    prizeAmount = this[PrizesTable.prizeAmount],
    motivation = this[PrizesTable.motivation],
    detailLink = this[PrizesTable.detailLink],
    laureates = laureates
)

fun ResultRow.toLaureate() = Laureate(
    id = this[LaureatesTable.nobelId],
    fullName = this[LaureatesTable.fullName],
    portion = this[LaureatesTable.portion],
    motivation = this[LaureatesTable.motivation],
    birthCountry = this[LaureatesTable.birthCountry],
    birthPlace = this[LaureatesTable.birthPlace],
    portraitUrl = this[LaureatesTable.portraitUrl]
)
