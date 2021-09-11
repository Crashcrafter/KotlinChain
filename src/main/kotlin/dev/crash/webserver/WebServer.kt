package dev.crash.webserver

import dev.crash.webserver.explorer.*
import dev.crash.webserver.rpc.*
import dev.crash.webserver.rpc.getAddress
import io.ktor.application.*
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
                    get("/getbalance") { getBalance() }
                    get("/getaddress") { getAddress() }
                    get("/gettx") { getTransaction() }
                    get("/getblock") { getBlock() }

                    get("{...}") {
                        println(call.request.local.uri)
                        call.respondText(call.request.local.uri)
                    }

                    post("/sendtx") { sendTx() }

                    post("{...}") {
                        println(call.request.local.uri)
                        call.respondText(call.request.local.uri)
                    }
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