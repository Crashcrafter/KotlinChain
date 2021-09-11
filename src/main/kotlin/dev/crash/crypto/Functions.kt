package dev.crash.crypto

import dev.crash.exceptions.ECDSAValidationException
import dev.crash.toUTF8ByteArray
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.asn1.x9.X9IntegerConverter
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jcajce.provider.digest.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.math.ec.ECAlgorithms
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec

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
val HALF_CURVE_ORDER: BigInteger = CURVE_PARAMS.n.shiftRight(1)

//KeyPair Generation
fun genECDSAKeyPair(): KeyPair {
    val generator = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider())
    generator.initialize(SPEC)
    return generator.generateKeyPair()
}

fun PrivateKey.getPrivateKeyBigInt(): BigInteger = (this as ECPrivateKey).s

fun PublicKey.getUncompressedPublicKeyBytes(): ByteArray {
    val point = (this as ECPublicKey).w
    val sx = adjustTo64(point.affineX.toString(16)).uppercase()
    val sy = adjustTo64(point.affineY.toString(16)).uppercase()
    return "04$sx$sy".toUTF8ByteArray()
}

fun ByteArray.publicKeyToAddress(): ByteArray {
    val hashed = sha512().keccak256().ripemd160()
    val checksum = hashed.sha256().sha256()
    val addressBytes = mutableListOf<Byte>()
    addressBytes.addAll(hashed.toList())
    addressBytes.addAll(checksum.copyOfRange(checksum.size-8, checksum.size).toList())
    return addressBytes.toByteArray()
}

fun adjustTo64(s: String): String {
    return when (s.length) {
        62 -> "00$s"
        63 -> "0$s"
        64 -> s
        else -> throw IllegalArgumentException("not a valid key: $s")
    }
}

//Crypto Functions
fun validateAddress(address: String): Boolean {
    if(address.length != 56) return false
    val bytes = address.asHexByteArray()
    val hashedPublicKey = bytes.copyOfRange(0, bytes.size-8)
    val checksum = hashedPublicKey.sha256().sha256()
    val combined = "${hashedPublicKey.toHexString()}${checksum.copyOfRange(checksum.size-8, checksum.size).toHexString()}"
    return combined == address
}

fun ByteArray.sign(privKey: BigInteger): ECDSASignature {
    val sig: ECDSASignature = doSign(this, privKey)
    var recId = -1
    val thisKey: ByteArray = publicKeyFromPrivate(privKey)
    for (i in 0..3) {
        val k: ByteArray? = recoverPublicKeyFromSignature(i.toByte(), sig.r, sig.s, this)
        if (k != null && k.contentEquals(thisKey)) {
            recId = i
            break
        }
    }
    if (recId == -1) {
        throw RuntimeException("Could not construct a recoverable key. This should never happen.")
    }
    sig.v = recId.toByte()
    return sig
}

fun doSign(input: ByteArray, privKey: BigInteger): ECDSASignature {
    if (input.size != 32) {
        throw IllegalArgumentException("Expected 32 byte input to ECDSA signature, not " + input.size)
    }
    val signer = ECDSASigner()
    val domain = ECDomainParameters(CURVE_PARAMS.curve, CURVE_PARAMS.g, CURVE_PARAMS.n)
    val ecPrivateKeyParams = ECPrivateKeyParameters(privKey, domain)
    signer.init(true, ecPrivateKeyParams)
    val components = signer.generateSignature(input)
    return ECDSASignature(components[0], components[1]).toCanonicalised()
}

fun publicKeyFromPrivate(privKey: BigInteger): ByteArray {
    val point = publicPointFromPrivate(privKey)
    return point.getEncoded(false)
}

private fun publicPointFromPrivate(privKey2: BigInteger): ECPoint {
    var privKey = privKey2
    if (privKey.bitLength() > CURVE.n.bitLength()) {
        privKey = privKey.mod(CURVE.n)
    }
    return FixedPointCombMultiplier().multiply(CURVE.g, privKey)
}

fun recoverPublicKeyFromSignature(recId: Byte, r: BigInteger, s: BigInteger, messageHash: ByteArray): ByteArray? {
    if(recId < 0) throw ECDSAValidationException("v must be positive, invalid signature")
    if(r.signum() < 0) throw ECDSAValidationException("r must be positive, invalid signature")
    if(s.signum() < 0) throw ECDSAValidationException("s must be positive, invalid signature")
    val n = CURVE.n
    val i = BigInteger.valueOf(recId.toLong() / 2)
    val x = r.add(i.multiply(n))
    val R: ECPoint = decompressKey(x, recId.toInt() and 1 == 1)
    if (!R.multiply(n).isInfinity) {
        return null
    }
    val e = BigInteger(1, messageHash)
    val eInv = BigInteger.ZERO.subtract(e).mod(n)
    val rInv = r.modInverse(n)
    val srInv = rInv.multiply(s).mod(n)
    val eInvrInv = rInv.multiply(eInv).mod(n)
    val q = ECAlgorithms.sumOfTwoMultiplies(CURVE.g, eInvrInv, R, srInv)
    return q.getEncoded(false)
}

private fun decompressKey(xBN: BigInteger, yBit: Boolean): ECPoint {
    val x9 = X9IntegerConverter()
    val compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.curve))
    compEnc[0] = (if (yBit) 0x03 else 0x02).toByte()
    return CURVE.curve.decodePoint(compEnc)
}

fun verifySignature(messageHash: ByteArray, publicKey: ByteArray, r: BigInteger, s: BigInteger): Boolean {
    val signer = ECDSASigner()
    val params = ECPublicKeyParameters(CURVE.curve.decodePoint(publicKey), CURVE)
    signer.init(false, params)
    return signer.verifySignature(messageHash, r, s)
}

class ECDSASignature(val r: BigInteger, val s: BigInteger) {
    var v: Byte = -1

    fun toCanonicalised(): ECDSASignature {
        return if (s > HALF_CURVE_ORDER) {
            ECDSASignature(r, CURVE.n.subtract(s))
        } else {
            this
        }
    }
}