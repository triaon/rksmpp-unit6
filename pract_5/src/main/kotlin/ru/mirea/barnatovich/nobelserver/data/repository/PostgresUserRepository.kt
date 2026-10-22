package ru.mirea.barnatovich.nobelserver.data.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.mindrot.jbcrypt.BCrypt
import ru.mirea.barnatovich.nobelserver.data.db.DatabaseFactory.dbQuery
import ru.mirea.barnatovich.nobelserver.data.db.UsersTable
import ru.mirea.barnatovich.nobelserver.domain.model.User
import ru.mirea.barnatovich.nobelserver.domain.repository.UserRepository

class PostgresUserRepository : UserRepository {

    override suspend fun checkCredentials(username: String, password: String): User? = dbQuery {
        val row = UsersTable.selectAll().where { UsersTable.username eq username }.singleOrNull()
        if (row != null && BCrypt.checkpw(password, row[UsersTable.passwordHash])) row.toUser() else null
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()?.toUser()
    }

    override suspend fun createIfMissing(username: String, password: String, role: String) = dbQuery {
        val exists = UsersTable.selectAll().where { UsersTable.username eq username }.count() > 0
        if (!exists) {
            UsersTable.insert {
                it[UsersTable.username] = username
                it[passwordHash] = BCrypt.hashpw(password, BCrypt.gensalt())
                it[UsersTable.role] = role
            }
        }
    }

    private fun ResultRow.toUser() = User(this[UsersTable.id], this[UsersTable.username], this[UsersTable.role])
}
