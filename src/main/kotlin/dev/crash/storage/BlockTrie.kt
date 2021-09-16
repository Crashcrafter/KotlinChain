package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.Block
import dev.crash.crypto.sha224
import dev.crash.exceptions.ChainStorageException
import dev.crash.toByteArrayAsVarLong
import dev.crash.toMemory
import org.kodein.memory.io.getBytes
import kotlin.collections.ArrayDeque

object BlockTrie : Trie("blocks") {
    var lastBlockNonce: Long = -1
    var lastBlockHash: ByteArray = byteArrayOf()
    val lastBlocks = ArrayDeque<Block>()

    fun addBlock(block: Block) {
        lastBlockNonce = block.blockNonce
        lastBlockHash = block.blockHash
        lastBlocks.addFirst(block)
        if(lastBlocks.size > 25) lastBlocks.removeLast()
        db.put(block.blockNonce.toByteArrayAsVarLong().toMemory(), block.blockBytes.toMemory())
        TransactionTrie.addTransactions(block.transactions)
    }

    fun loadLastBlocks(){
        val cursor = db.newCursor()
        cursor.seekToLast()
        if(!cursor.isValid()) {
            cursor.close()
            return
        }
        lastBlockNonce = BytePacket(cursor.transientKey().getBytes()).readVarLong()
        lastBlockHash = cursor.transientValue().getBytes().sha224()
        for(i in 0 until 25) {
            if(!cursor.isValid()) break
            lastBlocks.addLast(Block(cursor.transientValue().getBytes()))
            cursor.prev()
        }
        cursor.close()
    }

    fun getBlock(blockNumber: Long): Block? {
        val alloc = db.get(blockNumber.toByteArrayAsVarLong().toMemory()) ?: return null
        val bytes = alloc.getBytes()
        alloc.close()
        return Block(bytes)
    }

    fun verifyBlocks() {
        println("Verify blocks...")
        val cursor = db.newCursor()
        cursor.seekToLast()
        var lastBlockHash = this.lastBlockHash
        var lastNonce = this.lastBlockNonce
        while (cursor.isValid()){
            val bytes = cursor.transientValue().getBytes()
            val packet = BytePacket(bytes)
            val nonce = packet.readVarLong() //Last nonce
            if(!bytes.sha224().contentEquals(lastBlockHash) && nonce != lastNonce-1){
                throw ChainStorageException("Invalid block $nonce!")
            }
            lastNonce = nonce
            lastBlockHash = packet.readByteArray()
            cursor.seekTo(nonce.toByteArrayAsVarLong().toMemory()) //Get Previous block
        }
        cursor.close()
        println("Blocks verified!")
    }
}