package dev.crash.webserver.rpc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.chain.Transaction
import dev.crash.storage.TransactionTrie
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getLastTxs() {
    val result = mutableListOf<Transaction.JsonObj>()
    TransactionTrie.lastTxs.forEach {
        result.add(it.getJsonObj())
    }
    call.respond(HttpStatusCode.OK, jacksonObjectMapper().writeValueAsString(result))
}