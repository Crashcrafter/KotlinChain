package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Block
import dev.crash.crypto.sha224
import dev.crash.toByteArrayAsVarLong
import dev.crash.toMemory
import org.kodein.memory.io.getBytes
import java.util.*

object BlockTrie {
    var lastBlockNonce: Long = -1
    var lastBlockHash: ByteArray = byteArrayOf()
    val lastBlocks = Stack<Block>()

    fun addBlock(block: Block) {
        lastBlockNonce = block.blockNonce
        lastBlockHash = block.blockHash
        lastBlocks.push(block)
        if(lastBlocks.size > 100) lastBlocks.pop()
        val db = getLevelDB("blocks")
        db.put(block.blockNonce.toByteArrayAsVarLong().toMemory(), block.blockBytes.toMemory())
        TransactionTrie.addTransactions(block.transactions)
    }

    fun loadLastBlocks(){
        val db = getLevelDB("blocks")
        val cursor = db.newCursor()
        cursor.seekToLast()
        if(!cursor.isValid()) {
            cursor.close()
            return
        }
        lastBlockNonce = BytePacket(cursor.transientKey().getBytes()).readVarLong()
        lastBlockHash = cursor.transientValue().getBytes().sha224()
        for(i in 0..99) {
            if(!cursor.isValid()) break
            lastBlocks.push(Block(cursor.transientValue().getBytes()))
            cursor.prev()
        }
        lastBlocks.reverse()
        cursor.close()
    }

    fun getBlock(blockNumber: Long): Block? {
        val db = getLevelDB("blocks")
        val alloc = db.get(blockNumber.toByteArrayAsVarLong().toMemory()) ?: return null
        val bytes = alloc.getBytes()
        alloc.close()
        return Block(bytes)
    }
}