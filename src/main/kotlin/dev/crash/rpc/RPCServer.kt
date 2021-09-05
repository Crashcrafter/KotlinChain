package dev.crash.rpc

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object RPCServer {
    fun start(port: Int){
        println("Starting RPC server...")
        embeddedServer(Netty, port = port) {
            routing {
                get("{...}") {
                    println(call.request.local.uri)
                    call.respondText(call.request.local.uri)
                }
                post("/sendtx") {
                    val hex = call.request.headers["hex"]
                    if(hex == null){
                        call.respond(HttpStatusCode.BadRequest, "No tx hex in header provided")
                        return@post
                    }
                    //TODO: Check valid
                    //TODO: Push tx
                    call.respond(HttpStatusCode.OK, "txhash")
                }
                post("{...}") {
                    println(call.request.local.uri)
                    call.respondText(call.request.local.uri)
                }
            }
        }.start()
        println("RPC server started!")
    }
}