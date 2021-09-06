package dev.crash.webserver.rpc

import dev.crash.webserver.getAddress
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getAddress(){
    val address = getAddress() ?: return
    //TODO: Get Address Info
    call.respond(HttpStatusCode.OK, "Address Info")
}