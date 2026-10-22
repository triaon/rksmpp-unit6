package ru.mirea.barnatovich.photocatalog

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.mirea.barnatovich.photocatalog.data.remote.PicsumApi
import ru.mirea.barnatovich.photocatalog.data.repository.PhotoRepositoryImpl
import ru.mirea.barnatovich.photocatalog.domain.repository.PhotoRepository
import ru.mirea.barnatovich.photocatalog.domain.usecase.GetPhotoUseCase
import ru.mirea.barnatovich.photocatalog.domain.usecase.GetPhotosUseCase
import ru.mirea.barnatovich.photocatalog.domain.usecase.SavePhotoUseCase

// Ручной DI: создаём Retrofit, репозиторий и use case-ы в одном месте
class AppContainer(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val api: PicsumApi = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(PicsumApi::class.java)

    private val repository: PhotoRepository = PhotoRepositoryImpl(api, context.contentResolver)

    val getPhotosUseCase = GetPhotosUseCase(repository)
    val getPhotoUseCase = GetPhotoUseCase(repository)
    val savePhotoUseCase = SavePhotoUseCase(repository)
}
