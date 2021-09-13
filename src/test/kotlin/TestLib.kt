import dev.crash.chain.Address
import dev.crash.chain.TransactionOutput
import kotlin.random.Random
import kotlin.random.nextLong

fun main(){
    val address = Address.generate()
    address.createTransaction(TransactionOutput("ea1cd0569aa4dcd90f0103219420317d051e55663f488df3", Random.nextLong(0..Long.MAX_VALUE)))
    dev.crash.mainStart()
}