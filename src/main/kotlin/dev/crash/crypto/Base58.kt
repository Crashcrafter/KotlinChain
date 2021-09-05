package dev.crash.crypto

object Base58 {
    private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val BASE_58 = ALPHABET.size
    private const val BASE_256 = 256
    private val INDEXES = IntArray(128)
    fun encode(inputArray: ByteArray): String {
        var input = inputArray
        if (input.isEmpty()) {
            return ""
        }
        input = copyOfRange(input, 0, input.size)
        var zeroCount = 0
        while (zeroCount < input.size && input[zeroCount] == 0.toByte()) {
            ++zeroCount
        }
        val temp = ByteArray(input.size * 2)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < input.size) {
            val mod = divmod58(input, startAt)
            if (input[startAt] == 0.toByte()) {
                ++startAt
            }
            temp[--j] = ALPHABET[mod.toInt()].code.toByte()
        }
        while (j < temp.size && temp[j] == ALPHABET[0].code.toByte()) {
            ++j
        }
        while (--zeroCount >= 0) {
            temp[--j] = ALPHABET[0].code.toByte()
        }
        val output = copyOfRange(temp, j, temp.size)
        return String(output)
    }

    fun decode(input: String): ByteArray {
        if (input.isEmpty()) {
            return ByteArray(0)
        }
        val input58 = ByteArray(input.length)
        for (i in input.indices) {
            val c = input[i]
            var digit58 = -1
            if (c.code in 0..127) {
                digit58 = INDEXES[c.code]
            }
            if (digit58 < 0) {
                throw RuntimeException("Not a Base58 input: $input")
            }
            input58[i] = digit58.toByte()
        }
        var zeroCount = 0
        while (zeroCount < input58.size && input58[zeroCount] == 0.toByte()) {
            ++zeroCount
        }
        val temp = ByteArray(input.length)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < input58.size) {
            val mod = divmod256(input58, startAt)
            if (input58[startAt] == 0.toByte()) {
                ++startAt
            }
            temp[--j] = mod
        }
        while (j < temp.size && temp[j] == 0.toByte()) {
            ++j
        }
        return copyOfRange(temp, j - zeroCount, temp.size)
    }

    private fun divmod58(number: ByteArray, startAt: Int): Byte {
        var remainder = 0
        for (i in startAt until number.size) {
            val digit256 = number[i].toInt() and 0xFF
            val temp = remainder * BASE_256 + digit256
            number[i] = (temp / BASE_58).toByte()
            remainder = temp % BASE_58
        }
        return remainder.toByte()
    }

    private fun divmod256(number58: ByteArray, startAt: Int): Byte {
        var remainder = 0
        for (i in startAt until number58.size) {
            val digit58 = number58[i].toInt() and 0xFF
            val temp = remainder * BASE_58 + digit58
            number58[i] = (temp / BASE_256).toByte()
            remainder = temp % BASE_256
        }
        return remainder.toByte()
    }

    private fun copyOfRange(source: ByteArray, from: Int, to: Int): ByteArray {
        val range = ByteArray(to - from)
        System.arraycopy(source, from, range, 0, range.size)
        return range
    }

    init {
        for (i in INDEXES.indices) {
            INDEXES[i] = -1
        }
        for (i in ALPHABET.indices) {
            INDEXES[ALPHABET[i].code] = i
        }
    }
}