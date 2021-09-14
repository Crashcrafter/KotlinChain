package dev.crash.networking.packets

import dev.crash.BytePacket
import dev.crash.crypto.sha256
import dev.crash.networking.Packet
import dev.crash.networking.PacketManager.authCodes
import dev.crash.networking.PacketType
import dev.crash.networking.p2p.P2PChannel
import dev.crash.node.KotlinNode
import dev.crash.toByteArray
import kotlin.random.Random

class AuthenticationPacket(private val channel: P2PChannel) : Packet(PacketType.AUTHENTICATION) {
    override fun createPacket(): BytePacket {
        val packet = BytePacket()
        packet.write("KotlinChainFullNode") // Payload
        packet.writeAsVarInt(0) // Version
        packet.writeAsVarInt(1) // ChainId
        packet.write(KotlinNode.nodeAddress.address) // Node Address
        val hash = Random.nextLong().toByteArray().sha256()
        authCodes[channel] = hash.sha256()
        packet.write(hash) // Hash
        return packet
    }
}