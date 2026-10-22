package ru.mirea.barnatovich.nobelserver.domain.usecase

import ru.mirea.barnatovich.nobelserver.domain.model.User
import ru.mirea.barnatovich.nobelserver.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    // возвращает пользователя, если логин и пароль совпали, иначе null
    operator fun invoke(username: String, password: String): User? {
        val user = repository.findByUsername(username) ?: return null
        return if (user.password == password) user else null
    }
}
