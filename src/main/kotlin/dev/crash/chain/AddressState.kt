package dev.crash.chain

import dev.crash.BytePacket

class AddressState(bytes: ByteArray, val address: String) {
    var nonce: Long
    var balance: Long
    val transactions = mutableListOf<AddressTransactionPreview>()

    init {
        val bytePacket = BytePacket(bytes)
        nonce = bytePacket.readVarLong()
        balance = bytePacket.readVarLong()
        val length = bytePacket.readVarInt()
        for (i in 0 until length){
            val txid = bytePacket.readString()
            val amount = bytePacket.readVarLong()
            val otherAddresses = bytePacket.readStrings()
            transactions.add(AddressTransactionPreview(txid, amount, otherAddresses))
        }
    }

    fun toByteArray(): ByteArray {
        val bytePacket = BytePacket()
        bytePacket.writeAsVarLong(nonce)
        bytePacket.writeAsVarLong(balance)
        bytePacket.writeAsVarInt(transactions.size)
        transactions.forEach {
            bytePacket.write(it.txid)
            bytePacket.writeAsVarLong(it.amount)
            bytePacket.write(it.otherAddresses)
        }
        return bytePacket.toByteArray()
    }

    companion object {
        fun getDefault(address: String): AddressState {
            val bytePacket = BytePacket()
            bytePacket.writeAsVarLong(0) // Nonce
            bytePacket.writeAsVarLong(0) // Balance
            bytePacket.writeAsVarInt(0) // Length of transactions
            return AddressState(bytePacket.toByteArray(), address)
        }
    }
}