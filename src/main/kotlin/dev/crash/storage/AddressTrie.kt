package dev.crash.storage

import dev.crash.BytePacket
import dev.crash.chain.AddressState
import dev.crash.crypto.asHexByteArray
import dev.crash.crypto.toHexString
import dev.crash.exceptions.ChainStorageException
import dev.crash.node.Mempool
import dev.crash.toMemory
import org.kodein.memory.io.getBytes

object AddressTrie : Trie("addresses") {
    fun saveAddressStates(addressStates: List<AddressState>) {
        val batch = db.newWriteBatch()
        addressStates.forEach {
            batch.put(it.address.toMemory(), it.toByteArray().toMemory())
        }
        db.write(batch)
        batch.close()
    }

    fun getAddress(address: String): AddressState = getAddress(address.asHexByteArray())

    fun getAddress(address: ByteArray): AddressState {
        if(Mempool.tempAddressState.containsKey(address)) return Mempool.tempAddressState[address]!!
        val alloc = db.get(address.toMemory()) ?: return AddressState.getDefault(address)
        val result = AddressState(alloc.getBytes(), address)
        alloc.close()
        return result
    }

    fun verifyAddressStates() {
        println("Verify address states...\n(This may take up to 10 minutes!)")
        val cursor = db.newCursor()
        cursor.seekToFirst()
        while (cursor.isValid()){
            val address = cursor.transientKey().getBytes()
            val packet = BytePacket(cursor.transientValue().getBytes())
            packet.readVarLong() // Nonce
            val balance = packet.readVarLong()
            packet.readByteArray() // Data
            val txidsSize = packet.readVarInt()
            var countedBalance = 0L
            for (i in 0 until txidsSize){
                val txid = packet.readByteArray()
                val tx = TransactionTrie.getTransaction(txid) ?: throw ChainStorageException("Transaction ${txid.toHexString()} does not exist but is required by address ${address.toHexString()}")
                if(tx.from.contentEquals(address)){
                    countedBalance -= tx.getTotalOut()
                }else {
                    tx.getOutputOfAddress(address).forEach {
                        countedBalance += it.amount
                    }
                }
            }
            println(address.toHexString())
            if(countedBalance != balance){
                throw ChainStorageException("Balance of ${address.toHexString()} does not match with transactions!")
            }
            cursor.next()
        }
        cursor.close()
        println("Address states verified!")
    }
}