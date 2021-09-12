package dev.crash.chain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.*
import dev.crash.exceptions.ECDSAValidationException
import dev.crash.exceptions.InvalidAddressException
import dev.crash.exceptions.NonceException
import dev.crash.node.Mempool
import dev.crash.storage.AddressTrie
import dev.crash.storage.TransactionTrie
import java.math.BigInteger

class Transaction private constructor() {
    var bytes: ByteArray = byteArrayOf()
    var txid: String = ""
    var nonce: Long = -1
    var gasPrice: Long = -1
    var from: String = ""
    val outputs: MutableList<TransactionOutput> = mutableListOf()
    var recid: Byte = -1
    var r: BigInteger = BigInteger.ZERO
    var s: BigInteger = BigInteger.ZERO
    var confirmations: Int = 0
    var blockNonce: Long = -1

    constructor(bytes: ByteArray, confirm: Boolean = true) : this() {
        this.bytes = bytes
        this.txid = bytes.sha256().toHexString()
        val bytePacket = BytePacket(bytes)
        this.nonce = bytePacket.readVarLong()
        gasPrice = bytePacket.readVarLong()
        val outputAmount = bytePacket.readVarInt()
        if(confirm && outputAmount == 0) throw Exception("No Output specified")
        for(i in 0 until outputAmount){
            val recipient = bytePacket.readString()
            if(confirm && !validateAddress(recipient)) throw InvalidAddressException()
            val amount = bytePacket.readVarLong()
            val data = bytePacket.readByteArray()
            outputs.add(TransactionOutput(recipient, amount, data))
        }
        val v = bytePacket.readVarInt()
        val recIdCalc = v - 2*CONFIG.CHAINID
        if(confirm && recIdCalc !in 0..1) throw ECDSAValidationException("v is invalid")
        recid = recIdCalc.toByte()
        r = BigInteger(bytePacket.readByteArray())
        s = BigInteger(bytePacket.readByteArray())
        if(confirm) validateSendTx()
    }

    fun buildRawTxBytes(): ByteArray {
        val messageHashBuilder = BytePacket()
        messageHashBuilder.writeAsVarLong(nonce)
        messageHashBuilder.writeAsVarLong(gasPrice)
        messageHashBuilder.writeAsVarInt(outputs.size)
        outputs.forEach {
            messageHashBuilder.write(it.recipient)
            messageHashBuilder.writeAsVarLong(it.amount)
            messageHashBuilder.write(it.data)
        }
        return messageHashBuilder.toByteArray()
    }

    fun validateSendTx() {
        if(r == BigInteger.ZERO || s == BigInteger.ZERO || recid == (-1).toByte()) throw IllegalStateException("Transaction not initialized!")
        val messageHash = buildRawTxBytes().sha256()
        val recoveredKey = recoverPublicKeyFromSignature(recid, r, s, messageHash) ?: throw ECDSAValidationException("Invalid signature, cant recover public key")
        from = recoveredKey.publicKeyToAddress().toHexString()
        val state = AddressTrie.getAddress(from)
        if(state.nonce != nonce) throw NonceException()
        //if(state.balance < totalOut + gasPrice*bytes.size) throw InsufficientBalanceException()
        if(!verifySignature(messageHash, recoveredKey, r, s)) throw ECDSAValidationException("Invalid signature for address 0x$from")
    }

    fun validate(blockNonce: Long): Boolean {
        if(r == BigInteger.ZERO || s == BigInteger.ZERO || recid == (-1).toByte()) return false
        val messageHash = buildRawTxBytes().sha256()
        val recoveredKey = recoverPublicKeyFromSignature(recid, r, s, messageHash) ?: return false
        from = recoveredKey.publicKeyToAddress().toHexString()
        val state = AddressTrie.getAddress(from)
        if(state.nonce != nonce) return false
        //if(state.balance < totalOut + gasPrice*bytes.size) return false
        if(!verifySignature(messageHash, recoveredKey, r, s)) return false
        this.blockNonce = blockNonce
        var totalOut = 0L
        val recipients = mutableListOf<String>()
        outputs.forEach {
            totalOut += it.amount
            recipients.add(it.recipient)
        }
        state.nonce++
        state.balance -= totalOut
        state.transactions.add(AddressTransactionPreview(txid, totalOut, recipients))
        Mempool.tempAddressState[from] = state
        confirmations++
        return true
    }

    fun getDBBytes(): ByteArray {
        val bytePacket = BytePacket()
        bytePacket.write(bytes)
        bytePacket.writeAsVarLong(blockNonce)
        bytePacket.writeAsVarInt(confirmations)
        return bytePacket.toByteArray()
    }

    data class JsonObj(
        val blockNonce: Long,
        val txid: String,
        val nonce: Long,
        val from: String,
        val to: List<TransactionOutput>,
        val gasPrice: Long,
        val confirmations: Int
    )

    fun getJsonObj(): JsonObj = JsonObj(blockNonce, txid, nonce, from, outputs, gasPrice, confirmations)

    override fun toString(): String {
        return jacksonObjectMapper().writeValueAsString(getJsonObj())
    }

    companion object {
        fun fromDBBytes(bytes: ByteArray): Transaction {
            val bytePacket = BytePacket(bytes)
            val transaction = Transaction(bytePacket.readByteArray(), false)
            transaction.blockNonce = bytePacket.readVarLong()
            transaction.confirmations = bytePacket.readVarInt()
            return transaction
        }

        fun fromTxId(txHash: String): Transaction? = TransactionTrie.getTransaction(txHash)
    }
}