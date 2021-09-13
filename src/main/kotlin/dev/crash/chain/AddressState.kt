package dev.crash.chain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.crash.BytePacket
import dev.crash.crypto.toHexString
import dev.crash.toHexStringList

class AddressState(bytes: ByteArray, val address: ByteArray) {
    var nonce: Long
    var balance: Long
    var data: ByteArray
    val txids = mutableListOf<ByteArray>()

    init {
        val bytePacket = BytePacket(bytes)
        nonce = bytePacket.readVarLong()
        balance = bytePacket.readVarLong()
        data = bytePacket.readByteArray()
        txids.addAll(bytePacket.readByteArrays())
    }

    fun toByteArray(): ByteArray {
        val bytePacket = BytePacket()
        bytePacket.writeAsVarLong(nonce)
        bytePacket.writeAsVarLong(balance)
        bytePacket.write(data)
        bytePacket.write(txids)
        return bytePacket.toByteArray()
    }

    data class JsonObj(
        val address: String,
        val balance: Long,
        val nonce: Long,
        val transactions: List<String>
    )

    override fun toString(): String {
        return jacksonObjectMapper().writeValueAsString(JsonObj(address.toHexString(), balance, nonce, txids.toHexStringList()))
    }

    companion object {
        fun getDefault(address: ByteArray): AddressState {
            val bytePacket = BytePacket()
            bytePacket.writeAsVarLong(0) // Nonce
            bytePacket.writeAsVarLong(0) // Balance
            bytePacket.writeAsVarInt(0) // Data
            bytePacket.writeAsVarInt(0) // Length of transactions
            return AddressState(bytePacket.toByteArray(), address)
        }
    }
}