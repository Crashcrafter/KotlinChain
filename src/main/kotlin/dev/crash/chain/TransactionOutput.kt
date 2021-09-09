package dev.crash.chain

data class TransactionOutput(
    val recipient: String,
    val amount: Long,
    val data: ByteArray = byteArrayOf()
)