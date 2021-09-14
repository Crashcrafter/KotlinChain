import dev.crash.chain.Address
import dev.crash.chain.TransactionOutput
import dev.crash.node.KotlinNode
import kotlin.random.Random
import kotlin.random.nextLong

fun main(){
    val address = Address.generate()
    address.createTransaction(TransactionOutput(KotlinNode.nodeAddress.address, Random.nextLong(0..Long.MAX_VALUE)))
    dev.crash.mainStart()
}