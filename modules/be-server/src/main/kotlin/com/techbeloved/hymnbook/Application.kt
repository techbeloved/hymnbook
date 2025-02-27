package com.techbeloved.hymnbook

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("com.techbeloved.hymnbook.Application")

fun main() {
    log.debug("Starting application")
    embeddedServer(Netty, port = SERVERPORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            log.error("An unknown error occurred", cause)
            call.respondText(
                text = "500: $cause",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
    }
}
