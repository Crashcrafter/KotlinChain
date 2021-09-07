package dev.crash.storage

import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import java.io.File
import java.nio.ByteBuffer

val chainDir = File("blockchain/")

fun simpleEnv(): Env<ByteBuffer> = Env.create().setMapSize(10_485_760).setMaxReaders(10).setMaxDbs(4).open(chainDir)

fun Env<ByteBuffer>.openDBI(name: String): Dbi<ByteBuffer> = openDbi(name, DbiFlags.MDB_CREATE)