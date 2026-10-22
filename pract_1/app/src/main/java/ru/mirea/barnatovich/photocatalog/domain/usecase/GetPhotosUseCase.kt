package ru.mirea.barnatovich.photocatalog.domain.usecase

import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.domain.repository.PhotoRepository

class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): List<Photo> = repository.getPhotos()
}
