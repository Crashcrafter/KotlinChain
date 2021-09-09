package dev.crash.storage

import org.kodein.db.leveldb.LevelDB
import org.kodein.db.leveldb.jvm.LevelDBJvm
import java.io.File

private const val chainDir = "blockchain"
private val dbs = hashMapOf<String, LevelDB>()

fun getLevelDB(table: String): LevelDB = dbs[table] ?: openDB(table)

private fun openDB(name: String): LevelDB {
    val db = LevelDBJvm.open("$chainDir/$name")
    dbs[name] = db
    return db
}

fun saveDBs() {
    dbs.values.forEach {
        it.close()
    }
}

fun createDirectories(){
    File(chainDir).mkdir()
    File("$chainDir/blocks").mkdir()
    File("$chainDir/addresses").mkdir()
    File("$chainDir/transactions").mkdir()
    File("$chainDir/contracts").mkdir()
}