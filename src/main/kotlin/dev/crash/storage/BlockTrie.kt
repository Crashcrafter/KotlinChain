package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Block
import dev.crash.toByteArray

object BlockTrie {
    var lastBlockNumber: Long = getLatestBlockNumber()
    var lastBlockHash: String = ""

    //Updates after every Block confirmation

    fun addBlock(block: Block): Boolean {
        lastBlockNumber = block.blockNonce
        lastBlockHash = block.blockHash
        val db = getLevelDB("blocks")
        db.put(block.blockNonce.toByteArray(), block.blockBytes)
        db.close()
        return true
    }

    fun getLatestBlockNumber(): Long {
        val db = getLevelDB("blocks")
        val iterator = db.iterator()
        try {
            iterator.seekToLast()
        }catch (ex: UnsupportedOperationException){
            return -1
        }
        val result = BytePacket(iterator.peekNext().key).readLong()
        iterator.close()
        db.close()
        return result
    }
}