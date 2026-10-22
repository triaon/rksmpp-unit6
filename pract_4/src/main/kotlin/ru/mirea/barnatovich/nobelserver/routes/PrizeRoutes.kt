package ru.mirea.barnatovich.nobelserver.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetLaureatesUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizeUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizesUseCase
import ru.mirea.barnatovich.nobelserver.plugins.ErrorResponse

// краткая запись для списка премий (без лауреатов)
@Serializable
data class PrizeListItem(
    val awardYear: Int,
    val category: String,
    val categoryFullName: String,
    val motivation: String,
    val laureatesCount: Int
)

private fun NobelPrize.toListItem() =
    PrizeListItem(awardYear, category, categoryFullName, motivation, laureates.size)

fun Route.prizeRoutes(
    getPrizes: GetPrizesUseCase,
    getPrize: GetPrizeUseCase,
    getLaureates: GetLaureatesUseCase
) {
    get("/prizes") {
        call.respond(getPrizes().map { it.toListItem() })
    }

    get("/prizes/{year}/{category}") {
        val year = call.yearParam() ?: return@get
        val category = call.parameters["category"]!!
        val prize = getPrize(year, category)
        if (prize == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Премия $category за $year год не найдена"))
        } else {
            call.respond(prize)
        }
    }

    get("/prizes/{year}/{category}/laureates") {
        val year = call.yearParam() ?: return@get
        val category = call.parameters["category"]!!
        val laureates = getLaureates(year, category)
        if (laureates == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Премия $category за $year год не найдена"))
        } else {
            call.respond(laureates)
        }
    }
}

private suspend fun RoutingCall.yearParam(): Int? {
    val year = parameters["year"]?.toIntOrNull()
    if (year == null) respond(HttpStatusCode.BadRequest, ErrorResponse("Год должен быть числом"))
    return year
}
