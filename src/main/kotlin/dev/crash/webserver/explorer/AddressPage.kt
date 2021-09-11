package dev.crash.webserver.explorer

import dev.crash.webserver.getGetParam
import io.ktor.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.addressPage() {
    val address = getGetParam("address") ?: ""
}