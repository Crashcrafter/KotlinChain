package dev.crash.networking

enum class PacketType(val packetId: Int) {
    AUTHENTICATION(0),
    FINISH_AUTH(1),
    SHARE_PEERS(2),
    TRANSACTION_RECEIVED(3)
}