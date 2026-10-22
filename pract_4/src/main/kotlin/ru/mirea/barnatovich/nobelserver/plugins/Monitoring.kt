package ru.mirea.barnatovich.nobelserver.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.request.userAgent
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            "${call.request.httpMethod.value} ${call.request.uri} -> $status " +
                "[${call.request.origin.remoteHost}, ${call.request.userAgent()}]"
        }
    }
}
