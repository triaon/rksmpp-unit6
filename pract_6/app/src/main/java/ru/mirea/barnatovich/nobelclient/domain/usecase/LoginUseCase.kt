package ru.mirea.barnatovich.nobelclient.domain.usecase

import ru.mirea.barnatovich.nobelclient.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String) =
        repository.login(username.trim(), password)
}

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.logout()
}
