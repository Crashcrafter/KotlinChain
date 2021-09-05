package dev.crash.networking.packets

import dev.crash.BytePacket
import dev.crash.crypto.sha256
import dev.crash.networking.Packet
import dev.crash.networking.PacketType
import dev.crash.node.KotlinNode

class FinishAuthPacket(val hash: ByteArray) : Packet(PacketType.FINISH_AUTH) {
    override fun createPacket(): BytePacket {
        val packet = BytePacket()
        packet.write(hash.sha256())
        packet.write(KotlinNode.nodeWallet.address)
        return packet
    }
}