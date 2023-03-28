package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.space.d3.Direction3D.*

data class Coords3D(val x: Int, val y: Int, val z: Int) : Coords<Dimension3D>, D3 {

    /**
     * Moves to a new position one unit in the given direction.
     * Warning: this uses a minecraft-like coordinate system, where [NORTH] is negative z,
     * [EAST] is positive x, and [UP] is positive y.
     */
    override fun move(direction: Direction<Dimension3D>): Coords3D {
        return when (direction) {
            NORTH -> copy(z = z - 1)
            SOUTH -> copy(z = z + 1)
            EAST -> copy(x = x + 1)
            WEST -> copy(x = x - 1)
            UP -> copy(y = y + 1)
            DOWN -> copy(y = y - 1)
            else -> throw IllegalArgumentException("Direction $direction is not a Direction3D")
        }
    }

    override fun toString(): String = "($x, $y, $z)"
}