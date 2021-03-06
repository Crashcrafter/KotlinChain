package dev.crash.networking.handler

import dev.crash.BytePacket
import dev.crash.networking.PacketHandler
import dev.crash.networking.PacketType
import dev.crash.networking.Peer
import dev.crash.networking.PeerHandler
import dev.crash.networking.p2p.P2PChannel

class SharePeersPacketHandler : PacketHandler(PacketType.SHARE_PEERS) {
    override fun handle(channel: P2PChannel, packet: BytePacket) {
        val size = packet.readVarInt()
        for(i in 0 until size) {
            val ip = packet.readString()
            val port = packet.readVarInt()
            val nodeAddress = packet.readByteArray()
            val chainId = packet.readVarInt()
            PeerHandler.addPeer(Peer(ip, port, nodeAddress, chainId))
        }
    }
}