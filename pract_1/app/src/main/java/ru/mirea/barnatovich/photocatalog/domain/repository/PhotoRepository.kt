package ru.mirea.barnatovich.photocatalog.domain.repository

import ru.mirea.barnatovich.photocatalog.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>

    suspend fun getPhoto(id: String): Photo

    // destination — Uri документа, выбранного пользователем через SAF
    suspend fun savePhoto(photo: Photo, destination: String)
}
