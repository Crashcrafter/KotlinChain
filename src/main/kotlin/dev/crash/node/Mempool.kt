package dev.crash.node

import dev.crash.chain.Address
import dev.crash.chain.Block
import dev.crash.chain.Transaction
import dev.crash.chain.TransactionOutput
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
        for(i in 0..100) {
            val address = Address.generate()
            address.createTransaction(TransactionOutput("0x23d4fa575b21ad1dd2db14547a26f24845a8f96843f493569875197d", 1000))
        }
    }

    fun calculateBlock(): Boolean {
        val result = currentBlock!!.validate()
        if(result) BlockTrie.addBlock(currentBlock!!)
        return result
    }

    fun isReadyForNewBlock(): Boolean = (currentBlock == null ||currentBlock!!.isConfirmed) && onHold.isNotEmpty()

    fun getLastBlockNonce(): Long = BlockTrie.lastBlockNonce
}