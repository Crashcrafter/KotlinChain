package dev.crash.node

import dev.crash.chain.Transaction

object Mempool {
    val nextBlock = mutableListOf<Transaction>()
}