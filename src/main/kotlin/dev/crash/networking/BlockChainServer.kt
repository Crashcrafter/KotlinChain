package dev.crash.networking

import dev.crash.networking.p2p.P2PChannel
import dev.crash.networking.p2p.P2PClient
import dev.crash.networking.p2p.P2PHandler
import dev.crash.networking.p2p.P2PServer
import dev.crash.networking.packets.AuthenticationPacket

object BlockChainServer {
    val clients = mutableListOf<P2PClient>()

    fun start(port: Int) {
        println("Starting Blockchain TCP server on port $port...")
        val msgHandler = object : P2PHandler {
            override fun onConnect(channel: P2PChannel) {
                AuthenticationPacket(channel).send(channel)
            }
        }
        P2PServer("127.0.0.1", port, msgHandler)
        PeerHandler.loadPeers()
        PeerHandler.peers.forEach {
            val client = P2PClient(it.ip, it.port, msgHandler)
            clients.add(client)
        }
        println("Blockchain TCP server started!")
    }
}