package ru.mirea.barnatovich.photocatalog.data.repository

import android.content.ContentResolver
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mirea.barnatovich.photocatalog.data.remote.PhotoDto
import ru.mirea.barnatovich.photocatalog.data.remote.PicsumApi
import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.domain.repository.PhotoRepository
import java.io.IOException
import kotlin.random.Random

class PhotoRepositoryImpl(
    private val api: PicsumApi,
    private val contentResolver: ContentResolver
) : PhotoRepository {

    // Берём случайную страницу каталога, чтобы при каждом запуске были разные фото
    override suspend fun getPhotos(): List<Photo> =
        api.getPhotos(page = Random.nextInt(1, 30)).map { it.toDomain() }

    override suspend fun getPhoto(id: String): Photo = api.getPhoto(id).toDomain()

    override suspend fun savePhoto(photo: Photo, destination: String) {
        val body = api.download(photo.downloadUrl)
        withContext(Dispatchers.IO) {
            val output = contentResolver.openOutputStream(destination.toUri())
                ?: throw IOException("Не удалось открыть файл")
            output.use { out -> body.byteStream().use { it.copyTo(out) } }
        }
    }

    private fun PhotoDto.toDomain() = Photo(
        id = id,
        author = author,
        width = width,
        height = height,
        pageUrl = url,
        downloadUrl = downloadUrl,
        // picsum умеет отдавать уменьшенную копию по id
        thumbnailUrl = "https://picsum.photos/id/$id/400/400"
    )
}
