package dev.crash.chain

data class AddressTransactionPreview(
    val txid: String,
    val amount: Long,
    val otherAddresses: List<String>
)