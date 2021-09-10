package dev.crash.storage

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.toByteArrayMemory
import org.kodein.memory.io.getBytes
import java.util.*

object TransactionTrie {
    val lastTxs = Stack<Transaction>()

    fun addTransaction(transaction: Transaction) {
        lastTxs.push(transaction)
        if(lastTxs.size > 10) lastTxs.pop()
        val db = getLevelDB("transactions")
        db.put(transaction.txid.asHexByteArray().toByteArrayMemory(), transaction.bytes.toByteArrayMemory())
    }

    fun getTransaction(txHash: String): Transaction? {
        val db = getLevelDB("transactions")
        val alloc = db.get(txHash.asHexByteArray().toByteArrayMemory()) ?: return null
        val result = Transaction.fromDBBytes(alloc.getBytes())
        alloc.close()
        return result
    }
}