package dev.crash.networking.p2p

import dev.crash.BytePacket
import dev.crash.networking.BlockChainServer
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

class P2PClient(val ip: String, val port: Int, val messageHandler: P2PHandler = object : P2PHandler{}) {

    private lateinit var socket: Socket
    lateinit var channel: P2PChannel
    var isConnected: Boolean = false

    init {
        GlobalScope.launch {
            socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(ip, port))
            channel = P2PChannel(socket, messageHandler)
            isConnected = true
            channel.open()
            isConnected = false
            BlockChainServer.clients.remove(this@P2PClient)
        }
    }

    fun close() = socket.close()

    fun sendBytes(bytes: ByteArray) = runBlocking { channel.sendBytes(bytes) }

    fun sendPacket(bytePacket: BytePacket) = runBlocking { channel.sendPacket(bytePacket) }
}