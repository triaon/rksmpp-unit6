package ru.mirea.barnatovich.auth.domain.usecase

import ru.mirea.barnatovich.auth.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
