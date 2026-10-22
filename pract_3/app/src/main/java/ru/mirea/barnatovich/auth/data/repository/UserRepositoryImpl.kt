package ru.mirea.barnatovich.auth.data.repository

import ru.mirea.barnatovich.auth.data.remote.DummyJsonApi
import ru.mirea.barnatovich.auth.data.remote.UserDto
import ru.mirea.barnatovich.auth.domain.model.User
import ru.mirea.barnatovich.auth.domain.repository.UserRepository

class UserRepositoryImpl(private val api: DummyJsonApi) : UserRepository {

    override suspend fun getUsers(): List<User> = api.getUsers().users.map { it.toDomain() }

    override suspend fun getUser(id: Int): User = api.getUser(id).toDomain()

    private fun UserDto.toDomain() = User(id, firstName, lastName, username, email, image)
}
