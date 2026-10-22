package ru.mirea.barnatovich.photocatalog.presentation

import retrofit2.HttpException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun Throwable.toUserMessage(): String = when (this) {
    is java.net.UnknownHostException -> "Нет подключения к интернету"
    is java.net.SocketTimeoutException -> "Сервер не отвечает"
    is HttpException -> "Ошибка сервера: ${code()}"
    is java.io.IOException -> "Ошибка сети, проверьте подключение"
    else -> "Не удалось загрузить данные"
}
