package dev.crash.chain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.BytePacket
import dev.crash.crypto.sha224
import dev.crash.crypto.toHexString
import dev.crash.storage.BlockTrie
import dev.crash.toHexStringList

class Block private constructor() {

    constructor(transactions: List<Transaction>, blockNonce: Long) : this() {
        this.transactions = transactions
        val txids = mutableListOf<ByteArray>()
        transactions.forEach {
            txids.add(it.txid)
        }
        this.txids = txids
        this.blockNonce = blockNonce
    }

    //Constructor for DBBytes
    constructor(bytes: ByteArray, buildTxs: Boolean = false) : this() {
        blockBytes = bytes
        blockHash = blockBytes.sha224()
        val bytePacket = BytePacket(bytes)
        blockNonce = bytePacket.readVarLong() + 1
        previousBlockHash = bytePacket.readByteArray()
        txids = bytePacket.readByteArrays()
        if(buildTxs) {
            val transactions = mutableListOf<Transaction>()
            txids.forEach {
                transactions.add(Transaction.fromTxId(it.toHexString())!!)
            }
            this.transactions = transactions
        }
        confirmations = bytePacket.readVarLong()
        isConfirmed = confirmations > 0
    }

    var blockNonce: Long = -1
    var transactions: List<Transaction> = listOf()
    var txids: List<ByteArray> = listOf()
    var isConfirmed = false
    var confirmations: Long = 0
    var blockBytes = byteArrayOf()
    var blockHash = byteArrayOf()
    var previousBlockHash = byteArrayOf()

    fun validate(): Boolean {
        if(blockNonce < 0) return false
        val bytePacket = BytePacket()
        bytePacket.writeAsVarLong(BlockTrie.lastBlockNonce)
        bytePacket.write(BlockTrie.lastBlockHash)
        previousBlockHash = BlockTrie.lastBlockHash
        val txids = mutableListOf<ByteArray>()
        transactions.forEach {
            if(!it.validate(blockNonce)) return false
            txids.add(it.txid)
        }
        bytePacket.write(txids)
        confirmations++
        bytePacket.writeAsVarLong(confirmations)
        blockBytes = bytePacket.toByteArray()
        blockHash = blockBytes.sha224()
        println("Block $blockNonce (${blockHash.toHexString()}) confirmed")
        isConfirmed = true
        return true
    }

    data class JsonObj(
        val blockHash: String,
        val blockNonce: Long,
        val confirmations: Long,
        val txids: List<String>
    )

    fun getJsonObj(): JsonObj = JsonObj(blockHash.toHexString(), blockNonce, confirmations, txids.toHexStringList())

    override fun toString(): String {
        return jacksonObjectMapper().writeValueAsString(getJsonObj())
    }
}