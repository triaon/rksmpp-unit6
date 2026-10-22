package ru.mirea.barnatovich.nobelserver.data.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 100)
    val role = varchar("role", 20)
    override val primaryKey = PrimaryKey(id)
}

object PrizesTable : Table("prizes") {
    val id = integer("id").autoIncrement()
    val awardYear = integer("award_year")
    val category = varchar("category", 20)
    val fullName = varchar("full_name", 200)
    val motivation = text("motivation")
    val detailLink = varchar("detail_link", 300)
    val dateAwarded = varchar("date_awarded", 20)
    val prizeAmount = long("prize_amount")
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(awardYear, category)
    }
}

object LaureatesTable : Table("laureates") {
    val id = integer("id").autoIncrement()
    val prizeId = integer("prize_id").references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val nobelId = integer("nobel_id")
    val fullName = varchar("full_name", 200)
    val portion = varchar("portion", 10)
    val motivation = text("motivation")
    val birthCountry = varchar("birth_country", 100)
    val birthPlace = varchar("birth_place", 200)
    val portraitUrl = varchar("portrait_url", 500)
    override val primaryKey = PrimaryKey(id)
}

object UserPrizesTable : Table("user_prizes") {
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val prizeId = integer("prize_id").references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val addedAt = timestamp("added_at")
    override val primaryKey = PrimaryKey(userId, prizeId)
}
