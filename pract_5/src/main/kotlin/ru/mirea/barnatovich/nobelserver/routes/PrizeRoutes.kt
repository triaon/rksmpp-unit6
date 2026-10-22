package ru.mirea.barnatovich.nobelserver.routes

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.mirea.barnatovich.nobelserver.domain.model.NobelPrize
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizeUseCase
import ru.mirea.barnatovich.nobelserver.domain.usecase.GetPrizesUseCase
import ru.mirea.barnatovich.nobelserver.plugins.ErrorResponse

fun Route.prizeRoutes(getPrizes: GetPrizesUseCase, getPrize: GetPrizeUseCase) {
    get("/prizes", {
        tags("Премии")
        summary = "Список премий с лауреатами"
        description = "Данные хранятся в PostgreSQL (при первом запуске импортируются из api.nobelprize.org)"
        request {
            queryParameter<Int>("year") {
                description = "Год премии, например 2023"
                required = false
            }
            queryParameter<String>("category") {
                description = "physics, chemistry, medicine, literature, peace, economics"
                required = false
            }
        }
        response {
            code(HttpStatusCode.OK) { body<List<NobelPrize>>() }
        }
    }) {
        val year = call.request.queryParameters["year"]?.toIntOrNull()
        val category = call.request.queryParameters["category"]?.takeIf { it.isNotBlank() }
        call.respond(getPrizes(year, category))
    }

    get("/prizes/{prizeId}", {
        tags("Премии")
        summary = "Премия по id"
        request {
            pathParameter<Int>("prizeId") { description = "id премии в БД" }
        }
        response {
            code(HttpStatusCode.OK) { body<NobelPrize>() }
            code(HttpStatusCode.NotFound) { body<ErrorResponse>() }
        }
    }) {
        val prizeId = call.parameters["prizeId"]?.toIntOrNull()
        val prize = prizeId?.let { getPrize(it) }
        if (prize == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Премия не найдена"))
        } else {
            call.respond(prize)
        }
    }
}
