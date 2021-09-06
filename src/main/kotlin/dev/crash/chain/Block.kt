package dev.crash.chain

import dev.crash.crypto.sha256
import dev.crash.crypto.toHexString
import dev.crash.toByteArray
import kotlin.random.Random

class Block(val transactions: List<Transaction>, val blockNonce: Long) {
    private var bytes = byteArrayOf()
    var isConfirmed = false

    fun validate(): Boolean {
        if(blockNonce < 0) return false
        //Validate Transactions
        transactions.forEach {
            bytes = bytes.plus(it.bytes)
            if(!it.validate()) return false
        }
        //Small PoW
        var resolution: Long
        do {
            resolution = Random.nextLong()
            val hash = bytes.plus(resolution.toByteArray()).sha256().toHexString()
            isConfirmed = hash.startsWith("0000")
        }while (!isConfirmed)
        println("Block confirmation with number $resolution")
        return true
    }
}