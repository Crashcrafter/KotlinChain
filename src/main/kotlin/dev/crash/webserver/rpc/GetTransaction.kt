package dev.crash.webserver.rpc

import dev.crash.webserver.getGetParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getTransaction(){
    val txid = getGetParam("txid") ?: return
    //TODO: Get Transaction Info
    call.respond(HttpStatusCode.OK, "Transaction info")
}