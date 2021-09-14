package dev.crash.node

import dev.crash.chain.Address
import dev.crash.storage.BlockTrie
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object KotlinNode {
    var nodeAddress: Address

    fun start(){
        println("Starting node with wallet ${nodeAddress.address}")
        BlockTrie.loadLastBlocks()
        GlobalScope.launch {
            while (true) {
                if(Mempool.isReadyForNewBlock()){
                    Mempool.produceNewBlock()
                    if(!Mempool.calculateBlock()) {
                        println("Invalid block found!")
                    }
                }
            }
        }
    }

    init {
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