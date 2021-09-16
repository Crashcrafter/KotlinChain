package dev.crash.storage

import org.kodein.db.leveldb.LevelDB
import org.kodein.db.leveldb.jvm.LevelDBJvm
import java.io.File

private const val chainDir = "blockchain"
private val dbs = hashMapOf<String, LevelDB>()

fun getLevelDB(table: String): LevelDB = dbs[table] ?: openDB(table)

fun openDB(name: String): LevelDB {
    File("$chainDir/$name").mkdirs()
    val db = LevelDBJvm.open("$chainDir/$name")
    dbs[name] = db
    return db
}

fun deleteDB(name: String): Boolean = File("$chainDir/$name").delete()

fun saveDBs() {
    dbs.values.forEach {
        it.close()
    }
}

fun LevelDB.clear() {
    val cursor = newCursor()
    cursor.seekToFirst()
    while (cursor.isValid()){
        delete(cursor.transientKey())
    }
    cursor.close()
}