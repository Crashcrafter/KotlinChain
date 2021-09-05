package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.networking.p2p.P2PChannel
import io.netty.channel.Channel

abstract class Packet(val packetType: PacketType) {
    abstract fun createPacket(): BytePacket

    fun send(channel: P2PChannel){
        val packet = BytePacket()
        packet.writeAsVarInt(packetType.packetId)
        packet.write(createPacket())
        channel.sendPacket(packet)
    }
}