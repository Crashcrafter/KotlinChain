package dev.crash

object CONFIG {
    var VERSION: Int = 0
    var CHAINID: Int = 1
    var BLOCKCHAINSERVERPORT: Int = 8334
    var RPCSERVERPORT: Int = 80

    fun setParams(version: Int = VERSION, chainId: Int = CHAINID, blockchainServerPort: Int = BLOCKCHAINSERVERPORT, rpcServerPort: Int = RPCSERVERPORT) {
        VERSION = version
        CHAINID = chainId
        BLOCKCHAINSERVERPORT = blockchainServerPort
        RPCSERVERPORT = rpcServerPort
    }
}