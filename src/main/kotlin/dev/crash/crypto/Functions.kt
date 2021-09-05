package dev.crash.crypto

import dev.crash.toUTF8ByteArray
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jcajce.provider.digest.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECPoint

//Hex
fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
fun String.asHexByteArray(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4)
                + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

//Base Functions
fun ByteArray.base58(): String = Base58.encode(this)
fun Byte.base58(): String = Base58.encode(byteArrayOf(this))
fun String.base58(): ByteArray = Base58.decode(this)

//SHA Hashes
fun ByteArray.sha224(): ByteArray = SHA224.Digest().digest(this)
fun ByteArray.sha256(): ByteArray = SHA256.Digest().digest(this)
fun ByteArray.sha384(): ByteArray = SHA384.Digest().digest(this)
fun ByteArray.sha512(): ByteArray = SHA512.Digest().digest(this)

//Keccak
fun ByteArray.keccak224(): ByteArray = Keccak.Digest224().digest(this)
fun ByteArray.keccak256(): ByteArray = Keccak.Digest256().digest(this)
fun ByteArray.keccak288(): ByteArray = Keccak.Digest288().digest(this)
fun ByteArray.keccak384(): ByteArray = Keccak.Digest384().digest(this)
fun ByteArray.keccak512(): ByteArray = Keccak.Digest512().digest(this)

//Ripemd
fun ByteArray.ripemd128(): ByteArray = RIPEMD128.Digest().digest(this)
fun ByteArray.ripemd160(): ByteArray = RIPEMD160.Digest().digest(this)
fun ByteArray.ripemd256(): ByteArray = RIPEMD256.Digest().digest(this)
fun ByteArray.ripemd320(): ByteArray = RIPEMD320.Digest().digest(this)

//Standard Curve
val CURVE_PARAMS: X9ECParameters = CustomNamedCurves.getByName("secp256k1")
val SPEC = ECGenParameterSpec("secp256k1")
val CURVE = ECDomainParameters(CURVE_PARAMS.curve, CURVE_PARAMS.g, CURVE_PARAMS.n, CURVE_PARAMS.h)

//KeyPair Generation
fun genECDSAKeyPair(): KeyPair {
    val generator = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider())
    generator.initialize(SPEC)
    return generator.generateKeyPair()
}

fun PrivateKey.getPrivateKeyBigInt(): BigInteger = (this as ECPrivateKey).s

fun PublicKey.getUncompressedPublicKeyBytes(): ByteArray {
    val point : ECPoint = (this as ECPublicKey).w
    val sx = adjustTo64(point.affineX.toString(16)).uppercase()
    val sy = adjustTo64(point.affineY.toString(16)).uppercase()
    return "04$sx$sy".toUTF8ByteArray()
}

fun adjustTo64(s: String): String {
    return when (s.length) {
        62 -> "00$s"
        63 -> "0$s"
        64 -> s
        else -> throw IllegalArgumentException("not a valid key: $s")
    }
}