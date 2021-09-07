import dev.crash.BytePacket
import dev.crash.crypto.toHexString
import kotlin.random.Random

suspend fun main(){
    val bytePacket = BytePacket()
    bytePacket.writeAsVarLong(0)
    bytePacket.writeAsVarLong(21000)
    bytePacket.writeAsVarInt(1)
    bytePacket.write("014faf0ee12b34b4230264b43b18f8c92e8c7b179175aeaaca2278466f")
    bytePacket.writeAsVarLong(1000)
    bytePacket.write(byteArrayOf())
    bytePacket.writeAsVarInt(3)
    bytePacket.write(Random.nextBytes(32))
    bytePacket.write(Random.nextBytes(32))
    println(bytePacket.toByteArray().toHexString())
    dev.crash.mainStart()
}