package fr.bananasmoothii.wfc.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BitsUtilsKtTest {

    @Test
    fun bitAt() {
        bitAt(0) shouldBe Long.MIN_VALUE
        bitAt(1) shouldBe 0x4000000000000000
        bitAt(63) shouldBe 1L
    }

    @Test
    fun arraySizeForMaxIndex() {
        arraySizeForMaxIndex(0) shouldBe 1
        arraySizeForMaxIndex(1) shouldBe 1
        arraySizeForMaxIndex(63) shouldBe 1
        arraySizeForMaxIndex(64) shouldBe 2
    }

    @Test
    fun set1BitAt() {
        val array = LongArray(2)
        val arrayExpected = LongArray(2).also {
            it[0] = 0x4000000000000000
            it[1] = 0x0000000000000001
        }
        array.set1BitAt(1)
        array.set1BitAt(127)
        array shouldBe arrayExpected
    }

    @Test
    fun set0BitAt() {
        val array = LongArray(2) { -1L }
        val arrayExpected = LongArray(2).also {
            it[0] = 0x4000000000000000.inv()
            it[1] = 0x0000000000000001.inv()
        }
        array.set0BitAt(1)
        array.set0BitAt(127)
        array shouldBe arrayExpected
    }

    @Test
    fun getBitAt() {
        val array = LongArray(2).also {
            it[0] = 0x4000000000000000
            it[1] = 0x0000000000000001
        }
        array.getBitAt(1) shouldBe true
        array.getBitAt(127) shouldBe true
        array.getBitAt(0) shouldBe false
        array.getBitAt(63) shouldBe false
    }

    @Test
    fun setAll0AndSet1BitAt() {
        val array = LongArray(2)
        val arrayExpected = LongArray(2).also {
            it[1] = 0x0000000000000001
        }
        array.setAll0AndSet1BitAt(1)
        array.setAll0AndSet1BitAt(127)
        array shouldBe arrayExpected
    }

    @Test
    fun hasOnly0Bits() {
        LongArray(2).hasOnly0Bits() shouldBe true
        LongArray(2).also { it[0] = 0b0010 }.hasOnly0Bits() shouldBe false
        LongArray(2).also { it[1] = 0b1000 }.hasOnly0Bits() shouldBe false
        LongArray(2).also { it[0] = 0b0100; it[1] = 0b0100 }.hasOnly0Bits() shouldBe false
    }

    @Test
    fun hasSingle1Bit() {
        LongArray(2).hasSingle1Bit() shouldBe false
        LongArray(2).also { it[0] = 0b0010 }.hasSingle1Bit() shouldBe true
        LongArray(2).also { it[1] = 0b1100 }.hasSingle1Bit() shouldBe false
        LongArray(2).also { it[0] = 0b0100; it[1] = 0b0100 }.hasSingle1Bit() shouldBe false
    }

    @Test
    fun bitOrEquals() {
        val array = LongArray(2)
        val other = LongArray(2).also { it[0] = 0b0010 }
        (array bitOrEquals other) shouldBe true
        array shouldBe other
        (array bitOrEquals other) shouldBe false
    }

    @Test
    fun bitAndEquals() {
        val array = LongArray(2).also { it[0] = 0b0010 }
        val other = LongArray(2).also { it[0] = 0b0001 }
        (array bitAndEquals other) shouldBe true
        array shouldBe LongArray(2)
        (array bitAndEquals other) shouldBe false
    }
}