package dev.crash.rpc

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.exceptions.CouldNotReadValueOfTypeException
import dev.crash.exceptions.ECDSAValidationException
import dev.crash.node.Mempool
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.sendTx() {
    val hex = call.request.headers["hex"]
    if(hex == null){
        call.respond(HttpStatusCode.BadRequest, "No tx hex in header provided")
        return
    }
    val bytes = try {
        hex.asHexByteArray()
    }catch (ex: Exception) {
        call.respond(HttpStatusCode.BadRequest, "header field hex is not hex encoded")
        return
    }
    val transaction = try {
        Transaction(bytes)
    }catch (ex: Exception) {
        when(ex){
            is ECDSAValidationException -> call.respond(HttpStatusCode.InternalServerError, ex.msg)
            is CouldNotReadValueOfTypeException -> call.respond(HttpStatusCode.InternalServerError, "malformed transaction, wrong structure")
            else -> call.respond(HttpStatusCode.InternalServerError, "error parsing transaction, is your structure correct?")
        }
        return
    }
    Mempool.nextBlock.add(transaction)
    call.respond(HttpStatusCode.OK, transaction.txid)
}