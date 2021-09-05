package dev.crash

import dev.crash.node.KotlinNode
import dev.crash.networking.BlockChainServer
import dev.crash.rpc.RPCServer

suspend fun main(args: Array<String>){
    mainStart()
}

suspend fun mainStart(blockChainServerPort: Int = 8334, rpcServerPort: Int = 80){
    println("Starting KotlinChain Node...")
    KotlinNode.loadWallet()
    BlockChainServer.start(blockChainServerPort)
    RPCServer.start(rpcServerPort)
    KotlinNode.start()
}