package ru.mirea.barnatovich.auth.domain.usecase

import ru.mirea.barnatovich.auth.domain.repository.AuthRepository

class IsLoggedInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Boolean = repository.isLoggedIn()
}
