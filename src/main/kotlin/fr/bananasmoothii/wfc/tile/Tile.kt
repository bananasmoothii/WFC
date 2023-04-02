package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned
import fr.bananasmoothii.wfc.space.Direction

interface Tile<C: Rotatable<D>, D: Dimension<D>>: Dimensioned<D> {
    val id: Int
    val content: C
    val belongingSet: AbstractTileSet<C, D>?

    /**
     * Returns true if this piece accepts the neighbor in the given direction.
     */
    fun accepts(direction: Direction<D>, neighborId: Int): Boolean

    fun addAllowedNeighborsToArray(bitMask: LongArray, direction: Direction<D>)
}