package dev.crash.networking.handler

import dev.crash.BytePacket
import dev.crash.CONFIG
import dev.crash.networking.PacketHandler
import dev.crash.networking.PacketType
import dev.crash.networking.p2p.P2PChannel
import dev.crash.networking.packets.FinishAuthPacket

class AuthenticationPacketHandler : PacketHandler(PacketType.AUTHENTICATION) {
    override fun handle(channel: P2PChannel, packet: BytePacket) {
        val msg = packet.readString()
        val version = packet.readVarInt()
        val chainId = packet.readVarInt()
        val nodeAddress = packet.readString()
        val hash = packet.readByteArray()
        if(msg != "KotlinChainFullNode" || version != CONFIG.VERSION || chainId != CONFIG.CHAINID){
            channel.close()
            return
        }
        FinishAuthPacket(hash).send(channel)
    }
}