package ru.mirea.barnatovich.nobelclient.data

// JWT-токен от сервера; живёт, пока запущено приложение (срок токена всё равно 30 минут)
class TokenStorage {
    var token: String? = null
}
