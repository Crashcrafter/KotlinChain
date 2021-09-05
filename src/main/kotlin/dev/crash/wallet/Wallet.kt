package dev.crash.wallet

import dev.crash.BytePacket
import dev.crash.crypto.*
import java.io.File
import java.math.BigInteger

class Wallet constructor(val publicKey: ByteArray, val address: String, val privateKey: BigInteger) {

    fun saveToFile(path: String) = saveToFile(File(path))

    fun saveToFile(file: File) {
        val bytePacket = BytePacket()
        bytePacket.write(publicKey)
        bytePacket.write(address)
        bytePacket.write(privateKey)
        file.writeBytes(bytePacket.toByteArray())
    }

    companion object {
        fun generate(): Wallet {
            val keyPair = genECDSAKeyPair()
            val privateKey = keyPair.private.getPrivateKeyBigInt()
            val publicKey = keyPair.public.getUncompressedPublicKeyBytes()
            val hashed = publicKey.sha512().keccak256().ripemd160()
            val checksum = hashed.sha256().sha256()
            val addressBytes = mutableListOf<Byte>()
            addressBytes.add(0x01)
            addressBytes.addAll(hashed.toList())
            addressBytes.addAll(checksum.copyOfRange(checksum.size-8, checksum.size).toList())
            val address = "0x${addressBytes.toByteArray().toHexString()}"
            return Wallet(publicKey, address, privateKey)
        }

        fun fromFile(path: String): Wallet = fromFile(File(path))

        fun fromFile(file: File): Wallet {
            val bytes = file.readBytes()
            val bytePacket = BytePacket(bytes)
            val publicKey = bytePacket.readByteArray()
            val address = bytePacket.readString()
            val privateKey = bytePacket.readBigInt()
            return Wallet(publicKey, address, privateKey)
        }
    }
}