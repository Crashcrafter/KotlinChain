package dev.crash.networking

data class Peer(val ip: String, val port: Int, val nodeAddress: ByteArray, val chainId: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Peer

        if (ip != other.ip) return false
        if (port != other.port) return false
        if (!nodeAddress.contentEquals(other.nodeAddress)) return false
        if (chainId != other.chainId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.hashCode()
        result = 31 * result + port
        result = 31 * result + nodeAddress.contentHashCode()
        result = 31 * result + chainId
        return result
    }
}