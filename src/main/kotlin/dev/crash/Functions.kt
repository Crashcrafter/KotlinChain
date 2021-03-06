package dev.crash

import dev.crash.crypto.asHexByteArray
import dev.crash.crypto.toHexString
import org.kodein.memory.io.ByteArrayMemory
import java.nio.charset.Charset
import kotlin.experimental.or

fun String.toUTF8ByteArray(): ByteArray = toByteArray(Charset.defaultCharset())

fun Short.toByteArray(): ByteArray = byteArrayOf((this.toInt() ushr 8).toByte(), this.toByte())

fun Int.toByteListAsVarInt(): List<Byte> {
    var bvalue = this
    val result = mutableListOf<Byte>()
    do {
        var temp = (bvalue and 127).toByte()
        bvalue = bvalue ushr 7
        if (bvalue != 0) {
            temp = temp or 128.toByte()
        }
        result.add(temp)
    } while (bvalue != 0)
    return result
}

fun Int.toByteArrayAsVarInt(): ByteArray = toByteListAsVarInt().toByteArray()

fun Int.toByteArray(): ByteArray = byteArrayOf(
    (this ushr 24).toByte(), (this ushr 16).toByte(),
    (this ushr 8).toByte(), this.toByte()
)

fun Long.toByteListAsVarLong(): List<Byte> {
    var bvalue = this
    val result = mutableListOf<Byte>()
    do {
        var temp = (bvalue and 127).toByte()
        bvalue = bvalue ushr 7
        if (bvalue != 0L) {
            temp = temp or 128.toByte()
        }
        result.add(temp)
    } while (bvalue != 0L)
    return result
}

fun Long.toByteArrayAsVarLong(): ByteArray = toByteListAsVarLong().toByteArray()

fun Long.toByteArray(): ByteArray = byteArrayOf(
    (this ushr 56).toByte(), (this ushr 48).toByte(),
    (this ushr 40).toByte(), (this ushr 32).toByte(), (this ushr 24).toByte(),
    (this ushr 16).toByte(), (this ushr 8).toByte(), this.toByte()
)

fun ByteArray.toMemory(): ByteArrayMemory = ByteArrayMemory(this)

fun List<ByteArray>.toHexStringList(): List<String> {
    val result = mutableListOf<String>()
    forEach {
        result.add(it.toHexString())
    }
    return result
}

fun List<String>.toHexByteArrayList(): List<ByteArray> {
    val result = mutableListOf<ByteArray>()
    forEach {
        result.add(it.asHexByteArray())
    }
    return result
}