package dev.crash.networking.packethandler

import dev.crash.BytePacket
import dev.crash.networking.PacketHandler
import dev.crash.networking.PacketManager.authCodes
import dev.crash.networking.PacketType
import dev.crash.networking.PeerHandler
import dev.crash.networking.p2p.P2PChannel
import io.ktor.util.network.*

class FinishAuthPacketHandler : PacketHandler(PacketType.FINISH_AUTH) {
    override fun handle(channel: P2PChannel, packet: BytePacket) {
        val hash = packet.readByteArray()
        val otherNodeAddress = packet.readString()
        if(!hash.contentEquals(authCodes[channel])){
            channel.close()
            return
        }
        val remoteAddress = channel.socket.remoteAddress
        println("Finished auth with $remoteAddress")
        if(remoteAddress.port == 8334) return
        PeerHandler.addPeer(remoteAddress.hostname, remoteAddress.port, otherNodeAddress)
    }
}