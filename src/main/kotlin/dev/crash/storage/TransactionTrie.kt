package dev.crash.storage

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.toMemory
import org.kodein.memory.io.getBytes
import kotlin.collections.ArrayDeque

object TransactionTrie : Trie("transactions") {
    val lastTxs = ArrayDeque<Transaction>()

    fun addTransaction(transaction: Transaction) {
        lastTxs.addFirst(transaction)
        if(lastTxs.size > 100) lastTxs.removeLast()
        db.put(transaction.txid.toMemory(), transaction.getDBBytes().toMemory())
    }

    fun addTransactions(transactions: List<Transaction>) {
        val batch = db.newWriteBatch()
        transactions.forEach {
            lastTxs.addFirst(it)
            if(lastTxs.size > 100) lastTxs.removeLast()
            batch.put(it.txid.toMemory(), it.getDBBytes().toMemory())
        }
        db.write(batch)
        batch.close()
    }

    fun getTransaction(txHash: ByteArray): Transaction? {
        val alloc = db.get(txHash.toMemory()) ?: return null
        val result = Transaction.fromDBBytes(alloc.getBytes())
        alloc.close()
        return result
    }

    fun getTransaction(txHash: String): Transaction? = getTransaction(txHash.asHexByteArray())
}