package dev.crash.webserver

import dev.crash.webserver.rpc.getAddress
import dev.crash.webserver.rpc.getBalance
import dev.crash.webserver.rpc.getTransaction
import dev.crash.webserver.rpc.sendTx
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object RPCServer {
    fun start(port: Int){
        println("Starting RPC server...")
        embeddedServer(Netty, port = port) {
            routing {
                route("/api") {
                    get("/getBalance") { getBalance() }
                    get("/getAddress") { getAddress() }
                    get("/getTransaction") { getTransaction() }

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
            }
        }.start()
        println("RPC server started!")
    }
}