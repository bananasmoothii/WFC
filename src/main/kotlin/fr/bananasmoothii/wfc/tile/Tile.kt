package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned
import fr.bananasmoothii.wfc.space.Direction

interface Tile<C: Rotatable<C, D>, D: Dimension<D>>: Dimensioned<D> {
    val id: Int
    val content: C
    val belongingSet: AbstractTileSet<C, D>?

    /**
     * Adds an accepted neighbor to this piece.
     */
    fun accept(direction: Direction<D>, neighborId: Int)

    /**
     * Returns true if this piece accepts the neighbor in the given direction.
     */
    fun accepts(direction: Direction<D>, neighborId: Int): Boolean

    /*
     * Returns a mask of the accepted neighbors in the given direction.
     * Warning: for performance reasons, the returned array is the internal array of the tile, so don't modify it.
     *
    fun getNeighborMask(direction: Direction<D>): LongArray

     */

    fun addAllowedNeighborsToArray(bitMask: LongArray, direction: Direction<D>)

    /*
     * Applies the neighbor mask to the given array. Each bit in [currentOptions] will be set to 0 if the corresponding
     * neighbor is not accepted.
     * Example implementation:
     * ```
     * for (i in currentOptions.indices) {
     *     currentOptions[i] = currentOptions[i] and mask[i]
     * }
     * ```
     * @return true if anything changed
     *
    fun applyNeighborMask(direction: Direction<D>, currentOptions: LongArray): Boolean

     */
}