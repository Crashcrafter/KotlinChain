package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Block
import dev.crash.crypto.sha224
import dev.crash.toByteArray
import dev.crash.toByteArrayMemory
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
        if(lastBlocks.size > 10) lastBlocks.pop()
        val db = getLevelDB("blocks")
        db.put(block.blockNonce.toByteArray().toByteArrayMemory(), block.blockBytes.toByteArrayMemory())
    }

    fun loadLastBlocks(){
        val db = getLevelDB("blocks")
        val cursor = db.newCursor()
        cursor.seekToLast()
        if(!cursor.isValid()) {
            cursor.close()
            return
        }
        lastBlockNonce = BytePacket(cursor.transientKey().getBytes()).readLong()
        lastBlockHash = cursor.transientValue().getBytes().sha224()
        for(i in 0..9) {
            if(!cursor.isValid()) break
            lastBlocks.push(Block(cursor.transientValue().getBytes()))
            cursor.prev()
        }
        lastBlocks.reverse()
        cursor.close()
    }

    fun getBlock(blockNumber: Long): Block? {
        val db = getLevelDB("blocks")
        val alloc = db.get(blockNumber.toByteArray().toByteArrayMemory()) ?: return null
        val bytes = alloc.getBytes()
        alloc.close()
        return Block(bytes)
    }
}