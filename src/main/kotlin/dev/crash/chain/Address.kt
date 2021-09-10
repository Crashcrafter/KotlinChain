package dev.crash.chain

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.*
import dev.crash.node.Mempool
import dev.crash.storage.AddressState
import dev.crash.storage.AddressStateTrie
import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.nio.file.Path

class Address constructor(val publicKey: ByteArray, val address: String, val privateKey: BigInteger) {
    fun saveToFile(path: String) = saveToFile(File(path))

    fun saveToFile(path: Path) = saveToFile(path.toFile())

    fun saveToFile(file: File) {
        val bytePacket = BytePacket()
        bytePacket.write(publicKey)
        bytePacket.write(address)
        bytePacket.write(privateKey)
        file.writeBytes(bytePacket.toByteArray())
    }

    fun createTransaction(recipient: String, amount: Long, data: ByteArray = byteArrayOf()) = createTransaction(TransactionOutput(recipient, amount, data))

    fun createTransaction(output: TransactionOutput) = createTransaction(listOf(output))

    fun createTransaction(outputs: List<TransactionOutput>) {
        val bytePacket = BytePacket()
        val state = getState()
        bytePacket.writeAsVarLong(state.nonce)
        bytePacket.writeAsVarLong(21000)
        bytePacket.writeAsVarInt(outputs.size)
        outputs.forEach {
            bytePacket.write(it.recipient.removePrefix("0x"))
            bytePacket.writeAsVarLong(it.amount)
            bytePacket.write(it.data)
        }
        val rawTxBytes = bytePacket.toByteArray()
        val sig = rawTxBytes.sha256().sign(privateKey)
        bytePacket.writeAsVarInt(sig.v + 2*CONFIG.CHAINID)
        bytePacket.write(sig.r.toByteArray())
        bytePacket.write(sig.s.toByteArray())
        val tx = Transaction(bytePacket.toByteArray())
        if(!tx.validate(false)) {
            println("Invalid transaction")
            return
        }
        Mempool.addTransaction(tx)
    }

    fun getState(): AddressState = AddressStateTrie.getAddress(address)

    companion object {
        fun generate(): Address {
            return try {
                val keyPair = genECDSAKeyPair()
                val privateKey = keyPair.private.getPrivateKeyBigInt()
                val publicKey = keyPair.public.getUncompressedPublicKeyBytes()
                val address = "0x${publicKey.publicKeyToAddress().toHexString()}"
                Address(publicKey, address, privateKey)
            }catch (ex: IllegalArgumentException){
                generate()
            }
        }

        fun fromFile(path: String): Address = fromFile(File(path))

        fun fromFile(path: Path): Address = fromFile(path.toFile())

        fun fromFile(file: File): Address {
            val bytes = file.readBytes()
            val bytePacket = BytePacket(bytes)
            val publicKey = bytePacket.readByteArray()
            val address = bytePacket.readString()
            val privateKey = bytePacket.readBigInt()
            return Address(publicKey, address, privateKey)
        }
    }
}