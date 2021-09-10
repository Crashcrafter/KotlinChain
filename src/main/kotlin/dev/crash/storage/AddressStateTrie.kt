package dev.crash.storage

import dev.crash.crypto.asHexByteArray
import dev.crash.toByteArrayMemory
import org.kodein.memory.io.getBytes

object AddressStateTrie {
    fun saveAddressState(addressState: AddressState) {
        val db = getLevelDB("addresses")
        db.put(addressState.address.asHexByteArray().toByteArrayMemory(), addressState.toByteArray().toByteArrayMemory())
    }

    fun getAddress(address: String): AddressState {
        val db = getLevelDB("addresses")
        val alloc = db.get(address.asHexByteArray().toByteArrayMemory()) ?: return AddressState.getDefault(address)
        val result = AddressState(alloc.getBytes(), address)
        alloc.close()
        return result
    }
}