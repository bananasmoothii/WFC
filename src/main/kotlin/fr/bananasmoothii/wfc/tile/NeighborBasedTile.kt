package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Direction

interface NeighborBasedTile<C: Rotatable<D>, D: Dimension<D>>: Tile<C, D> {
    /**
     * Adds an accepted neighbor to this piece.
     */
    fun accept(direction: Direction<D>, neighborId: Int)

    /**
     * Adds an accepted neighbor to this piece.
     */
    fun accept(direction: Direction<D>, neighbor: Tile<C, D>) = accept(direction, neighbor.id)

    /**
     * Adds accepted neighbors to this piece.
     */
    fun accept(direction: Direction<D>, vararg neighbor: Tile<C, D>) {
        neighbor.forEach {
            accept(direction, it.id)
        }
    }
}