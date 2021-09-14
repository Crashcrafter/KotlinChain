package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.networking.p2p.P2PChannel
import org.reflections.Reflections
import kotlin.collections.HashMap

object PacketManager {
    val packets: HashMap<Int, PacketHandler> = hashMapOf()
    val authCodes: HashMap<P2PChannel, ByteArray> = hashMapOf()

    init {
        Reflections("dev.crash.networking.handler").getSubTypesOf(PacketHandler::class.java).forEach {
            it.getDeclaredConstructor().newInstance()
        }
    }

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

