package dev.crash.networking.packets

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.crypto.sha256
import dev.crash.networking.Packet
import dev.crash.networking.PacketType
import dev.crash.node.KotlinNode

class FinishAuthPacket(private val hash: ByteArray) : Packet(PacketType.FINISH_AUTH) {
    override fun createPacket(): BytePacket {
        val packet = BytePacket()
        packet.write(hash.sha256()) // Hash
        packet.write(KotlinNode.nodeAddress.address) // Node address
        packet.writeAsVarInt(CONFIG.CHAINID) // Chain ID
        return packet
    }
}