package dev.crash.node

import dev.crash.chain.*
import dev.crash.storage.AddressTrie
import dev.crash.storage.BlockTrie
import kotlin.random.Random
import kotlin.random.nextLong

object Mempool {
    var currentBlock: Block? = null
    val onHold = mutableListOf<Transaction>()
    val tempAddressState = hashMapOf<ByteArray, AddressState>()

    fun addTransaction(item: Transaction): Boolean {
        onHold.forEach {
            if(it.txid.contentEquals(item.txid)) return false
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
            address.createTransaction(TransactionOutput("ea1cd0569aa4dcd90f0103219420317d051e55663f488df3", Random.nextLong(0..Long.MAX_VALUE)))
        }
    }

    fun calculateBlock(): Boolean {
        val result = currentBlock!!.validate()
        if(result) BlockTrie.addBlock(currentBlock!!)
        val tempAddressStatesToSave = mutableListOf<AddressState>()
        tempAddressStatesToSave.addAll(tempAddressState.values)
        tempAddressStatesToSave.forEach {
            tempAddressState.remove(it.address)
        }
        AddressTrie.saveAddressStates(tempAddressStatesToSave)
        return result
    }

    fun isReadyForNewBlock(): Boolean = (currentBlock == null ||currentBlock!!.isConfirmed) && onHold.isNotEmpty()

    fun getLastBlockNonce(): Long = BlockTrie.lastBlockNonce
}