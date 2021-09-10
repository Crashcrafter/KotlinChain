package dev.crash.storage

data class AddressTransactionPreview(
    val txid: String,
    val amount: Long,
    val sent: Boolean,
    val otherAddress: String
)