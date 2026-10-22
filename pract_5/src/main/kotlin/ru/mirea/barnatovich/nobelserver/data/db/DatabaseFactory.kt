package ru.mirea.barnatovich.nobelserver.data.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {

    fun init(url: String, user: String, password: String) {
        val config = HikariConfig().apply {
            jdbcUrl = url
            username = user
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
        }
        Database.connect(HikariDataSource(config))
        transaction {
            SchemaUtils.create(UsersTable, PrizesTable, LaureatesTable, UserPrizesTable)
        }
    }

    // запросы к БД выполняем в пуле IO, чтобы не блокировать потоки Ktor
    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }
}
