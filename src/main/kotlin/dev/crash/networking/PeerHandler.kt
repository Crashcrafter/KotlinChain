package dev.crash.networking

import dev.crash.BytePacket
import java.io.File

object PeerHandler {
    val peers = mutableListOf<Peer>()
    private val file = File("peers")

    fun loadPeers() {
        if(!file.exists()) {
            file.createNewFile()
            peers.add(Peer("127.0.0.1", 8334, ""))
            save()
            return
        }
        val packet = BytePacket(file.readBytes())
        val size = packet.readVarInt()
        for (i in 0 until size) {
            val ip = packet.readString()
            val port = packet.readVarInt()
            val address = packet.readString()
            var exists = false
            peers.forEach {
                if(ip == it.ip) {
                    exists = true
                    return@forEach
                }
            }
            if(!exists) peers.add(Peer(ip, port, address))
            save()
        }
    }

    fun addPeer(ip: String, port: Int, address: String) {
        val toRemove = mutableListOf<Peer>()
        peers.forEach {
            if(it.nodeAddress == address) {
                if(it.ip == ip){
                    return
                }else {
                    toRemove.add(it)
                }
            }
        }
        peers.add(Peer(ip, port, address))
        toRemove.forEach {
            peers.remove(it)
        }
        save()
    }

    fun save(){
        val packet = BytePacket()
        packet.writeAsVarInt(peers.size)
        peers.forEach {
            packet.write(it.ip)
            packet.writeAsVarInt(it.port)
            packet.write(it.nodeAddress)
        }
        file.writeBytes(packet.toByteArray())
    }
}