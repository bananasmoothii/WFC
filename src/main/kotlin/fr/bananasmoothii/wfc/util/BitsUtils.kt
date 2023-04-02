package fr.bananasmoothii.wfc.util

const val ZERO_LEFT = 1L shl 63

/**
 * @param index the index of the bit, will be mod 64 thanks to [Long.ushr]
 * @return 0L 000...000010000... where there are [index] 0s before the 1
 */
fun bitAt(index: Int) = ZERO_LEFT ushr index

fun arraySizeForMaxIndex(maxIndex: Int) = maxIndex / 64 + 1

fun LongArray.set1BitAt(index: Int) {
    this[index / 64] = this[index / 64] or bitAt(index)
}

fun LongArray.set0BitAt(index: Int) {
    this[index / 64] = this[index / 64] and bitAt(index).inv()
}

fun LongArray.setBitAt(index: Int, value: Boolean) {
    if (value) set1BitAt(index) else set0BitAt(index)
}

fun LongArray.getBitAt(index: Int) = this[index / 64] and bitAt(index) != 0L

fun Int.idToArray(arrayLength: Int) = LongArray(arrayLength).also { it.set1BitAt(this) }

fun LongArray.setAll0AndSet1BitAt(index: Int) {
    val arrayIndex = index / 64
    for (i in indices) {
        if (i == arrayIndex) this[i] = bitAt(index)
        else this[i] = 0L
    }
}

fun LongArray.hasOnly0Bits() = all { it == 0L }

fun LongArray.hasSingle1Bit(): Boolean { // TODO: test
    var found = false
    for (i in indices) {
        val l = this[i]
        if (l == 0L) continue
        if (found) return false
        if (l and (l - 1) != 0L) return false
        found = true
    }
    return found
}

/**
 * @return true if anything changed
 */
infix fun LongArray.bitOrEquals(other: LongArray): Boolean {
    var changed = false
    for (i in indices) {
        val old = this[i]
        val new = old or other[i]
        if (old != new) {
            changed = true
            this[i] = new
        }
    }
    return changed
}

/**
 * @return true if anything changed
 */
infix fun LongArray.bitAndEquals(other: LongArray): Boolean {
    var changed = false
    for (i in indices) {
        val old = this[i]
        val new = old and other[i]
        if (old != new) {
            changed = true
            this[i] = new
        }
    }
    return changed
}

