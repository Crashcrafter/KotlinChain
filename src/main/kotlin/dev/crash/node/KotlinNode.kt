package dev.crash.node

import dev.crash.chain.Address
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.delay
import java.io.File

object KotlinNode {
    lateinit var nodeAddress: Address

    suspend fun start(){
        println("Starting node with wallet ${nodeAddress.address}")
        while (true) {
            delay(10000)
            if(Mempool.isReadyForNewBlock()){
                Mempool.produceNewBlock()
                if(!Mempool.calculateBlock()) {
                    println("Invalid block found!")
                }
            }
        }
    }

    fun loadNodeWallet(){
        val addressFile = File("node.wallet")
        if(addressFile.exists()){
            nodeAddress = Address.fromFile(addressFile)
        }else {
            val address = Address.generate()
            nodeAddress = address
            if(addressFile.createNewFile()){
                address.saveToFile(addressFile)
            }else {
                throw IOException("File ${addressFile.name} could not be created! Stopping node...")
            }
        }
    }
}