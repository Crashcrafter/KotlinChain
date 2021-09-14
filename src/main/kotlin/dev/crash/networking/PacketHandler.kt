package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.networking.p2p.P2PChannel

abstract class PacketHandler(val packetType: PacketType) {
    abstract fun handle(channel: P2PChannel, packet: BytePacket)

    init {
        PacketManager.packets[packetType.packetId] = this
    }
}