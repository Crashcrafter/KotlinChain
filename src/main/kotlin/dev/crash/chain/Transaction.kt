package dev.crash.chain

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.sha256
import dev.crash.crypto.toHexString
import dev.crash.exceptions.ECDSAValidationException

class Transaction(val bytes: ByteArray) {
    val txid = bytes.sha256().toHexString()
    val nonce: Long
    val gasPrice: Long
    val outputs: MutableList<TransactionOutput> = mutableListOf()
    val recid: Byte
    val r: ByteArray
    val s: ByteArray

    init {
        val bytePacket = BytePacket(bytes)
        nonce = bytePacket.readVarLong()
        gasPrice = bytePacket.readVarLong()
        val outputAmount = bytePacket.readVarInt()
        if(outputAmount == 0) throw Exception("No Output specified")
        for(i in 0 until outputAmount){
            val recipient = bytePacket.readString()
            val amount = bytePacket.readVarLong()
            val data = bytePacket.readByteArray()
            outputs.add(TransactionOutput(recipient, amount, data))
        }
        val v = bytePacket.readVarInt()
        val recIdCalc = v - 2*CONFIG.CHAINID
        if(recIdCalc !in 0..1) throw ECDSAValidationException("v is invalid")
        recid = recIdCalc.toByte()
        r = bytePacket.readByteArray()
        s = bytePacket.readByteArray()
        if(!validate()) throw ECDSAValidationException("Invalid signature")
    }

    fun validate(): Boolean {
        //TODO: Validate transaction
        return true
    }
}