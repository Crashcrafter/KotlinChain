package dev.crash.networking.p2p

import dev.crash.networking.PacketManager

interface P2PHandler {
    fun handle(channel: P2PChannel, bytes: ByteArray) {
        PacketManager.handlePacket(channel, bytes)
    }

    fun onConnect(channel: P2PChannel) {
        println("Channel opened to " + channel.socket.remoteAddress)
    }

    fun onDisconnect(channel: P2PChannel) {
        println("Channel ${channel.socket.remoteAddress} closed!")
    }
}