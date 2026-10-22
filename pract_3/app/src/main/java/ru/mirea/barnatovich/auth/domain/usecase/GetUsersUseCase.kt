package ru.mirea.barnatovich.auth.domain.usecase

import ru.mirea.barnatovich.auth.domain.model.User
import ru.mirea.barnatovich.auth.domain.repository.UserRepository

class GetUsersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): List<User> = repository.getUsers()
}
