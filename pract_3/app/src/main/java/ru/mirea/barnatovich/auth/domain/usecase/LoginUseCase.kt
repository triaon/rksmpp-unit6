package ru.mirea.barnatovich.auth.domain.usecase

import ru.mirea.barnatovich.auth.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String) = repository.login(username.trim(), password)
}
