package dev.crash

import dev.crash.node.KotlinNode
import dev.crash.networking.BlockChainServer
import dev.crash.webserver.RPCServer

suspend fun main(args: Array<String>){
    mainStart()
}

suspend fun mainStart(blockChainServerPort: Int = 8334, rpcServerPort: Int = 80, chainId: Int = 1){
    println("Starting KotlinChain Node...")
    CONFIG.CHAINID = chainId
    KotlinNode.loadWallet()
    BlockChainServer.start(blockChainServerPort)
    RPCServer.start(rpcServerPort)
    KotlinNode.start()
}