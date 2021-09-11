package dev.crash.webserver.explorer

import dev.crash.webserver.getGetParam
import io.ktor.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.blockPage() {
    val blockNonce = getGetParam("block")?.toLong() ?: -1
}