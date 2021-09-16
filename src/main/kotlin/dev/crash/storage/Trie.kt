package dev.crash.storage

import org.kodein.db.leveldb.LevelDB

abstract class Trie(val dbName: String) {
    val db: LevelDB = getLevelDB(dbName)

    fun clear() = db.clear()

    fun delete() {
        db.close()
        deleteDB(dbName)
    }
}