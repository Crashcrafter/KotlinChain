package dev.crash.chain

import dev.crash.crypto.asHexByteArray
import dev.crash.crypto.toHexString

data class TransactionOutput(
    val recipient: ByteArray,
    val amount: Long,
    val data: ByteArray = byteArrayOf()
) {
    constructor(recipient: String, amount: Long, data: ByteArray = byteArrayOf()): this(recipient.removePrefix("0x").asHexByteArray(), amount, data)

    data class JsonObj(
        val recipient: String,
        val amount: Long,
        val data: String
    )

    fun getJsonObj(): JsonObj {
        return JsonObj(recipient.toHexString(), amount, data.toHexString())
    }
}