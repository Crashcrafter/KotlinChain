package dev.crash.webserver.api

import dev.crash.chain.Transaction
import dev.crash.webserver.getGetParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getTransaction(){
    val txid = getGetParam("txid") ?: return
    val tx = Transaction.fromTxId(txid)
    if(tx == null){
        call.respond(HttpStatusCode.BadRequest, "No tx with txid $txid")
    }else {
        call.respond(HttpStatusCode.OK, tx.toString())
    }
}