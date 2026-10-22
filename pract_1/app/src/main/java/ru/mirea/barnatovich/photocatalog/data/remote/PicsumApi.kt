package ru.mirea.barnatovich.photocatalog.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface PicsumApi {

    @GET("v2/list")
    suspend fun getPhotos(@Query("page") page: Int = 1, @Query("limit") limit: Int = 30): List<PhotoDto>

    @GET("id/{id}/info")
    suspend fun getPhoto(@Path("id") id: String): PhotoDto

    // Скачивание файла по полному адресу, без загрузки целиком в память
    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody
}
