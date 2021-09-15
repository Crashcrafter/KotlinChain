package dev.crash.webserver.api

import dev.crash.storage.BlockTrie
import dev.crash.webserver.getGetParam
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getBlock(){
    val blockNonce = getGetParam("blockNonce")?.toLong() ?: return
    val block = BlockTrie.getBlock(blockNonce)
    if(block == null){
        call.respond(HttpStatusCode.BadRequest, "Block not found")
    }else {
        call.respond(HttpStatusCode.OK, block.toString())
    }
}