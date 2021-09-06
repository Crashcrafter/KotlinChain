package dev.crash.webserver

import dev.crash.crypto.validateAddress
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getAddress(): String? {
    val address = getGetParam("address")?.removePrefix("0x") ?: return null
    if(!validateAddress(address)){
        call.respond(HttpStatusCode.BadRequest, "Invalid address")
        return null
    }
    return address
}

suspend fun PipelineContext<Unit, ApplicationCall>.getGetParam(paramName: String): String? {
    val param = call.request.queryParameters[paramName]
    if(param == null){
        call.respond(HttpStatusCode.BadRequest, "No $paramName provided")
        return null
    }
    return param
}

suspend fun PipelineContext<Unit, ApplicationCall>.getPostParam(paramName: String): String? {
    val param = call.request.headers[paramName]
    if(param == null){
        call.respond(HttpStatusCode.BadRequest, "No $paramName provided")
        return null
    }
    return param
}