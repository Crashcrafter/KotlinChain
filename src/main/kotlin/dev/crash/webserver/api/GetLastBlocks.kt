package dev.crash.webserver.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.chain.Block
import dev.crash.storage.BlockTrie
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.getLastBlocks() {
    val result = mutableListOf<Block.JsonObj>()
    BlockTrie.lastBlocks.forEach {
        result.add(it.getJsonObj())
    }
    call.respond(HttpStatusCode.OK, jacksonObjectMapper().writeValueAsString(result))
}