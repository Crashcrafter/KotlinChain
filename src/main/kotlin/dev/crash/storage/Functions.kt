package dev.crash.storage

import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory
import java.io.File

const val chainDir = "blockchain"

fun getLevelDB(table: String): DB {
    val options = Options()
    options.createIfMissing(true)
    return Iq80DBFactory.factory.open(File("$chainDir/$table"), options)
}