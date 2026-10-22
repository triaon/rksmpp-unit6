package ru.mirea.barnatovich.nobelserver.domain.usecase

import ru.mirea.barnatovich.nobelserver.domain.model.User
import ru.mirea.barnatovich.nobelserver.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): User? =
        repository.checkCredentials(username, password)
}
