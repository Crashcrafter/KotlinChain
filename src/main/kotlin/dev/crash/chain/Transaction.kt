package dev.crash.chain

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.*
import dev.crash.exceptions.ECDSAValidationException
import dev.crash.exceptions.InvalidAddressException
import dev.crash.exceptions.NonceException
import dev.crash.node.Mempool
import dev.crash.storage.AddressStateTrie
import dev.crash.storage.TransactionTrie
import java.math.BigInteger

class Transaction private constructor() {
    var bytes: ByteArray = byteArrayOf()
    var txid: String = ""
    var nonce: Long = -1
    var gasPrice: Long = -1
    val outputs: MutableList<TransactionOutput> = mutableListOf()
    var recid: Byte = -1
    var r: BigInteger = BigInteger.ZERO
    var s: BigInteger = BigInteger.ZERO
    var confirmations: Int = 0

    constructor(bytes: ByteArray, confirm: Boolean = true) : this() {
        this.bytes = bytes
        this.txid = bytes.sha256().toHexString()
        val bytePacket = BytePacket(bytes)
        this.nonce = bytePacket.readVarLong()
        gasPrice = bytePacket.readVarLong()
        val outputAmount = bytePacket.readVarInt()
        if(outputAmount == 0) throw Exception("No Output specified")
        for(i in 0 until outputAmount){
            val recipient = bytePacket.readString()
            if(!validateAddress(recipient)) throw InvalidAddressException()
            val amount = bytePacket.readVarLong()
            val data = bytePacket.readByteArray()
            outputs.add(TransactionOutput(recipient, amount, data))
        }
        val v = bytePacket.readVarInt()
        val recIdCalc = v - 2*CONFIG.CHAINID
        if(recIdCalc !in 0..1) throw ECDSAValidationException("v is invalid")
        recid = recIdCalc.toByte()
        r = BigInteger(bytePacket.readByteArray())
        s = BigInteger(bytePacket.readByteArray())
        if(confirm) validate(false)
    }

    fun validate(count: Boolean = true): Boolean {
        if(r == BigInteger.ZERO || s == BigInteger.ZERO || recid == (-1).toByte()) throw IllegalStateException("Transaction not initialized!")
        val messageHashBuilder = BytePacket()
        messageHashBuilder.writeAsVarLong(nonce)
        messageHashBuilder.writeAsVarLong(gasPrice)
        messageHashBuilder.writeAsVarInt(outputs.size)
        var totalOut = 0L
        val recipients = mutableListOf<String>()
        outputs.forEach {
            recipients.add(it.recipient)
            messageHashBuilder.write(it.recipient)
            messageHashBuilder.writeAsVarLong(it.amount)
            totalOut += it.amount
            messageHashBuilder.write(it.data)
        }
        val messageHash = messageHashBuilder.toByteArray().sha256()
        val recoveredKey = recoverPublicKeyFromSignature(recid, r, s, messageHash) ?: throw ECDSAValidationException("Invalid signature, cant recover public key")
        val address = recoveredKey.publicKeyToAddress().toHexString()
        val state = AddressStateTrie.getAddress(address)
        if(state.nonce != nonce) throw NonceException()
        //if(state.balance < totalOut + gasPrice*bytes.size) throw InsufficientBalanceException()
        if(!verifySignature(messageHash, recoveredKey, r, s)) throw ECDSAValidationException("Invalid signature for address 0x$address")
        if(count) {
            state.nonce++
            state.balance -= totalOut
            state.transactions.add(AddressTransactionPreview(txid, totalOut, recipients))
            Mempool.tempAddressState[address] = state
            confirmations++
            //TODO: Update Transaction State
        }
        return true
    }

    fun getDBBytes(): ByteArray {
        val bytePacket = BytePacket()
        bytePacket.write(bytes)
        bytePacket.writeAsVarInt(confirmations)
        return bytePacket.toByteArray()
    }

    companion object {
        fun fromDBBytes(bytes: ByteArray): Transaction {
            val bytePacket = BytePacket(bytes)
            val transaction = Transaction(bytePacket.readByteArray())
            transaction.confirmations = bytePacket.readVarInt()
            return transaction
        }

        fun fromTxHash(txHash: String): Transaction? = TransactionTrie.getTransaction(txHash)
    }
}