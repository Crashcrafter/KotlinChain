package dev.crash.node

import dev.crash.chain.Block
import dev.crash.chain.Transaction
import dev.crash.storage.BlockTrie

object Mempool {
    var currentBlock: Block? = null
    val onHold = mutableListOf<Transaction>()

    fun addTransaction(item: Transaction): Boolean {
        onHold.forEach {
            if(it.txid == item.txid) return false
        }
        onHold.add(item)
        return true
    }

    fun produceNewBlock(){
        val nextTx = mutableListOf<Transaction>()
        nextTx.addAll(onHold)
        currentBlock = Block(nextTx, getLastBlockNonce()+1)
        onHold.removeAll(nextTx)
    }

    fun calculateBlock(): Boolean {
        val result = currentBlock!!.validate()
        if(result) BlockTrie.addBlock(currentBlock!!)
        return result
    }

    fun isReadyForNewBlock(): Boolean = (currentBlock == null ||currentBlock!!.isConfirmed) && onHold.isNotEmpty()

    fun getLastBlockNonce(): Long = BlockTrie.lastBlockNonce
}