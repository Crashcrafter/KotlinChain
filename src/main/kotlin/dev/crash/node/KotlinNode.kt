package dev.crash.node

import dev.crash.wallet.Wallet
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.delay
import java.io.File

object KotlinNode {
    lateinit var nodeWallet: Wallet

    suspend fun start(){
        println("Starting node with wallet ${nodeWallet.address}")
        while (true) {
            delay(1000)
        }
    }

    fun loadWallet(){
        val addressFile = File("node.wallet")
        if(addressFile.exists()){
            nodeWallet = Wallet.fromFile(addressFile)
        }else {
            val wallet = Wallet.generate()
            nodeWallet = wallet
            if(addressFile.createNewFile()){
                wallet.saveToFile(addressFile)
            }else {
                throw IOException("File ${addressFile.name} could not be created! Stopping node...")
            }
        }
    }
}