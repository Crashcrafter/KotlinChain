package dev.crash.node

import dev.crash.chain.Block
import dev.crash.chain.Transaction

object Mempool {
    var currentBlock = Block(listOf(), -1)
    val onHold = mutableListOf<Transaction>()

    fun addTransaction(item: Transaction): Boolean {
        onHold.forEach {
            if(it.txid == item.txid) return false
        }
        onHold.add(item)
        return true
    }

    fun produceNewBlock(){
        println("Produce new block...")
        val nextTx = mutableListOf<Transaction>()
        nextTx.addAll(onHold)
        currentBlock = Block(nextTx, getNextBlockNonce())
        onHold.removeAll(nextTx)
        println("Block number ${currentBlock.blockNonce} created!")
    }

    fun calculateBlock(): Boolean = currentBlock.validate()

    fun isReadyForNewBlock(): Boolean = (currentBlock.isConfirmed || currentBlock.blockNonce < 0) && onHold.isNotEmpty()

    fun getNextBlockNonce(): Long {
        //TODO: Get next block nonce
        return 0
    }
}