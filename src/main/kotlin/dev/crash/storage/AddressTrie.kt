package dev.crash.storage

import dev.crash.chain.AddressState
import dev.crash.crypto.asHexByteArray
import dev.crash.node.Mempool
import dev.crash.toMemory
import org.kodein.memory.io.getBytes

object AddressTrie : Trie("addresses") {
    fun saveAddressState(addressState: AddressState) {
        db.put(addressState.address.toMemory(), addressState.toByteArray().toMemory())
    }

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
}