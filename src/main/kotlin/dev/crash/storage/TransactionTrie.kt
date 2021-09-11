package dev.crash.storage

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.toMemory
import org.kodein.memory.io.getBytes
import java.util.*

object TransactionTrie {
    val lastTxs = Stack<Transaction>()

    fun addTransaction(transaction: Transaction) {
        lastTxs.push(transaction)
        if(lastTxs.size > 100) lastTxs.pop()
        val db = getLevelDB("transactions")
        db.put(transaction.txid.asHexByteArray().toMemory(), transaction.bytes.toMemory())
    }

    fun addTransactions(transactions: List<Transaction>) {
        val db = getLevelDB("transactions")
        val batch = db.newWriteBatch()
        transactions.forEach {
            batch.put(it.txid.asHexByteArray().toMemory(), it.bytes.toMemory())
        }
        db.write(batch)
        batch.close()
    }

    fun getTransaction(txHash: String): Transaction? {
        val db = getLevelDB("transactions")
        val alloc = db.get(txHash.asHexByteArray().toMemory()) ?: return null
        val result = Transaction.fromDBBytes(alloc.getBytes())
        alloc.close()
        return result
    }
}