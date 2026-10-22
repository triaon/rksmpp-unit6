package ru.mirea.barnatovich.nobelserver.routes

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.nobelserver.domain.model.FavoritePrize
import ru.mirea.barnatovich.nobelserver.domain.model.UserProfile
import ru.mirea.barnatovich.nobelserver.domain.usecase.AddFavoriteResult
import ru.mirea.barnatovich.nobelserver.domain.usecase.AddFavoriteUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetFavoritesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetProfileUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.RemoveFavoriteUseCase
import ru.mirea.barnatovich.nobelserver.plugins.ErrorResponse
import ru.mirea.barnatovich.nobelserver.plugins.userId

@Serializable
data class MessageResponse(val message: String)

// маршруты внутри authenticate { } — нужен заголовок Authorization: Bearer <token>
fun Route.userRoutes(
    getProfile: GetProfileUseCase,
    getFavorites: GetFavoritesUseCase,
    addFavorite: AddFavoriteUseCase,
    removeFavorite: RemoveFavoriteUseCase
) {
    get("/users/me", {
        tags("Пользователь")
        summary = "Профиль текущего пользователя"
        response {
            code(HttpStatusCode.OK) { body<UserProfile>() }
        }
    }) {
        val profile = getProfile(call.userId())
        if (profile == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Пользователь не найден"))
        } else {
            call.respond(profile)
        }
    }

    get("/users/me/prizes", {
        tags("Пользователь")
        summary = "Избранные премии"
        response {
            code(HttpStatusCode.OK) { body<List<FavoritePrize>>() }
        }
    }) {
        call.respond(getFavorites(call.userId()))
    }

    post("/users/me/prizes/{prizeId}", {
        tags("Пользователь")
        summary = "Добавить премию в избранное"
        request {
            pathParameter<Int>("prizeId") { description = "id премии" }
        }
        response {
            code(HttpStatusCode.Created) { body<MessageResponse>() }
            code(HttpStatusCode.Conflict) {
                description = "Премия уже в избранном"
                body<ErrorResponse>()
            }
            code(HttpStatusCode.NotFound) { body<ErrorResponse>() }
        }
    }) {
        val prizeId = call.parameters["prizeId"]?.toIntOrNull()
        if (prizeId == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("prizeId должен быть числом"))
            return@post
        }
        when (addFavorite(call.userId(), prizeId)) {
            AddFavoriteResult.ADDED ->
                call.respond(HttpStatusCode.Created, MessageResponse("Премия $prizeId добавлена в избранное"))
            AddFavoriteResult.ALREADY_EXISTS ->
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Премия $prizeId уже в избранном"))
            AddFavoriteResult.PRIZE_NOT_FOUND ->
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Премия $prizeId не найдена"))
        }
    }

    delete("/users/me/prizes/{prizeId}", {
        tags("Пользователь")
        summary = "Удалить премию из избранного"
        request {
            pathParameter<Int>("prizeId") { description = "id премии" }
        }
        response {
            code(HttpStatusCode.OK) { body<MessageResponse>() }
            code(HttpStatusCode.NotFound) { body<ErrorResponse>() }
        }
    }) {
        val prizeId = call.parameters["prizeId"]?.toIntOrNull()
        if (prizeId != null && removeFavorite(call.userId(), prizeId)) {
            call.respond(MessageResponse("Премия $prizeId удалена из избранного"))
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Премии нет в избранном"))
        }
    }
}
