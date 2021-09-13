package dev.crash.networking

import dev.crash.BytePacket
import dev.crash.node.KotlinNode
import java.io.File

object PeerHandler {
    val peers = mutableListOf<Peer>()
    private val file = File("peers")

    fun loadPeers() {
        if(!file.exists()) {
            file.createNewFile()
            addPeer("127.0.0.1", 8334, KotlinNode.nodeAddress.address, 1)
            return
        }
        val packet = BytePacket(file.readBytes())
        val size = packet.readVarInt()
        for (i in 0 until size) {
            val ip = packet.readString()
            val port = packet.readVarInt()
            val address = packet.readString()
            val chainId = packet.readVarInt()
            var exists = false
            peers.forEach {
                if(hasPeer(it)) {
                    exists = true
                    return@forEach
                }
            }
            if(!exists) peers.add(Peer(ip, port, address, chainId))
        }
        save()
    }

    fun addPeer(peer: Peer) {
        if(!hasPeer(peer)) {
            peers.add(peer)
        }
        save()
    }

    fun addPeer(ip: String, port: Int, address: String, chainId: Int) = addPeer(Peer(ip, port, address, chainId))

    fun hasPeer(peer: Peer): Boolean {
        peers.forEach {
            if(it.ip == peer.ip && it.nodeAddress == peer.nodeAddress && it.chainId == peer.chainId) return true
        }
        return false
    }

    fun save(){
        val packet = BytePacket()
        packet.writeAsVarInt(peers.size)
        peers.forEach {
            packet.write(it.ip)
            packet.writeAsVarInt(it.port)
            packet.write(it.nodeAddress)
            packet.writeAsVarInt(it.chainId)
        }
        file.writeBytes(packet.toByteArray())
    }
}