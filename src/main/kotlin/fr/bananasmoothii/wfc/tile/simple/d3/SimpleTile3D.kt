package fr.bananasmoothii.wfc.tile.simple.d3

import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.space.d3.Dimension3D
import fr.bananasmoothii.wfc.space.d3.Direction3D
import fr.bananasmoothii.wfc.space.d3.Direction3D.*
import fr.bananasmoothii.wfc.tile.TileSet
import fr.bananasmoothii.wfc.tile.d3.Rotatable3D
import fr.bananasmoothii.wfc.tile.simple.SimpleTile
import fr.bananasmoothii.wfc.tile.simple.SimpleTileSet
import fr.bananasmoothii.wfc.tile.simple.UnfinishedTileSetException
import fr.bananasmoothii.wfc.util.getBitAt
import fr.bananasmoothii.wfc.util.set1BitAt

/**
 * A piece in a [TileSet].
 * Instantiate with [SimpleTileSet3D.createOrGetTile].
 * @param C the type of the content of the piece
 */
open class SimpleTile3D<C : Rotatable3D> internal constructor(
    id: Int,
    content: C,
    belongingSet: SimpleTileSet<C, Dimension3D>? = null
) : SimpleTile<C, Dimension3D>(id, content, belongingSet), Rotatable3D by content {

    lateinit var northPieceMask: LongArray private set
    lateinit var eastPieceMask: LongArray private set
    lateinit var southPieceMask: LongArray private set
    lateinit var westPieceMask: LongArray private set
    lateinit var upPieceMask: LongArray private set
    lateinit var downPieceMask: LongArray private set

    private var actionsAfterFinishCreation: MutableList<() -> Unit>? = mutableListOf()

    override fun accept(direction: Direction<Dimension3D>, neighborId: Int) {
        if (belongingSet?.canCreateNewPieces != false) {
            actionsAfterFinishCreation!!.add { accept(direction, neighborId) }
            return
        }
        getArray(direction as Direction3D).set1BitAt(neighborId)
    }

    /**
     * Returns true if this piece accepts the neighbor in the given direction.
     */
    override fun accepts(direction: Direction<Dimension3D>, neighborId: Int): Boolean =
        getArray(direction).getBitAt(neighborId)

    /**
     * Warning: this exposes the internal array. Don't modify it.
     */
    override fun getNeighborMask(direction: Direction<Dimension3D>): LongArray {
        return getArray(direction as Direction3D)
    }

    fun applyNeighborMask(direction: Direction<Dimension3D>, currentOptions: LongArray): Boolean {
        var changed = false
        val array = getArray(direction)
        for (i in currentOptions.indices) {
            val longBefore = currentOptions[i]
            val newLong = currentOptions[i] and array[i]
            if (longBefore != newLong) {
                changed = true
                currentOptions[i] = newLong
            }
        }
        return changed
    }

    private fun getArray(direction: Direction<Dimension3D>): LongArray {
        if (belongingSet?.canCreateNewPieces != false) throw UnfinishedTileSetException()
        if (direction !is Direction3D) throw IllegalArgumentException("direction must be a Direction3D")

        return when (direction) {
            NORTH -> northPieceMask
            EAST -> eastPieceMask
            SOUTH -> southPieceMask
            WEST -> westPieceMask
            UP -> upPieceMask
            DOWN -> downPieceMask
        }
    }

    override fun toString() = "Piece $id"
}