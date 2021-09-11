package dev.crash.node

import dev.crash.chain.*
import dev.crash.storage.BlockTrie

object Mempool {
    var currentBlock: Block? = null
    val onHold = mutableListOf<Transaction>()
    val tempAddressState = hashMapOf<String, AddressState>()

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
        for(i in 0 until 100) {
            val address = Address.generate()
            address.createTransaction(TransactionOutput("23d4fa575b21ad1dd2db14547a26f24845a8f96843f493569875197d", 1000))
        }
    }

    fun calculateBlock(): Boolean {
        val result = currentBlock!!.validate()
        if(result) BlockTrie.addBlock(currentBlock!!)
        tempAddressState.clear()
        return result
    }

    fun isReadyForNewBlock(): Boolean = (currentBlock == null ||currentBlock!!.isConfirmed) && onHold.isNotEmpty()

    fun getLastBlockNonce(): Long = BlockTrie.lastBlockNonce
}