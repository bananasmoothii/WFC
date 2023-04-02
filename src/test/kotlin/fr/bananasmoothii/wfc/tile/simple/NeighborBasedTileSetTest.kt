package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.d2.D2
import fr.bananasmoothii.wfc.space.d2.Dimension2D
import fr.bananasmoothii.wfc.space.d2.Direction2D
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.util.set1BitAt
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NeighborBasedTileSetTest {

    data class Arrow2D(val direction: Direction2D) : Rotatable<Dimension2D>, D2 {
        override fun generateRotations(): Iterable<Rotatable<Dimension2D>> = Direction2D.values.map { Arrow2D(it) }
    }

    private val tileSet = NeighborBasedTileSet<Arrow2D, Dimension2D>()

    @Test
    @Order(0)
    fun generateRotations() {
        tileSet.createOrGetTile(Arrow2D(Direction2D.UP))
        tileSet.generateRotations()
        tileSet.tiles.size shouldBe 4
    }

    @Test
    @Order(2)
    fun getTiles() {
        tileSet.tiles.map { it.content } shouldContainAll listOf(
            Arrow2D(Direction2D.UP),
            Arrow2D(Direction2D.RIGHT),
            Arrow2D(Direction2D.DOWN),
            Arrow2D(Direction2D.LEFT)
        )
    }

    @Test
    @Order(3)
    fun createOrGetTile() {
        tileSet.createOrGetTile(Arrow2D(Direction2D.UP)) shouldBe tileSet.fromId(0)
    }

    @Test
    @Order(4)
    fun `finish tile creation`() {
        tileSet.finishTileCreation()
    }

    @Nested
    inner class Tiles {

        @Test
        @Order(5)
        fun `neighbor modification`() {
            val tile = tileSet.fromId(0)
            tile.accepts(Direction2D.UP, 0) shouldBe false
            tile.accepts(Direction2D.RIGHT, 3) shouldBe false
            tile.accept(Direction2D.RIGHT, 0)
            tile.accepts(Direction2D.RIGHT, 0) shouldBe true
            tile.accepts(Direction2D.RIGHT, 3) shouldBe false
        }

        @Test
        @Order(6)
        fun addAllowedNeighborsToArray() {
            val tile = tileSet.fromId(1)
            tile.accept(Direction2D.UP, 0)
            tile.accept(Direction2D.RIGHT, 1)
            tile.accept(Direction2D.DOWN, 2)
            tile.accept(Direction2D.LEFT, 3)
            val bitMask = LongArray(1)

            tile.addAllowedNeighborsToArray(bitMask, Direction2D.UP)
            val expected = LongArray(1).also { it.set1BitAt(0) }
            bitMask shouldBe expected

            tile.addAllowedNeighborsToArray(bitMask, Direction2D.RIGHT)
            expected.set1BitAt(1)
            bitMask shouldBe expected

            tile.addAllowedNeighborsToArray(bitMask, Direction2D.DOWN)
            expected.set1BitAt(2)
            bitMask shouldBe expected

            tile.addAllowedNeighborsToArray(bitMask, Direction2D.LEFT)
            expected.set1BitAt(3)
            bitMask shouldBe expected
        }
    }
}