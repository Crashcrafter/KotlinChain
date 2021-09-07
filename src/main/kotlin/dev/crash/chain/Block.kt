package dev.crash.chain

import dev.crash.BytePacket
import dev.crash.crypto.asHexByteArray
import dev.crash.crypto.sha224
import dev.crash.crypto.toHexString
import dev.crash.storage.BlockTrie

class Block(val transactions: List<Transaction>, val blockNonce: Long) {
    var isConfirmed = false
    var blockBytes = byteArrayOf()
    var blockHash = ""

    fun validate(): Boolean {
        if(blockNonce < 0) return false
        val bytePacket = BytePacket()
        bytePacket.write(BlockTrie.lastBlockHash.asHexByteArray())
        bytePacket.write(transactions.size)
        transactions.forEach {
            if(!it.validate()) return false
            bytePacket.write(it.bytes)
        }
        blockBytes = bytePacket.toByteArray()
        blockHash = blockBytes.sha224().toHexString()
        println("Block $blockNonce ($blockHash) confirmed")
        isConfirmed = true
        return true
    }
}