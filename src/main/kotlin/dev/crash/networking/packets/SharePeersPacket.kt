package dev.crash.networking.packets

import dev.crash.BytePacket
import dev.crash.networking.Packet
import dev.crash.networking.PacketType
import dev.crash.networking.PeerHandler

class SharePeersPacket : Packet(PacketType.SHARE_PEERS) {
    override fun createPacket(): BytePacket {
        val bytePacket = BytePacket()
        bytePacket.writeAsVarInt(PeerHandler.peers.size) // Size of the following list
        PeerHandler.peers.forEach {
            bytePacket.write(it.ip) // Ip of peer
            bytePacket.writeAsVarInt(it.port) // Port of peer
            bytePacket.write(it.nodeAddress) // Address of peer
            bytePacket.writeAsVarInt(it.chainId) // Chain ID of peer
        }
        return bytePacket
    }
}