import dev.crash.chain.Address
import dev.crash.chain.TransactionOutput

suspend fun main(){
    val address = Address.generate()
    address.createTransaction(TransactionOutput("0x23d4fa575b21ad1dd2db14547a26f24845a8f96843f493569875197d", 1000))
    dev.crash.mainStart()
}