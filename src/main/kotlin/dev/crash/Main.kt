package dev.crash

import dev.crash.node.KotlinNode
import dev.crash.networking.BlockChainServer
import dev.crash.storage.saveDBs
import dev.crash.webserver.WebServer
import kotlin.system.exitProcess

fun main(){
    mainStart()
}

fun mainStart(blockChainServerPort: Int = 8334, rpcServerPort: Int = 80, chainId: Int = 1){
    println("Starting KotlinChain Node...")
    CONFIG.setParams(chainId = chainId, blockchainServerPort = blockChainServerPort, rpcServerPort = rpcServerPort)
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