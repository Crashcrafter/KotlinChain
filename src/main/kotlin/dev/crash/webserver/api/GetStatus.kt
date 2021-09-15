package dev.crash.webserver.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.CONFIG
import dev.crash.crypto.toHexString
import dev.crash.storage.BlockTrie
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

private data class NodeStatus(
    val synced: Boolean,
    val chainId: Int,
    val version: Int,
    val lastBlockNonce: Long,
    val lastBlockHash: String
)

suspend fun PipelineContext<Unit, ApplicationCall>.getStatus() {
    call.respond(HttpStatusCode.OK, jacksonObjectMapper().writeValueAsString(
        NodeStatus(true, CONFIG.CHAINID, CONFIG.VERSION, BlockTrie.lastBlockNonce, BlockTrie.lastBlockHash.toHexString())))
}