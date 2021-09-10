package dev.crash

import dev.crash.node.KotlinNode
import dev.crash.networking.BlockChainServer
import dev.crash.storage.saveDBs
import dev.crash.webserver.WebServer
import kotlin.system.exitProcess

suspend fun main(args: Array<String>){
    mainStart()
}

suspend fun mainStart(blockChainServerPort: Int = 8334, rpcServerPort: Int = 80, chainId: Int = 1){
    println("Starting KotlinChain Node...")
    CONFIG.CHAINID = chainId
    KotlinNode.loadNodeWallet()
    BlockChainServer.start(blockChainServerPort)
    WebServer.start(rpcServerPort)
    KotlinNode.start()
    while (true) {
        val cmd = readLine() ?: continue
        when(cmd) {
            "stop" -> shutdown()
        }
    }
}

fun shutdown() {
    saveDBs()
    exitProcess(0)
}