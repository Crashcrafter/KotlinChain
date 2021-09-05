package dev.crash.networking.p2p

import dev.crash.BytePacket
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress

class P2PServer(val hostname: String, val port: Int, val messageHandler: P2PHandler = object : P2PHandler{}) {

    private lateinit var server: ServerSocket
    val channels = mutableListOf<P2PChannel>()

    init {
        GlobalScope.launch {
            server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(hostname, port))
            println("Server running: ${server.localAddress}")
            while (true){
                val socket = server.accept()
                println("Socket accepted: ${socket.remoteAddress}")
                launch {
                    val channel = P2PChannel(socket, messageHandler)
                    channels.add(channel)
                    channel.open()
                    channels.remove(channel)
                }
            }
        }
    }

    fun close() {
        server.close()
        channels.clear()
    }

    fun sendPacketToAll(bytePacket: BytePacket) = sendToAll(bytePacket.toByteArray())

    fun sendToAll(bytes: ByteArray) {
        runBlocking {
            channels.forEach {
                it.sendBytes(bytes)
            }
        }
    }
}