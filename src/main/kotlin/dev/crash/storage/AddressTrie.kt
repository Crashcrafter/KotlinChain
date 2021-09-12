package dev.crash.storage

import dev.crash.chain.AddressState
import dev.crash.crypto.asHexByteArray
import dev.crash.node.Mempool
import dev.crash.toMemory
import org.kodein.memory.io.getBytes

object AddressTrie : Trie("addresses") {
    fun saveAddressState(addressState: AddressState) {
        db.put(addressState.address.asHexByteArray().toMemory(), addressState.toByteArray().toMemory())
    }

    fun getAddress(address: String): AddressState {
        if(Mempool.tempAddressState.containsKey(address)) return Mempool.tempAddressState[address]!!
        val alloc = db.get(address.asHexByteArray().toMemory()) ?: return AddressState.getDefault(address)
        val result = AddressState(alloc.getBytes(), address)
        alloc.close()
        return result
    }
}