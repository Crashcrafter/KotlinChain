package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Transaction
import dev.crash.crypto.asHexByteArray
import dev.crash.crypto.sha256
import dev.crash.crypto.toHexString
import dev.crash.exceptions.ChainStorageException
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

    fun verifyTransactions() {
        println("Verify transactions...")
        val cursor = db.newCursor()
        cursor.seekToFirst()
        while (cursor.isValid()){
            val txid = cursor.transientKey().getBytes()
            val txBytes = BytePacket(cursor.transientValue().getBytes()).readByteArray()
            if(!txid.contentEquals(txBytes.sha256())){
                throw ChainStorageException("Invalid transaction ${txid.toHexString()}")
            }
            cursor.next()
        }
        cursor.close()
        println("Transactions verified!")
    }
}