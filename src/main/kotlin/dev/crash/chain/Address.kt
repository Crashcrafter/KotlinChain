package dev.crash.chain

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.*
import dev.crash.node.Mempool
import java.io.File
import java.math.BigInteger

class Address constructor(val publicKey: ByteArray, val address: String, val privateKey: BigInteger) {
    var nonce: Long = 0

    fun saveToFile(path: String) = saveToFile(File(path))

    fun saveToFile(file: File) {
        val bytePacket = BytePacket()
        bytePacket.write(publicKey)
        bytePacket.write(address)
        bytePacket.write(privateKey)
        file.writeBytes(bytePacket.toByteArray())
    }

    fun createTransaction(outputs: List<TransactionOutput>) {
        val bytePacket = BytePacket()
        bytePacket.writeAsVarLong(nonce)
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

    companion object {
        fun generate(): Address {
            val keyPair = genECDSAKeyPair()
            val privateKey = keyPair.private.getPrivateKeyBigInt()
            val publicKey = keyPair.public.getUncompressedPublicKeyBytes()
            val address = "0x${publicKey.publicKeyToAddress().toHexString()}"
            return Address(publicKey, address, privateKey)
        }

        fun fromFile(path: String): Address = fromFile(File(path))

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