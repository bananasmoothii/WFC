package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.d2.D2
import fr.bananasmoothii.wfc.space.d2.Dimension2D
import fr.bananasmoothii.wfc.space.d2.Direction2D
import fr.bananasmoothii.wfc.tile.Rotatable
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TileSetTest {

    data class Arrow2D(val direction: Direction2D) : Rotatable<Dimension2D>, D2 {
        override fun generateRotations(): Iterable<Rotatable<Dimension2D>> = Direction2D.values.map { Arrow2D(it) }
    }

    private val tileSet = TileSet<Arrow2D, Dimension2D>()

    @Test
    @Order(1)
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
    fun createOrGetTile() {
        tileSet.createOrGetTile(Arrow2D(Direction2D.UP)) shouldBe tileSet.fromId(0)
    }
}