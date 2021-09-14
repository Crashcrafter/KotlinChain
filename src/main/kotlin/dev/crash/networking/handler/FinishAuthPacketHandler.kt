package dev.crash.networking.handler

import dev.crash.BytePacket
import dev.crash.networking.PacketHandler
import dev.crash.networking.PacketManager.authCodes
import dev.crash.networking.PacketType
import dev.crash.networking.PeerHandler
import dev.crash.networking.p2p.P2PChannel
import dev.crash.networking.packets.SharePeersPacket
import io.ktor.util.network.*

class FinishAuthPacketHandler : PacketHandler(PacketType.FINISH_AUTH) {
    override fun handle(channel: P2PChannel, packet: BytePacket) {
        val hash = packet.readByteArray()
        val otherNodeAddress = packet.readByteArray()
        val chainId = packet.readVarInt()
        if(!hash.contentEquals(authCodes[channel])){
            channel.close()
            return
        }
        val remoteAddress = channel.socket.remoteAddress
        println("Finished auth with $remoteAddress")
        if(remoteAddress.port == 8334) return
        SharePeersPacket().send(channel)
        PeerHandler.addPeer(remoteAddress.hostname, remoteAddress.port, otherNodeAddress, chainId)
    }
}