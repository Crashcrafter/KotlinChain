package dev.crash.webserver

import dev.crash.webserver.explorer.*
import dev.crash.webserver.rpc.*
import dev.crash.webserver.rpc.getAddress
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object WebServer {
    fun start(port: Int){
        println("Starting RPC server...")
        embeddedServer(Netty, port = port) {
            routing {
                route("/api") {
                    get("/getBalance") { getBalance() }
                    get("/getAddress") { getAddress() }
                    get("/getTx") { getTransaction() }
                    get("/getBlock") { getBlock() }
                    get("/blocks") { getLastBlocks() }
                    get("/txs") { getLastTxs() }
                    get("/doc") { call.respondRedirect("https://github.com/Cr4shdev/KotlinChain/wiki/Rest-API") }

                    get("{...}") { call.respond(HttpStatusCode.NotFound, call.request.local.uri) }

                    post("/sendTx") { sendTx() }

                    post("{...}") { call.respond(HttpStatusCode.NotFound, call.request.local.uri) }
                }
                get("/") { mainPage() }
                get("/block") { blockPage() }
                get("/blocks") { lastBlocksPage() }
                get("/address") { addressPage() }
                get("/tx") { transactionPage() }
                get("/txs") { lastTransactionsPage() }
            }
        }.start()
        println("RPC server started!")
    }
}