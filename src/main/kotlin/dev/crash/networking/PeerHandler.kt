package dev.crash.networking

import dev.crash.BytePacket
import java.io.File

object PeerHandler {
    val peers = mutableListOf<Peer>()
    private val file = File("peers")

    fun loadPeers() {
        if(!file.exists()) {
            file.createNewFile()
            save()
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
                if(ip == it.ip && chainId != it.chainId) {
                    exists = true
                    return@forEach
                }
            }
            if(!exists) peers.add(Peer(ip, port, address, chainId))
            save()
        }
    }

    fun addPeer(peer: Peer) {
        val toRemove = mutableListOf<Peer>()
        peers.forEach {
            if(it.nodeAddress == peer.nodeAddress) {
                if(it.ip == peer.ip){
                    return
                }else {
                    toRemove.add(it)
                }
            }
        }
        peers.add(peer)
        toRemove.forEach {
            peers.remove(it)
        }
        save()
    }

    fun addPeer(ip: String, port: Int, address: String, chainId: Int) = addPeer(Peer(ip, port, address, chainId))

    fun hasPeer(peer: Peer): Boolean {
        peers.forEach {
            if(it.ip == peer.ip && it.port == peer.port && it.nodeAddress == peer.nodeAddress && it.chainId == peer.chainId) return true
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
        }
        file.writeBytes(packet.toByteArray())
    }
}