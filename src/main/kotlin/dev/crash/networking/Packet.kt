package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.networking.p2p.P2PChannel
import io.netty.channel.Channel

abstract class Packet(val packetType: PacketType) {
    abstract fun createPacket(): BytePacket

    fun createPacketWithId(): BytePacket {
        val packet = BytePacket()
        packet.writeAsVarInt(packetType.packetId)
        packet.write(createPacket())
        return packet
    }

    fun send(channel: P2PChannel){
        channel.sendPacket(createPacketWithId())
    }
}