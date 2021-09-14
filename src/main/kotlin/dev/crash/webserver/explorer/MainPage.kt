package dev.crash.webserver.explorer

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.lang

suspend fun PipelineContext<Unit, ApplicationCall>.mainPage() {
    call.respondHtml {
        lang = "en"
        head {

        }
        body {

        }
    }
}