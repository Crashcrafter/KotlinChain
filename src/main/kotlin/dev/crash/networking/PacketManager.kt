package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.networking.p2p.P2PChannel
import dev.crash.networking.packethandler.AuthenticationPacketHandler
import dev.crash.networking.packethandler.FinishAuthPacketHandler
import dev.crash.networking.packethandler.SharePeersPacketHandler

object PacketManager {
    val packets: HashMap<Int, PacketHandler> = hashMapOf(0 to AuthenticationPacketHandler(), 1 to FinishAuthPacketHandler(), 2 to SharePeersPacketHandler())
    val authCodes: HashMap<P2PChannel, ByteArray> = hashMapOf()

    fun handlePacket(channel: P2PChannel, bytes: ByteArray) {
        val packet = BytePacket(bytes)
        while (packet.bytes.size > packet.readPos){
            val packetId = packet.readVarInt()
            if(packets.containsKey(packetId)){
                try {
                    packets[packetId]!!.handle(channel, packet)
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    channel.close()
                    break
                }
            }else {
                println("Unhandled Packet with packet id $packetId")
                channel.close()
                break
            }
        }
    }
}

