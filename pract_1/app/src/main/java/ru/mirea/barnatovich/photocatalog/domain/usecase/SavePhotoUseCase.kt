package ru.mirea.barnatovich.photocatalog.domain.usecase

import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.domain.repository.PhotoRepository

class SavePhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(photo: Photo, destination: String) = repository.savePhoto(photo, destination)
}
