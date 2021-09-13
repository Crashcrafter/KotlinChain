package dev.crash.webserver.rpc

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.exceptions.*
import dev.crash.node.Mempool
import dev.crash.webserver.getPostParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.sendTx() {
    val hex = getPostParam("hex")?.removePrefix("0x") ?: return
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
            is CouldNotReadValueOfTypeException -> call.respond(HttpStatusCode.BadRequest, "malformed transaction, wrong structure")
            is InvalidAddressException -> call.respond(HttpStatusCode.BadRequest, "invalid address")
            is NonceException -> call.respond(HttpStatusCode.BadRequest, "invalid nonce")
            is InsufficientBalanceException -> call.respond(HttpStatusCode.InternalServerError, "insufficient balance for gasPrice*size + value")
            is TransactionOutputException -> call.respond(HttpStatusCode.BadRequest, ex.msg)
            else -> {
                ex.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "error parsing transaction, is your structure correct?")
            }
        }
        return
    }
    if(!Mempool.addTransaction(transaction)){
        call.respond(HttpStatusCode.InternalServerError, "Transaction already in next block")
        return
    }
    call.respond(HttpStatusCode.OK, transaction.txid)
}