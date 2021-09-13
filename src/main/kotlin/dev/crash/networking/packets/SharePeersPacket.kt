package dev.crash.networking.packets

import dev.crash.BytePacket
import dev.crash.networking.Packet
import dev.crash.networking.PacketType
import dev.crash.networking.PeerHandler

class SharePeersPacket : Packet(PacketType.SHARE_PEERS) {
    override fun createPacket(): BytePacket {
        val bytePacket = BytePacket()
        bytePacket.writeAsVarInt(PeerHandler.peers.size)
        PeerHandler.peers.forEach {
            bytePacket.write(it.ip)
            bytePacket.writeAsVarInt(it.port)
            bytePacket.write(it.nodeAddress)
            bytePacket.writeAsVarInt(it.chainId)
        }
        return bytePacket
    }
}