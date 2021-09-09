package dev.crash.storage

import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.toByteArrayMemory
import org.kodein.memory.io.getBytes
import java.util.*

object TransactionTrie {
    //Updates after every Block confirmation
    //Also updates with each transaction received -> unconfirmed
    val lastTxs = Stack<Transaction>()

    fun addTransaction(transaction: Transaction): Boolean {
        lastTxs.push(transaction)
        if(lastTxs.size > 10) lastTxs.pop()
        val db = getLevelDB("transactions")
        db.put(transaction.txid.asHexByteArray().toByteArrayMemory(), transaction.bytes.toByteArrayMemory())
        return true
    }

    fun getTransaction(txHash: String): Transaction? {
        val db = getLevelDB("transactions")
        val cursor = db.newCursor()
        cursor.seekTo(txHash.asHexByteArray().toByteArrayMemory())
        if(!cursor.isValid()) return null
        val result = Transaction.fromDBBytes(cursor.transientValue().getBytes())
        cursor.close()
        return result
    }
}