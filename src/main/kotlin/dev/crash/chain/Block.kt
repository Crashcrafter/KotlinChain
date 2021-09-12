package dev.crash.chain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.BytePacket
import dev.crash.crypto.sha224
import dev.crash.crypto.toHexString
import dev.crash.storage.BlockTrie

class Block private constructor() {

    constructor(transactions: List<Transaction>, blockNonce: Long) : this() {
        this.transactions = transactions
        val txids = mutableListOf<String>()
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
        bytePacket.readByteArray()
        val amountTx = bytePacket.readVarInt()
        this.txids = bytePacket.readStrings()
        if(buildTxs) {
            val transactions = mutableListOf<Transaction>()
            for (i in 0 until amountTx) {
                val value = bytePacket.readString()
                transactions.add(Transaction.fromTxId(value)!!)
            }
            this.transactions = transactions
        }
        confirmations = bytePacket.readVarLong()
        isConfirmed = confirmations > 0
    }

    var blockNonce: Long = -1
    var transactions: List<Transaction> = listOf()
    var txids: List<String> = listOf()
    var isConfirmed = false
    var confirmations: Long = 0
    var blockBytes = byteArrayOf()
    var blockHash = byteArrayOf()

    fun validate(): Boolean {
        if(blockNonce < 0) return false
        val bytePacket = BytePacket()
        bytePacket.writeAsVarLong(BlockTrie.lastBlockNonce)
        bytePacket.write(BlockTrie.lastBlockHash)
        val txids = mutableListOf<String>()
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

    fun getJsonObj(): JsonObj = JsonObj(blockHash.toHexString(), blockNonce, confirmations, txids)

    override fun toString(): String {
        return jacksonObjectMapper().writeValueAsString(getJsonObj())
    }
}