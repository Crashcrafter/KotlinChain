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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionOutput

        if (!recipient.contentEquals(other.recipient)) return false
        if (amount != other.amount) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipient.contentHashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}