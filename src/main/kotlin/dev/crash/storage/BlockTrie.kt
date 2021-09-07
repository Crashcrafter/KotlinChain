package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Block
import dev.crash.toByteArray
import dev.crash.toByteBuffer
import org.lmdbjava.KeyRange

object BlockTrie {
    var lastBlockNumber: Long = -1
    var lastBlockHash: String = ""
    //Updates after every Block confirmation
    fun addBlock(block: Block): Boolean {
        lastBlockNumber = block.blockNonce
        lastBlockHash = block.blockHash
        val env = simpleEnv()
        val db = env.openDBI("block")
        val txn = env.txnRead()
        val key = block.blockNonce.toByteArray().toByteBuffer()
        val found = db.get(txn, key)
        txn.commit()
        if(found != null) {
            db.close()
            env.close()
            txn.close()
            return false
        }
        txn.close()
        db.put(key, block.blockBytes.toByteBuffer())
        db.close()
        env.close()
        return true
    }

    fun validateBlockNumbers(): Boolean {
        val env = simpleEnv()
        val db = env.openDBI("block")
        val txn = env.txnRead()
        val ci = db.iterate(txn, KeyRange.all())
        var lastValue: Long = -1
        var valid = true
        for (kv in ci) {
            val blockNumber = BytePacket(kv.key().array()).readLong()
            if(blockNumber != lastValue + 1) valid = false
            lastValue = blockNumber
        }
        ci.close()
        txn.close()
        db.close()
        env.close()
        return valid
    }

    fun lastBlockNumber(): Long {
        val env = simpleEnv()
        val db = env.openDBI("block")
        val txn = env.txnRead()
        val c = db.openCursor(txn)
        c.last()
        val result = BytePacket(c.key().array()).readLong()
        c.close()
        txn.commit()
        txn.close()
        db.close()
        env.close()
        return result
    }
}