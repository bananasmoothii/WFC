package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Direction

interface ConnectionBasedTile<C: Rotatable<D>, D: Dimension<D>>: Tile<C, D> {
    /**
     * Adds a connection to this piece. The user is responsible for managing connections.
     *
     * **Warning**: if there are no connection in the given direction, the tile can connect to other tile that have no
     * connection in the given direction. This means that "no connection" is a valid connection.
     */
    fun addConnection(connection: Int, direction: Direction<D>)
}