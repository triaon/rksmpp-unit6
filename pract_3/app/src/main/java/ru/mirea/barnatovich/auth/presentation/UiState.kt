package ru.mirea.barnatovich.auth.presentation

import ru.mirea.barnatovich.auth.domain.model.InvalidCredentialsException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun Throwable.toUserMessage(): String = when (this) {
    is InvalidCredentialsException -> "Неверные данные"
    is java.net.UnknownHostException, is java.net.ConnectException -> "Нет соединения"
    is java.net.SocketTimeoutException -> "Сервер не отвечает"
    is java.io.IOException -> "Нет соединения"
    else -> "Что-то пошло не так, попробуйте ещё раз"
}
