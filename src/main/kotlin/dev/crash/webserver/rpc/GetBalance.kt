package dev.crash.webserver.rpc

import dev.crash.storage.AddressTrie
import dev.crash.webserver.getAddress
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getBalance(){
    val address = getAddress() ?: return
    val addressState = AddressTrie.getAddress(address)
    call.respond(HttpStatusCode.OK, addressState.balance)
}