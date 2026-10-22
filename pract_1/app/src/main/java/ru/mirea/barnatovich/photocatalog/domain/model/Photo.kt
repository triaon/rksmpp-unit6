package ru.mirea.barnatovich.photocatalog.domain.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val pageUrl: String,
    val downloadUrl: String,
    val thumbnailUrl: String
)
